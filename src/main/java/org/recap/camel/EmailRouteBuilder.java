package org.recap.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.io.FileUtils;
import org.recap.RecapConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Created by chenchulakshmig on 13/9/16.
 */
@Component
public class EmailRouteBuilder {
    private static final Logger logger = LoggerFactory.getLogger(EmailRouteBuilder.class);

    private String emailBody;
    private String emailBodyForNoData;
    private String emailPassword;
    private static final String SUBJECT = "subject";
    private static final String EMAIL_PAYLOAD_TO = "${header.emailPayLoad.to}";
    private static final String EMAIL_PAYLOAD_CC = "${header.emailPayLoad.cc}";
    private static final String SMTPS = "smtps://";
    private static final String USERNAME = "?username=";
    private static final String PASSWORD = "&password=";
    private static final String EMAIL_BODY_FOR = "emailBodyFor";

    /**
     * Instantiates a new Email route builder.
     *
     * @param context           the context
     * @param username          the username
     * @param passwordDirectory the password directory
     * @param from              the from
     * @param subject           the subject
     * @param noDataSubject     the no data subject
     * @param smtpServer        the smtp server
     */
    @Autowired
    public EmailRouteBuilder(CamelContext context, @Value("${data.dump.email.username}") String username, @Value("${data.dump.email.password.file}") String passwordDirectory,
                             @Value("${data.dump.email.from}") String from, @Value("${data.dump.email.subject}") String subject,@Value("${data.dump.email.nodata.subject}") String noDataSubject,
                             @Value("${smtpServer}") String smtpServer) {
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    loadEmailBodyTemplate();
                    loadEmailBodyTemplateForNoData();
                    loadEmailPassword();

                    from(RecapConstants.EMAIL_Q)
                            .routeId(RecapConstants.EMAIL_ROUTE_ID)
                            .setHeader("emailPayLoad").body(EmailPayLoad.class)
                            .onCompletion().log("Email has been sent successfully.")
                            .end()
                                .choice()
                                    .when(header(RecapConstants.DATADUMP_EMAILBODY_FOR).isEqualTo(RecapConstants.DATADUMP_DATA_AVAILABLE))
                                        .setHeader(SUBJECT, simple(subject))
                                        .setBody(simple(emailBody))
                                        .setHeader("from", simple(from))
                                        .setHeader("to", simple(EMAIL_PAYLOAD_TO))
                                        .log("email body for data available")
                                        .to(SMTPS + smtpServer + USERNAME + username + PASSWORD + emailPassword)
                                    .when(header(EMAIL_BODY_FOR).isEqualTo(RecapConstants.DATADUMP_NO_DATA_AVAILABLE))
                                        .setHeader(SUBJECT, simple(noDataSubject))
                                        .setBody(simple(emailBodyForNoData))
                                        .setHeader("from", simple(from))
                                        .setHeader("to", simple(EMAIL_PAYLOAD_TO))
                                        .setHeader("cc", simple(EMAIL_PAYLOAD_CC))
                                        .log("email body for no data available")
                                        .to(SMTPS + smtpServer + USERNAME + username + PASSWORD + emailPassword)
                                    .when(header(EMAIL_BODY_FOR).isEqualTo(RecapConstants.EMAIL_INCREMENTAL_DATA_DUMP))
                                        .setHeader(SUBJECT, simple("${header.emailPayLoad.subject}"))
                                        .setBody(simple("The report is available in the ${header.emailPayLoad.location}"))
                                        .setHeader("from", simple(from))
                                        .setHeader("to", simple(EMAIL_PAYLOAD_TO))
                                        .setHeader("cc", simple(EMAIL_PAYLOAD_CC))
                                        .log("Email sent for Incremental DataDump")
                                        .to(SMTPS + smtpServer + USERNAME + username + PASSWORD + emailPassword)
                                    .when(header(EMAIL_BODY_FOR).isEqualTo(RecapConstants.EMAIL_DELETION_DATA_DUMP))
                                        .setHeader(SUBJECT, simple("${header.emailPayLoad.subject}"))
                                        .setBody(simple("The report is available in the ${header.emailPayLoad.location}"))
                                        .setHeader("from", simple(from))
                                        .setHeader("to", simple(EMAIL_PAYLOAD_TO))
                                        .setHeader("cc", simple(EMAIL_PAYLOAD_CC))
                                        .log("Email sent for Deletion data dump")
                                        .to(SMTPS + smtpServer + USERNAME + username + PASSWORD + emailPassword)
                            ;
                }

                private void loadEmailBodyTemplate() {
                    InputStream inputStream = getClass().getResourceAsStream("email_body.vm");
                    StringBuilder out = emailBodyTemplate(inputStream);
                    emailBody = out.toString();
                }

                private void loadEmailBodyTemplateForNoData() {
                    InputStream inputStream = getClass().getResourceAsStream("no_data_email_body.vm");
                    StringBuilder out = emailBodyTemplate(inputStream);
                    emailBodyForNoData = out.toString();
                }

                private void loadEmailPassword() {
                    File file = new File(passwordDirectory);
                    if (file.exists()) {
                        try {
                            emailPassword = FileUtils.readFileToString(file, StandardCharsets.UTF_8).trim();
                        } catch (IOException e) {
                            logger.error(RecapConstants.ERROR,e);
                        }
                    }
                }
            });
        } catch (Exception e) {
            logger.error(RecapConstants.ERROR,e);
        }
    }

    private StringBuilder emailBodyTemplate(InputStream inputStream) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder out = new StringBuilder();
                    String line;
                    try {
                        while ((line = reader.readLine()) != null) {
                            if (line.isEmpty()) {
                                out.append("\n");
                            } else {
                                out.append(line);
                                out.append("\n");
                            }
                        }
                    } catch (IOException e) {
                        logger.error(RecapConstants.ERROR,e);
                    }
        return out;
    }

}
