package org.recap.camel;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.io.FileUtils;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbConstants;
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
@Slf4j
@Component
public class EmailRouteBuilder {


    private String emailBody;
    private String emailForExportNotification;
    private String emailBodyForNoData;
    private String emailPassword;
    private static final String SUBJECT = "subject";
    private static final String EMAIL_PAYLOAD_TO = "${header.emailPayLoad.to}";
    private static final String EMAIL_PAYLOAD_CC = "${header.emailPayLoad.cc}";
    private static final String EMAIL_PAYLOAD_SUBJECT = "${header.emailPayLoad.subject}";
    private static final String SMTPS = "smtps://";
    private static final String USERNAME = "?username=";
    private static final String PASSWORD = "&password=";
    private static final String EMAIL_BODY_FOR = "emailBodyFor";
    private String emailPayLoadMessage = "${header.emailPayLoad.message}";

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
    public EmailRouteBuilder(CamelContext context, @Value("${" + PropertyKeyConstants.EMAIL_SMTP_SERVER_USERNAME + "}") String username, @Value("${" + PropertyKeyConstants.EMAIL_SMTP_SERVER_PASSWORD_FILE + "}") String passwordDirectory,
                             @Value("${" + PropertyKeyConstants.EMAIL_DATA_DUMP_FROM + "}") String from, @Value("${" + PropertyKeyConstants.EMAIL_DATA_DUMP_SUBJECT + "}") String subject,@Value("${" + PropertyKeyConstants.EMAIL_DATA_DUMP_NODATA_SUBJECT + "}") String noDataSubject,
                             @Value("${" + PropertyKeyConstants.EMAIL_SMTP_SERVER + "}") String smtpServer,@Value("${" + PropertyKeyConstants.ILS.ILS_EMAIL_DATA_DUMP_CC + "}") String emailCC) {
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    loadNotificationEmailBodyTemplate();
                    loadEmailBodyTemplate();
                    loadEmailBodyTemplateForNoData();
                    loadEmailPassword();

                    from(ScsbConstants.EMAIL_Q)
                            .routeId(ScsbConstants.EMAIL_ROUTE_ID)
                            .setHeader("emailPayLoad").body(EmailPayLoad.class)
                            .onCompletion().log("Email has been sent successfully.")
                            .end()
                                .choice()
                                    .when(header(ScsbConstants.DATADUMP_EMAILBODY_FOR).isEqualTo(ScsbConstants.DATADUMP_EXPORT_NOTIFICATION))
                                        .setHeader("from", simple(from))
                                        .setHeader("to", simple(EMAIL_PAYLOAD_TO))
                                        .setHeader("cc", simple(EMAIL_PAYLOAD_CC))
                                        .setHeader(SUBJECT, simple(EMAIL_PAYLOAD_SUBJECT))
                                        .setBody(simple(emailForExportNotification))
                                        .log("Sending email notification for start of data dump process")
                                        .to(SMTPS + smtpServer + USERNAME + username + PASSWORD + emailPassword)
                                    .when(header(ScsbConstants.DATADUMP_EMAILBODY_FOR).isEqualTo(ScsbConstants.DATADUMP_DATA_AVAILABLE))
                                        .setHeader(SUBJECT, simple(subject))
                                        .setBody(simple(emailBody))
                                        .setHeader("from", simple(from))
                                        .setHeader("to", simple(EMAIL_PAYLOAD_TO))
                                        .setHeader("cc", simple(emailCC))
                                        .log("Sending email for data available")
                                        .to(SMTPS + smtpServer + USERNAME + username + PASSWORD + emailPassword)
                                    .when(header(EMAIL_BODY_FOR).isEqualTo(ScsbConstants.DATADUMP_NO_DATA_AVAILABLE))
                                        .setHeader(SUBJECT, simple(EMAIL_PAYLOAD_SUBJECT))
                                        .setBody(simple(emailBodyForNoData))
                                        .setHeader("from", simple(from))
                                        .setHeader("to", simple(EMAIL_PAYLOAD_TO))
                                        .setHeader("cc", simple(EMAIL_PAYLOAD_CC))
                                        .log("Sending email for no data available")
                                        .to(SMTPS + smtpServer + USERNAME + username + PASSWORD + emailPassword)
                                    .when(header(EMAIL_BODY_FOR).isEqualTo(ScsbConstants.EMAIL_INCREMENTAL_DATA_DUMP))
                                        .setHeader(SUBJECT, simple("${header.emailPayLoad.subject}"))
                                        .setBody(simple("The report is available in the ${header.emailPayLoad.location}"))
                                        .setHeader("from", simple(from))
                                        .setHeader("to", simple(EMAIL_PAYLOAD_TO))
                                        .setHeader("cc", simple(EMAIL_PAYLOAD_CC))
                                        .log("Email sent for Incremental DataDump")
                                        .to(SMTPS + smtpServer + USERNAME + username + PASSWORD + emailPassword)
                                    .when(header(EMAIL_BODY_FOR).isEqualTo(ScsbConstants.EMAIL_DELETION_DATA_DUMP))
                                        .setHeader(SUBJECT, simple("${header.emailPayLoad.subject}"))
                                        .setBody(simple("The report is available in the ${header.emailPayLoad.location}"))
                                        .setHeader("from", simple(from))
                                        .setHeader("to", simple(EMAIL_PAYLOAD_TO))
                                        .setHeader("cc", simple(EMAIL_PAYLOAD_CC))
                                        .log("Email sent for Deletion data dump")
                                        .to(SMTPS + smtpServer + USERNAME + username + PASSWORD + emailPassword)
                                    .when(header(EMAIL_BODY_FOR).isEqualTo(ScsbConstants.EMAIL_REQUEST_LOG))
                                        .setHeader(SUBJECT, simple(ScsbConstants.GATEWAY_REQUESTS_LOG_SUBJECT))
                                        .setBody(simple(emailPayLoadMessage))
                                        .setHeader("from", simple(from))
                                        .setHeader("to", simple(EMAIL_PAYLOAD_TO))
                                        .setHeader("cc", simple(EMAIL_PAYLOAD_CC))
                                        .log("email sent for gateway requests log")
                                        .to(SMTPS + smtpServer + USERNAME + username + PASSWORD + emailPassword);
                }

                private void loadEmailBodyTemplate() {
                    InputStream inputStream = getClass().getResourceAsStream("email_body.vm");
                    StringBuilder out = emailBodyTemplate(inputStream);
                    emailBody = out.toString();
                }

                private void loadNotificationEmailBodyTemplate() {
                    InputStream inputStream = getClass().getResourceAsStream("email_for_export_notification.vm");
                    StringBuilder out = emailBodyTemplate(inputStream);
                    emailForExportNotification = out.toString();
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
                            log.error(ScsbConstants.ERROR,e);
                        }
                    }
                }
            });
        } catch (Exception e) {
            log.error(ScsbConstants.ERROR,e);
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
                        log.error(ScsbConstants.ERROR,e);
                    }
        return out;
    }

}
