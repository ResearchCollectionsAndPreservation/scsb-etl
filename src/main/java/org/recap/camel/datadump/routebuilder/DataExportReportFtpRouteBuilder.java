package org.recap.camel.datadump.routebuilder;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
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

/**
 * Created by premkb on 01/10/16.
 */
@Component
public class DataExportReportFtpRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(DataExportReportFtpRouteBuilder.class);

    /**
     * Instantiates a new Data export report ftp route builder.
     *
     * @param context                      the context
     * @param s3OnlyReportRemoteServer     the ftp only report remote server
     * @param s3DumpWithReportRemoteServer the ftp dump with report remote server
     */
    @Autowired
    public DataExportReportFtpRouteBuilder(CamelContext context, @Value("${s3.add.s3.routes.on.startup}") boolean addS3RoutesOnStartup, @Value("${s3.datadump.report.remote.server}") String s3OnlyReportRemoteServer,
                                           @Value("${s3.data.dump.dir}") String s3DumpWithReportRemoteServer) {
        try {
            if (addS3RoutesOnStartup) {
                context.addRoutes(new RouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        from(RecapConstants.DATADUMP_SUCCESS_REPORT_FTP_Q)
                                .routeId(RecapConstants.DATADUMP_SUCCESS_REPORT_FTP_ROUTE_ID)
                                .process(new FileNameProcessorForDataDumpSuccess())
                                .marshal().bindy(BindyType.Csv, DataDumpSuccessReport.class)
                                .setHeader(S3Constants.KEY, simple(s3OnlyReportRemoteServer + "${in.header.directoryName}/${in.header.fileName}-${in.header.reportType}-${date:now:ddMMMyyyy}.csv"))
                                .to(RecapConstants.SCSB_CAMEL_S3_TO_ENDPOINT);
                    }
                });

                context.addRoutes(new RouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        from(RecapConstants.DATADUMP_FAILURE_REPORT_FTP_Q)
                                .routeId(RecapConstants.DATADUMP_FAILURE_REPORT_FTP_ROUTE_ID)
                                .process(new FileNameProcessorForDataDumpFailure())
                                .marshal().bindy(BindyType.Csv, DataDumpFailureReport.class)
                                .setHeader(S3Constants.KEY, simple(s3OnlyReportRemoteServer + "${in.header.directoryName}/${in.header.fileName}-${in.header.reportType}-${date:now:ddMMMyyyy}.csv"))
                                .to(RecapConstants.SCSB_CAMEL_S3_TO_ENDPOINT);
                    }
                });

                context.addRoutes(new RouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        from(RecapConstants.DATAEXPORT_WITH_SUCCESS_REPORT_FTP_Q)
                                .routeId(RecapConstants.DATAEXPORT_WITH_SUCCESS_REPORT_FTP_ROUTE_ID)
                                .process(new FileNameProcessorForDataDumpSuccess())
                                .marshal().bindy(BindyType.Csv, DataDumpSuccessReport.class)
                                .setHeader(S3Constants.KEY, simple(s3DumpWithReportRemoteServer + "${in.header.fileName}.csv"))
                                .to(RecapConstants.SCSB_CAMEL_S3_TO_ENDPOINT);
                    }
                });

                context.addRoutes(new RouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        from(RecapConstants.DATAEXPORT_WITH_FAILURE_REPORT_FTP_Q)
                                .routeId(RecapConstants.DATAEXPORT_WITH_FAILURE_REPORT_FTP_ROUTE_ID)
                                .process(new FileNameProcessorForDataDumpFailure())
                                .marshal().bindy(BindyType.Csv, DataDumpFailureReport.class)
                                .setHeader(S3Constants.KEY, simple(s3DumpWithReportRemoteServer + "${in.header.fileName}.csv"))
                                .to(RecapConstants.SCSB_CAMEL_S3_TO_ENDPOINT);
                    }
                });
            }
        } catch (Exception e) {
            logger.error(RecapConstants.ERROR, e);
        }
    }
}
