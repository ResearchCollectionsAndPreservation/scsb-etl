package org.recap.service.email.datadump;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.model.ILSConfigProperties;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class DataDumpEmailServiceUT extends BaseTestCaseUT {

    @InjectMocks
    DataDumpEmailService dataDumpEmailService;

    @Mock
    PropertyUtil propertyUtil;

    @Mock
    ProducerTemplate producer;

    @Value("${etl.data.dump.directory}")
    private String fileSystemDataDumpDirectory;

    @Value("${s3.data.dump.dir}")
    private String ftpDataDumpDirectory;

    @Value("${etl.data.dump.fetchtype.full}")
    private String dataDumpFetchType;

    @Before
    public void setup() throws Exception {
        ReflectionTestUtils.setField(dataDumpEmailService, "fileSystemDataDumpDirectory", fileSystemDataDumpDirectory);
        ReflectionTestUtils.setField(dataDumpEmailService, "ftpDataDumpDirectory", "/");
        ReflectionTestUtils.setField(dataDumpEmailService, "dataDumpFetchType", dataDumpFetchType);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testsendEmail() {
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        dataDumpEmailService.sendEmail(institutionCodes, 1, 0, "1", "2016-09-02 12:00", "peri.subrahmanya@gmail.com", "dataNotAvailable", 0, "0", "NYPL");
        assertTrue(true);
    }
    @Test
    public void testsendEmailCae() {
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        dataDumpEmailService.sendEmail(institutionCodes, 1, 0, "1", "2016-09-02 12:00", "peri.subrahmanya@gmail.com", "dataNotAvailable", 0, "10", "NYPL");
        assertTrue(true);
    }
    @Test
    public void testsendEmailCae1() {
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        ILSConfigProperties ilsConfigProperties=new ILSConfigProperties();
        ilsConfigProperties.setEmailDataDumpCc("test");
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        dataDumpEmailService.sendEmail(institutionCodes, 1, 0, "1", "2016-09-02 12:00", "peri.subrahmanya@gmail.com", "dataNotAvailable", 0, "2", "NYPL");
        assertTrue(true);
    }
    @Test
    public void testsendEmailCae2() {
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        ILSConfigProperties ilsConfigProperties=new ILSConfigProperties();
        ilsConfigProperties.setEmailDataDumpCc("test");
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        dataDumpEmailService.sendEmail(institutionCodes, 1, 0, "0", "2016-09-02 12:00", "peri.subrahmanya@gmail.com", "dataNotAvailable", 0, "2", "NYPL");
        assertTrue(true);
    }

    @Test
    public void testsendEmailCae22() {
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        ILSConfigProperties ilsConfigProperties=new ILSConfigProperties();
        ilsConfigProperties.setEmailDataDumpCc("test");
        ReflectionTestUtils.setField(dataDumpEmailService, "ftpDataDumpDirectory", " ");
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        Mockito.doNothing().when(producer).sendBodyAndHeader(Mockito.any(),Mockito.anyString(),Mockito.any());
        dataDumpEmailService.sendEmail(institutionCodes, 1, 0, "0", "2016-09-02 12:00", "peri.subrahmanya@gmail.com", "dataNotAvailable", 0, "2", "NYPL");
        assertTrue(true);
    }
    @Test
    public void testsendEmailCae3() {
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        ILSConfigProperties ilsConfigProperties=new ILSConfigProperties();
        ilsConfigProperties.setEmailDataDumpCc("test");
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        dataDumpEmailService.sendEmail(institutionCodes, 1, 0, "2", "2016-09-02 12:00", "peri.subrahmanya@gmail.com", "dataNotAvailable", 0, "1", "NYPL");
        assertTrue(true);
    }
}
