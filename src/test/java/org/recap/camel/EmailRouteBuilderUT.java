package org.recap.camel;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by chenchulakshmig on 15/9/16.
 */
public class EmailRouteBuilderUT extends BaseTestCase {

    @Autowired
    private ProducerTemplate producer;

    @Value("${data.dump.email.nypl.to}")
    private String dataDumpEmailNyplTo;

    @Value("${etl.data.dump.directory}")
    String fileSystemDataDumpDirectory;

    @Value("${s3.data.dump.dir}")
    String ftpDataDumpDirectory;

    @Test
    public void testEmail() throws Exception {
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        emailPayLoad.setInstitutions(Arrays.asList("PUL", "CUL"));
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyyyy");
        String day = sdf.format(date);
        String  location = "File System - " + fileSystemDataDumpDirectory + "/" + "NYPL" + File.separator + day;
        emailPayLoad.setLocation(location);
        emailPayLoad.setCount(100);
        emailPayLoad.setTo(dataDumpEmailNyplTo);
        producer.sendBody(RecapConstants.EMAIL_Q, emailPayLoad);
    }

}