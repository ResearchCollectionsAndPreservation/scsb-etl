package org.recap.service.preprocessor;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbConstants;
import org.recap.model.export.DataDumpRequest;
import org.recap.service.email.datadump.DataDumpEmailService;
import org.recap.service.executor.datadump.DataDumpExecutorService;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DataDumpExportServiceUT extends BaseTestCaseUT {
    @InjectMocks
    DataDumpExportService dataDumpExportService;

    @Mock
    DataDumpExecutorService dataDumpExecutorService;

    @Mock
    ConsumerTemplate consumerTemplate;

    @Mock
    Exchange receive;

    @Mock
    Message value;


    @Mock
    DataDumpEmailService dataDumpEmailService;

    @Mock
    PropertyUtil propertyUtil;

    @Mock
    ProducerTemplate producerTemplate;


    @Value("${etl.data.dump.fetchtype.full}")
    private String fetchTypeFull;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void startDataDumpProcessForHTTPavailable() {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setTransmissionType(ScsbConstants.DATADUMP_TRANSMISSION_TYPE_HTTP);
        Mockito.when(consumerTemplate.receive(Mockito.anyString())).thenReturn(receive);
        Mockito.when(receive.getIn()).thenReturn(value);
        Mockito.when(value.getBody()).thenReturn(null).thenReturn(ScsbConstants.DATADUMP_RECORDS_AVAILABLE_FOR_PROCESS);
        String responseMessage = dataDumpExportService.startDataDumpProcess(dataDumpRequest);
        assertEquals(ScsbConstants.DATADUMP_RECORDS_AVAILABLE_FOR_PROCESS, responseMessage);
    }

    @Test
    public void startDataDumpProcessForHTTPunavailable() {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setTransmissionType(ScsbConstants.DATADUMP_TRANSMISSION_TYPE_HTTP);
        Mockito.when(consumerTemplate.receive(Mockito.anyString())).thenReturn(receive);
        Mockito.when(receive.getIn()).thenReturn(value);
        Mockito.when(value.getBody()).thenReturn("unavailable");
        String responseMessage = dataDumpExportService.startDataDumpProcess(dataDumpRequest);
        assertEquals("unavailable", responseMessage);
    }

    @Test
    public void startDataDumpProcessForFTP() {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setTransmissionType(ScsbConstants.DATADUMP_TRANSMISSION_TYPE_S3);
        Mockito.when(consumerTemplate.receive(Mockito.anyString())).thenReturn(receive);
        Mockito.when(receive.getIn()).thenReturn(value);
        Mockito.when(value.getBody()).thenReturn(ScsbConstants.DATADUMP_RECORDS_AVAILABLE_FOR_PROCESS);
        String dataDumpStatusFileName = this.getClass().getResource("dataExportStatus.txt").getPath();
        ReflectionTestUtils.setField(dataDumpExportService, "dataDumpStatusFileName", dataDumpStatusFileName);
        String responseMessage = dataDumpExportService.startDataDumpProcess(dataDumpRequest);
        assertEquals(ScsbConstants.DATADUMP_PROCESS_STARTED, responseMessage);
    }

    @Test
    public void startDataDumpProcessInvalidTransmissionType() {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setTransmissionType("");
        Mockito.when(consumerTemplate.receive(Mockito.anyString())).thenReturn(receive);
        Mockito.when(receive.getIn()).thenReturn(value);
        Mockito.when(value.getBody()).thenReturn(ScsbConstants.DATADUMP_RECORDS_AVAILABLE_FOR_PROCESS);
        String responseMessage = dataDumpExportService.startDataDumpProcess(dataDumpRequest);
        assertEquals(ScsbConstants.DATADUMP_EXPORT_FAILURE, responseMessage);
    }

    @Test
    public void startDataDumpProcessForFTPNoDataAvailable() {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setTransmissionType(ScsbConstants.DATADUMP_TRANSMISSION_TYPE_S3);
        Mockito.when(consumerTemplate.receive(Mockito.anyString())).thenReturn(receive);
        Mockito.when(receive.getIn()).thenReturn(value);
        Mockito.when(value.getBody()).thenReturn(ScsbConstants.DATADUMP_NO_DATA_AVAILABLE);
        String responseMessage = dataDumpExportService.startDataDumpProcess(dataDumpRequest);
        assertEquals(ScsbConstants.DATADUMP_NO_DATA_AVAILABLE, responseMessage);
    }

    @Test
    public void startDataDumpProcessException() {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setTransmissionType(ScsbConstants.DATADUMP_TRANSMISSION_TYPE_HTTP);
        Mockito.when(consumerTemplate.receive(Mockito.anyString())).thenReturn(receive);
        Mockito.when(receive.getIn()).thenReturn(value);
        Mockito.when(value.getBody()).thenReturn(ScsbConstants.DATADUMP_RECORDS_AVAILABLE_FOR_PROCESS);
        ReflectionTestUtils.setField(dataDumpExportService, "dataDumpExecutorService", null);
        ReflectionTestUtils.setField(dataDumpExportService, "dataDumpEmailService", null);
        String responseMessage = dataDumpExportService.startDataDumpProcess(dataDumpRequest);
        assertEquals(ScsbConstants.DATADUMP_EXPORT_FAILURE, responseMessage);
    }

    @Test
    public void setDataExportCurrentStatus() {
        ReflectionTestUtils.setField(dataDumpExportService, "dataDumpStatusFileName", "src/test/resources/org/recap/service/formatter/datadump/princeton.xml");
        ReflectionTestUtils.invokeMethod(dataDumpExportService, "setDataExportCurrentStatus");
    }

    @Test
    public void writeStatusToFile(){
        File file = new File("dataExportStatus.txt");
        String status = ScsbConstants.DATAEXPORT_WITH_SUCCESS_REPORT_FTP_ROUTE_ID;
        ReflectionTestUtils.invokeMethod(dataDumpExportService,"writeStatusToFile",file,status);
    }

}
