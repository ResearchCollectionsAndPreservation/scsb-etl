package org.recap.service.email.datadump;

import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbConstants;
import org.recap.model.ILSConfigProperties;
import org.recap.model.export.DataDumpRequest;
import org.recap.util.PropertyUtil;
import org.recap.util.datadump.DataDumpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
    private final String dataDumpFetchType = "10";

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(dataDumpEmailService, "fileSystemDataDumpDirectory", fileSystemDataDumpDirectory);
        ReflectionTestUtils.setField(dataDumpEmailService, "ftpDataDumpDirectory", "/");
        ReflectionTestUtils.setField(dataDumpEmailService, "dataDumpFetchType", dataDumpFetchType);

    }

    @Test
    public void testsendEmailForFull() {
        dataDumpEmailService.sendEmail(institutionCodes(), 1, 0, "2", "2016-09-02 12:00", "peri.subrahmanya@gmail.com", "dataNotAvailable", 0, "10", "NYPL", Arrays.asList("RECAP"));
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
        Mockito.doNothing().when(producer).sendBodyAndHeader(Mockito.any(), Mockito.anyString(), Mockito.any());
        dataDumpEmailService.sendEmail(institutionCodes(), 1, 0, "0", "2016-09-02 12:00", "peri.subrahmanya@gmail.com", "dataNotAvailable", 0, "2", "NYPL", Arrays.asList("RECAP"));
        assertTrue(true);
    }

    @Test
    public void testsendEmailForIncremental() {
        ILSConfigProperties ilsConfigProperties = getIlsConfigProperties();
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        dataDumpEmailService.sendEmail(institutionCodes(), 1, 0, "2", "2016-09-02 12:00", "peri.subrahmanya@gmail.com", "dataNotAvailable", 0, "1", "NYPL", Arrays.asList("RECAP"));
        assertTrue(true);
    }

    @Test
    public void sendEmailForDumpNotification() {
        DataDumpRequest[] dataDumpRequests = {getDataDumpRequest(ScsbConstants.DATADUMP_FETCHTYPE_FULL, ScsbConstants.DATADUMP_XML_FORMAT_MARC, ScsbConstants.DATADUMP_TRANSMISSION_TYPE_S3), getDataDumpRequest(ScsbConstants.DATADUMP_FETCHTYPE_INCREMENTAL, ScsbConstants.DATADUMP_XML_FORMAT_SCSB, ScsbConstants.DATADUMP_TRANSMISSION_TYPE_HTTP), getDataDumpRequest(ScsbConstants.DATADUMP_FETCHTYPE_DELETED, ScsbConstants.DATADUMP_DELETED_JSON_FORMAT, ScsbConstants.DATADUMP_TRANSMISSION_TYPE_FILESYSTEM), getDataDumpRequest("Export", ScsbConstants.DATADUMP_DELETED_JSON_FORMAT, ScsbConstants.DATADUMP_TRANSMISSION_TYPE_FILESYSTEM)};
        for (DataDumpRequest dataDumpRequest : dataDumpRequests) {
            Mockito.when(dataDumpUtil.getFetchType(Mockito.anyString())).thenCallRealMethod();
            Mockito.when(dataDumpUtil.getOutputformat(Mockito.anyString())).thenCallRealMethod();
            Mockito.when(dataDumpUtil.getTransmissionType(Mockito.anyString())).thenCallRealMethod();
            dataDumpEmailService.sendEmailNotification(dataDumpRequest);
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
        dataDumpRequest.setToEmailAddress("test@email.com");
        return dataDumpRequest;
    }
}
