package org.recap.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.model.dataformat.BindyType;
import org.recap.RecapConstants;
import org.recap.model.csv.ReCAPCSVFailureRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by peris on 8/16/16.
 */
@Component
public class S3FailureReportRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(S3FailureReportRouteBuilder.class);

    /**
     * Instantiates a new Ftp failure report route builder.
     *
     * @param context         the context
     * @param s3EtlReportsDir the s3 etl remote server
     */
    @Autowired
    public S3FailureReportRouteBuilder(CamelContext context, @Value("${s3.etl.reports.dir}") String s3EtlReportsDir) {

        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.FTP_SUCCESS_Q)
                            .routeId(RecapConstants.FTP_SUCCESS_ROUTE_ID)
                            .process(new FileNameProcessorForFailureRecord())
                            .marshal().bindy(BindyType.Csv, ReCAPCSVFailureRecord.class)
                            .setHeader(S3Constants.KEY, simple(s3EtlReportsDir+"${in.header.directoryName}/${in.header.fileName}-${in.header.reportType}-${date:now:ddMMMyyyy}.csv"))
                            .to(RecapConstants.SCSB_CAMEL_S3_TO_ENDPOINT);
                }
            });
        } catch (Exception e) {
            logger.error(RecapConstants.ERROR,e);
        }
    }
}
