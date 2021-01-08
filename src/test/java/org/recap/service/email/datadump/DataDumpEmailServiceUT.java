package org.recap.service.email.datadump;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.model.ILSConfigProperties;
import org.recap.model.export.DataDumpRequest;
import org.recap.util.PropertyUtil;
import org.recap.util.datadump.DataDumpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class DataDumpEmailServiceUT extends BaseTestCaseUT {

    @InjectMocks
    DataDumpEmailService dataDumpEmailService;

    @Mock
    PropertyUtil propertyUtil;

    @Mock
    ProducerTemplate producer;

    @Mock
    DataDumpUtil dataDumpUtil;

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
    public void testsendEmailForFull() {
        dataDumpEmailService.sendEmail(institutionCodes(), 1, 0, "2", "2016-09-02 12:00", "peri.subrahmanya@gmail.com", "dataNotAvailable", 0, "10", "NYPL",Arrays.asList("RECAP"));
        assertTrue(true);
    }

    @Test
    public void testsendEmailForDeleted() {
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(getIlsConfigProperties());
        dataDumpEmailService.sendEmail(institutionCodes(), 1, 0, "0", "2016-09-02 12:00", "peri.subrahmanya@gmail.com", "dataNotAvailable", 0, "2", "NYPL", Arrays.asList("RECAP"));
        assertTrue(true);
    }

    @Test
    public void testsendEmailCae() {
        ReflectionTestUtils.setField(dataDumpEmailService, "ftpDataDumpDirectory", " ");
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(getIlsConfigProperties());
        Mockito.doNothing().when(producer).sendBodyAndHeader(Mockito.any(),Mockito.anyString(),Mockito.any());
        dataDumpEmailService.sendEmail(institutionCodes(), 1, 0, "0", "2016-09-02 12:00", "peri.subrahmanya@gmail.com", "dataNotAvailable", 0, "2", "NYPL",Arrays.asList("RECAP"));
        assertTrue(true);
    }
    @Test
    public void testsendEmailForIncremental() {
        ILSConfigProperties ilsConfigProperties = getIlsConfigProperties();
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        dataDumpEmailService.sendEmail(institutionCodes(), 1, 0, "2", "2016-09-02 12:00", "peri.subrahmanya@gmail.com", "dataNotAvailable", 0, "1", "NYPL",Arrays.asList("RECAP"));
        assertTrue(true);
    }

    @Test
    public void sendEmailForDumpNotification() {
        DataDumpRequest[] dataDumpRequests={getDataDumpRequest(RecapConstants.DATADUMP_FETCHTYPE_FULL, RecapConstants.DATADUMP_XML_FORMAT_MARC, RecapConstants.DATADUMP_TRANSMISSION_TYPE_FTP),getDataDumpRequest(RecapConstants.DATADUMP_FETCHTYPE_INCREMENTAL, RecapConstants.DATADUMP_XML_FORMAT_SCSB, RecapConstants.DATADUMP_TRANSMISSION_TYPE_HTTP),getDataDumpRequest(RecapConstants.DATADUMP_FETCHTYPE_DELETED, RecapConstants.DATADUMP_DELETED_JSON_FORMAT, RecapConstants.DATADUMP_TRANSMISSION_TYPE_FILESYSTEM),getDataDumpRequest("Export", RecapConstants.DATADUMP_DELETED_JSON_FORMAT, RecapConstants.DATADUMP_TRANSMISSION_TYPE_FILESYSTEM)};
        for (DataDumpRequest dataDumpRequest:dataDumpRequests) {
            Mockito.when(dataDumpUtil.getFetchType(Mockito.anyString())).thenCallRealMethod();
            Mockito.when(dataDumpUtil.getOutputformat(Mockito.anyString())).thenCallRealMethod();
            Mockito.when(dataDumpUtil.getTransmissionType(Mockito.anyString())).thenCallRealMethod();
            dataDumpEmailService.sendEmailForDumpNotification(dataDumpRequest);
            assertTrue(true);
        }
    }

    private ILSConfigProperties getIlsConfigProperties() {
        ILSConfigProperties ilsConfigProperties = new ILSConfigProperties();
        ilsConfigProperties.setEmailDataDumpCc("test");
        return ilsConfigProperties;
    }

    private List<String> institutionCodes() {
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        return institutionCodes;
    }


    private DataDumpRequest getDataDumpRequest(String fetchType, String outputFileFormat, String transmissionType) {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setIncrementalSequence(false);
        dataDumpRequest.setFetchType(fetchType);
        dataDumpRequest.setOutputFileFormat(outputFileFormat);
        dataDumpRequest.setTransmissionType(transmissionType);
        dataDumpRequest.setToEmailAddress("test@htcindia.com");
        return dataDumpRequest;
    }
}
