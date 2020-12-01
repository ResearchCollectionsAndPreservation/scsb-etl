package org.recap.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
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
public class FtpFailureReportRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(FtpFailureReportRouteBuilder.class);

    /**
     * Instantiates a new Ftp failure report route builder.
     *
     * @param context         the context
     * @param ftpUserName     the ftp user name
     * @param ftpRemoteServer the ftp remote server
     * @param ftpKnownHost    the ftp known host
     * @param ftpPrivateKey   the ftp private key
     */
    @Autowired
    public FtpFailureReportRouteBuilder(CamelContext context,
                                        @Value("${ftp.server.userName}") String ftpUserName, @Value("${ftp.etl.remote.server}") String ftpRemoteServer,
                                        @Value("${ftp.server.knownHost}") String ftpKnownHost, @Value("${ftp.server.privateKey}") String ftpPrivateKey) {

        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.FTP_SUCCESS_Q)
                            .routeId(RecapConstants.FTP_SUCCESS_ROUTE_ID)
                            .process(new FileNameProcessorForFailureRecord())
                            .marshal().bindy(BindyType.Csv, ReCAPCSVFailureRecord.class)
                            .to("sftp://" + ftpUserName + "@" + ftpRemoteServer + "?privateKeyFile=" + ftpPrivateKey + "&knownHostsFile=" + ftpKnownHost + "&fileName=${in.header.directoryName}/${in.header.fileName}-${in.header.reportType}-${date:now:ddMMMyyyy}.csv&fileExist=append");
                }
            });
        } catch (Exception e) {
            logger.error(RecapConstants.ERROR,e);
        }
    }
}
