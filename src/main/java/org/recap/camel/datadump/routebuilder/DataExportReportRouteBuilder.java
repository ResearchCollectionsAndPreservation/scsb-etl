package org.recap.camel.datadump.routebuilder;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.model.dataformat.BindyType;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.camel.datadump.consumer.DataExportReportActiveMQConsumer;
import org.recap.model.csv.DataExportFailureReport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by peris on 11/12/16.
 */
@Slf4j
@Component
public class DataExportReportRouteBuilder {

    /**
     * Instantiates a new Data export report route builder.
     *
     * @param camelContext the camel context
     */
    @Autowired
    private DataExportReportRouteBuilder(@Value("${" + PropertyKeyConstants.S3_ADD_S3_ROUTES_ON_STARTUP + "}") boolean addS3RoutesOnStartup, @Value("${" + PropertyKeyConstants.ETL_EXPORT_S3_FAILUREREPORT_DIRECTORY + "}") String s3FailureReportDirectory, CamelContext camelContext) {
        try {
            if (addS3RoutesOnStartup) {
                camelContext.addRoutes(new RouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        from(ScsbConstants.DATADUMP_SUCCESS_REPORT_Q)
                                .routeId(ScsbConstants.DATADUMP_SUCCESS_REPORT_ROUTE_ID)
                                .bean(DataExportReportActiveMQConsumer.class, "saveSuccessReportEntity");
                    }
                });

                camelContext.addRoutes(new RouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        from(ScsbConstants.DATADUMP_FAILURE_REPORT_Q)
                                .routeId(ScsbConstants.DATADUMP_FAILURE_REPORT_ROUTE_ID)
                                .bean(DataExportReportActiveMQConsumer.class, "saveFailureReportEntity");
                    }
                });

                camelContext.addRoutes(new RouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        from(ScsbConstants.DATADUMP_FAILURE_REPORT_SFTP_Q)
                                .routeId(ScsbConstants.DATADUMP_FAILURE_REPORT_SFTP_ID)
                                .process(new Processor() {
                                    @Override
                                    public void process(Exchange exchange) throws Exception {
                                        List<DataExportFailureReport> dataExportFailureReportList = (List<DataExportFailureReport>) exchange.getIn().getBody();
                                        exchange.getIn().setHeader(ScsbCommonConstants.REPORT_FILE_NAME, dataExportFailureReportList.get(0).getFilename());
                                        exchange.getIn().setHeader(ScsbConstants.REPORT_TYPE, dataExportFailureReportList.get(0).getReportType());
                                        exchange.getIn().setHeader(ScsbConstants.INST_NAME, dataExportFailureReportList.get(0).getRequestingInstitutionCode());
                                    }
                                })
                                .marshal().bindy(BindyType.Csv, DataExportFailureReport.class)
                                .setHeader(S3Constants.KEY, simple(s3FailureReportDirectory + "${in.header.fileName}.csv"))
                                .to(ScsbConstants.SCSB_CAMEL_S3_TO_ENDPOINT);
                    }
                });
            }
        } catch (Exception e) {
            log.error(ScsbConstants.ERROR, e);
        }
    }
}
