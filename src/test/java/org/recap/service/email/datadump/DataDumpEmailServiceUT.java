package org.recap.service.email.datadump;

import org.apache.camel.spi.AsEndpointUri;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class DataDumpEmailServiceUT extends BaseTestCase {
    @Autowired
    DataDumpEmailService dataDumpEmailService;
    @Test
    public void testsendEmail() {
        //DataDumpEmailService dataDumpEmailService = new DataDumpEmailService();
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        dataDumpEmailService.sendEmail(institutionCodes, 1, 0, "1", "2016-09-02 12:00", "peri.subrahmanya@gmail.com", "dataNotAvailable", 0, "0", "NYPL");
        assertTrue(true);
    }
    @Test
    public void testsendEmailCae() {
        //DataDumpEmailService dataDumpEmailService = new DataDumpEmailService();
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        dataDumpEmailService.sendEmail(institutionCodes, 1, 0, "1", "2016-09-02 12:00", "peri.subrahmanya@gmail.com", "dataNotAvailable", 0, "10", "NYPL");
        assertTrue(true);
    }
    @Test
    public void testsendEmailCae1() {
        //DataDumpEmailService dataDumpEmailService = new DataDumpEmailService();
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        dataDumpEmailService.sendEmail(institutionCodes, 1, 0, "1", "2016-09-02 12:00", "peri.subrahmanya@gmail.com", "dataNotAvailable", 0, "2", "NYPL");
        assertTrue(true);
    }
    @Test
    public void testsendEmailCae2() {
        //DataDumpEmailService dataDumpEmailService = new DataDumpEmailService();
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        dataDumpEmailService.sendEmail(institutionCodes, 1, 0, "0", "2016-09-02 12:00", "peri.subrahmanya@gmail.com", "dataNotAvailable", 0, "2", "NYPL");
        assertTrue(true);
    }
}
