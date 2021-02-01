package org.recap.camel.datadump.routebuilder;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.recap.RecapConstants;
import org.recap.camel.datadump.FileNameProcessorForDataDumpFailure;
import org.recap.camel.datadump.FileNameProcessorForDataDumpSuccess;
import org.recap.model.csv.DataDumpFailureReport;
import org.recap.model.csv.DataDumpSuccessReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by peris on 11/12/16.
 */
@Component
public class DataExportCsvReportRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(DataExportCsvReportRouteBuilder.class);

    /**
     * Instantiates a new Data export csv report route builder.
     *
     * @param camelContext     the camel context
     * @param reportsDirectory the reports directory
     */
    @Autowired
    public DataExportCsvReportRouteBuilder(CamelContext camelContext, @Value("${etl.data.dump.report.directory}") String reportsDirectory) {
        try {
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.DATADUMP_SUCCESS_REPORT_CSV_Q)
                            .routeId(RecapConstants.DATADUMP_SUCCESS_REPORT_CSV_ROUTE_ID)
                            .process(new FileNameProcessorForDataDumpSuccess())
                            .marshal().bindy(BindyType.Csv, DataDumpSuccessReport.class)
                            .to("file:" + reportsDirectory + File.separator + "?fileName=${in.header.fileName}-${in.header.reportType}-${date:now:ddMMMyyyy}.csv&fileExist=append");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.DATADUMP_FAILURE_REPORT_CSV_Q)
                            .routeId(RecapConstants.DATADUMP_FAILURE_REPORT_CSV_ROUTE_ID)
                            .process(new FileNameProcessorForDataDumpFailure())
                            .marshal().bindy(BindyType.Csv, DataDumpFailureReport.class)
                            .to("file:" + reportsDirectory + File.separator + "?fileName=${in.header.fileName}-${in.header.reportType}-${date:now:ddMMMyyyy}.csv&fileExist=append");
                }
            });
        } catch (Exception e) {
            logger.error(RecapConstants.ERROR,e);
        }
    }
}
