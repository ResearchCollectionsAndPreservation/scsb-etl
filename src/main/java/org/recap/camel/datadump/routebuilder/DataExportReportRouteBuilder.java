package org.recap.camel.datadump.routebuilder;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.camel.datadump.consumer.DataExportReportActiveMQConsumer;
import org.recap.model.csv.DataExportFailureReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Created by peris on 11/12/16.
 */
@Component
public class DataExportReportRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(DataExportReportRouteBuilder.class);

    private static final String SFTP = "sftp://";
    private static final String PRIVATE_KEY_FILE = "?privateKeyFile=";
    private static final String KNOWN_HOSTS_FILE = "&knownHostsFile=";



    /**
     * Instantiates a new Data export report route builder.
     *
     * @param camelContext the camel context
     */
    @Autowired
    private DataExportReportRouteBuilder(@Value("${ftp.userName}") String ftpUserName,@Value("${ftp.ftpHost}") String ftpHost,
                                         @Value("${ftp.ftpPort}") String ftpPort,@Value("${ftp.privateKey}") String ftpPrivateKey,
                                         @Value("${ftp.knownHost}") String ftpKnownHost,@Value("${etl.export.ftp.failurereport.directory}") String ftpFailureReportDirectory,
                                         CamelContext camelContext) {
        try {
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.DATADUMP_SUCCESS_REPORT_Q)
                            .routeId(RecapConstants.DATADUMP_SUCCESS_REPORT_ROUTE_ID)
                            .bean(DataExportReportActiveMQConsumer.class, "saveSuccessReportEntity");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.DATADUMP_FAILURE_REPORT_Q)
                            .routeId(RecapConstants.DATADUMP_FAILURE_REPORT_ROUTE_ID)
                            .bean(DataExportReportActiveMQConsumer.class, "saveFailureReportEntity");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.DATADUMP_FAILURE_REPORT_SFTP_Q)
                            .routeId(RecapConstants.DATADUMP_FAILURE_REPORT_SFTP_ID)
                            .process(new Processor() {
                                @Override
                                public void process(Exchange exchange) throws Exception {
                                    List<DataExportFailureReport> dataExportFailureReportList = (List<DataExportFailureReport>) exchange.getIn().getBody();
                                    exchange.getIn().setHeader(RecapCommonConstants.REPORT_FILE_NAME, dataExportFailureReportList.get(0).getFilename());
                                    exchange.getIn().setHeader(RecapConstants.REPORT_TYPE, dataExportFailureReportList.get(0).getReportType());
                                    exchange.getIn().setHeader(RecapConstants.INST_NAME,dataExportFailureReportList.get(0).getRequestingInstitutionCode());
                                }
                            })
                            .marshal().bindy(BindyType.Csv, DataExportFailureReport.class)
                            .to(SFTP + ftpUserName + "@" +ftpHost +":" +ftpPort+ftpFailureReportDirectory+ PRIVATE_KEY_FILE + ftpPrivateKey + KNOWN_HOSTS_FILE + ftpKnownHost + "&fileName=${in.header.fileName}.csv&fileExist=append");
                }
            });
        } catch (Exception e) {
            logger.error(RecapConstants.ERROR,e);
        }
    }
}
