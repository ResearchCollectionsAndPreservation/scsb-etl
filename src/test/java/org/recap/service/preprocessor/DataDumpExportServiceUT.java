package org.recap.service.preprocessor;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.TestUtil;
import org.recap.model.ILSConfigProperties;
import org.recap.model.export.DataDumpRequest;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.repository.CollectionGroupDetailsRepository;
import org.recap.repository.ImsLocationDetailsRepository;
import org.recap.repository.InstitutionDetailsRepository;
import org.recap.service.email.datadump.DataDumpEmailService;
import org.recap.service.executor.datadump.DataDumpExecutorService;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


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
    ImsLocationDetailsRepository imsLocationDetailsRepository;

    @Mock
    CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    PropertyUtil propertyUtil;


    @Value("${etl.data.dump.fetchtype.full}")
    private String fetchTypeFull;

    @Value("${las.assist.email.to}")
    private String recapAssistEmailAddress;

    @Before
    public void setup() throws Exception {
        ReflectionTestUtils.setField(dataDumpExportService, "fetchTypeFull", fetchTypeFull);
        ReflectionTestUtils.setField(dataDumpExportService, "recapAssistEmailAddress", recapAssistEmailAddress);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void startDataDumpProcessForHTTPavailable() {
        DataDumpRequest dataDumpRequest=new DataDumpRequest();
        dataDumpRequest.setTransmissionType(RecapConstants.DATADUMP_TRANSMISSION_TYPE_HTTP);
        Mockito.when(consumerTemplate.receive(Mockito.anyString())).thenReturn(receive);
        Mockito.when(receive.getIn()).thenReturn(value);
        Mockito.when(value.getBody()).thenReturn(null).thenReturn(RecapConstants.DATADUMP_RECORDS_AVAILABLE_FOR_PROCESS);
        String responseMessage=dataDumpExportService.startDataDumpProcess(dataDumpRequest);
        assertEquals(RecapConstants.DATADUMP_RECORDS_AVAILABLE_FOR_PROCESS,responseMessage);
    }

    @Test
    public void startDataDumpProcessForHTTPunavailable() {
        DataDumpRequest dataDumpRequest=new DataDumpRequest();
        dataDumpRequest.setTransmissionType(RecapConstants.DATADUMP_TRANSMISSION_TYPE_HTTP);
        Mockito.when(consumerTemplate.receive(Mockito.anyString())).thenReturn(receive);
        Mockito.when(receive.getIn()).thenReturn(value);
        Mockito.when(value.getBody()).thenReturn("unavailable");
        String responseMessage=dataDumpExportService.startDataDumpProcess(dataDumpRequest);
        assertEquals("unavailable",responseMessage);
    }

    @Test
    public void startDataDumpProcessForFTP() {
        DataDumpRequest dataDumpRequest=new DataDumpRequest();
        dataDumpRequest.setTransmissionType(RecapConstants.DATADUMP_TRANSMISSION_TYPE_FTP);
        Mockito.when(consumerTemplate.receive(Mockito.anyString())).thenReturn(receive);
        Mockito.when(receive.getIn()).thenReturn(value);
        Mockito.when(value.getBody()).thenReturn(RecapConstants.DATADUMP_RECORDS_AVAILABLE_FOR_PROCESS);
        String dataDumpStatusFileName = this.getClass().getResource("dataExportStatus.txt").getPath();
        ReflectionTestUtils.setField(dataDumpExportService, "dataDumpStatusFileName", dataDumpStatusFileName);
        String responseMessage=dataDumpExportService.startDataDumpProcess(dataDumpRequest);
        assertEquals(RecapConstants.DATADUMP_PROCESS_STARTED,responseMessage);
    }

    @Test
    public void startDataDumpProcessInvalidTransmissionType() {
        DataDumpRequest dataDumpRequest=new DataDumpRequest();
        dataDumpRequest.setTransmissionType("");
        Mockito.when(consumerTemplate.receive(Mockito.anyString())).thenReturn(receive);
        Mockito.when(receive.getIn()).thenReturn(value);
        Mockito.when(value.getBody()).thenReturn(RecapConstants.DATADUMP_RECORDS_AVAILABLE_FOR_PROCESS);
        String responseMessage=dataDumpExportService.startDataDumpProcess(dataDumpRequest);
        assertEquals(RecapConstants.DATADUMP_EXPORT_FAILURE,responseMessage);
    }

    @Test
    public void startDataDumpProcessForFTPNoDataAvailable() {
        DataDumpRequest dataDumpRequest=new DataDumpRequest();
        dataDumpRequest.setTransmissionType(RecapConstants.DATADUMP_TRANSMISSION_TYPE_FTP);
        Mockito.when(consumerTemplate.receive(Mockito.anyString())).thenReturn(receive);
        Mockito.when(receive.getIn()).thenReturn(value);
        Mockito.when(value.getBody()).thenReturn(RecapConstants.DATADUMP_NO_DATA_AVAILABLE);
        String responseMessage=dataDumpExportService.startDataDumpProcess(dataDumpRequest);
        assertEquals(RecapConstants.DATADUMP_NO_DATA_AVAILABLE,responseMessage);
    }

    @Test
    public void startDataDumpProcessException() {
        DataDumpRequest dataDumpRequest=new DataDumpRequest();
        dataDumpRequest.setTransmissionType(RecapConstants.DATADUMP_TRANSMISSION_TYPE_HTTP);
        Mockito.when(consumerTemplate.receive(Mockito.anyString())).thenReturn(receive);
        Mockito.when(receive.getIn()).thenReturn(value);
        Mockito.when(value.getBody()).thenReturn(RecapConstants.DATADUMP_RECORDS_AVAILABLE_FOR_PROCESS);
        ReflectionTestUtils.setField(dataDumpExportService, "dataDumpExecutorService", null);
        ReflectionTestUtils.setField(dataDumpExportService, "dataDumpEmailService", null);
        String responseMessage=dataDumpExportService.startDataDumpProcess(dataDumpRequest);
        assertEquals(RecapConstants.DATADUMP_EXPORT_FAILURE,responseMessage);
    }

    @Test
    public void setDataDumpRequest() {
        dataDumpExportService.setDataDumpRequest(new DataDumpRequest(),"10","PUL",new Date().toString(),new Date().toString(),"1","0","PUL","test@htcindia.com","0","RECAP");
        assertTrue(true);
    }

    @Test
    public void setDataDumpRequestWithBlankimslocation() {
        Mockito.when(imsLocationDetailsRepository.findByImsLocationCode(Mockito.anyString())).thenReturn(TestUtil.getImsLocationEntity(1,"RECAP","RECAP_LAS"));
        Mockito.when(collectionGroupDetailsRepository.findByCollectionGroupCode(RecapConstants.COLLECTION_GROUP_SHARED)).thenReturn(getCollectionGroupEntity(1,RecapConstants.COLLECTION_GROUP_SHARED));
        Mockito.when(collectionGroupDetailsRepository.findByCollectionGroupCode(RecapConstants.COLLECTION_GROUP_OPEN)).thenReturn(getCollectionGroupEntity(2,RecapConstants.COLLECTION_GROUP_OPEN));
        dataDumpExportService.setDataDumpRequest(new DataDumpRequest(),"10","PUL",new Date().toString(),new Date().toString(),"","","PUL","test@htcindia.com","0","");
        assertTrue(true);
    }


    @Test
    public void validateIncomingRequestFull() {
        DataDumpRequest dataDumpRequest=getDataDumpRequest(Arrays.asList("PUL"),"PUL",Arrays.asList("RECAP"),RecapConstants.DATADUMP_FETCHTYPE_FULL,RecapConstants.DATADUMP_TRANSMISSION_TYPE_FTP,RecapCommonConstants.DATE_FORMAT_YYYYMMDDHHMM);
        Mockito.when(institutionDetailsRepository.findAllInstitutionCodeExceptHTC()).thenReturn(Arrays.asList("PUL","CUL","NYPL","HUL"));
        Mockito.when(imsLocationDetailsRepository.findAllImsLocationCode()).thenReturn(Arrays.asList("RECAP","HD"));
        String dataDumpStatusFileName = this.getClass().getResource("dataExportStatus.txt").getPath();
        ReflectionTestUtils.setField(dataDumpExportService, "dataDumpStatusFileName", dataDumpStatusFileName);
        String validationMessage =dataDumpExportService.validateIncomingRequest(dataDumpRequest);
        assertNull(validationMessage);
    }

    @Test
    public void validateIncomingRequestFetchTypeError() {
        DataDumpRequest dataDumpRequest=getDataDumpRequest(Arrays.asList("PUL"),"PUL",Arrays.asList("RECAP")," ",RecapConstants.DATADUMP_TRANSMISSION_TYPE_FTP,RecapCommonConstants.DATE_FORMAT_YYYYMMDDHHMM);
        Mockito.when(institutionDetailsRepository.findAllInstitutionCodeExceptHTC()).thenReturn(Arrays.asList("PUL","CUL","NYPL","HUL"));
        Mockito.when(imsLocationDetailsRepository.findAllImsLocationCode()).thenReturn(Arrays.asList("RECAP","HD"));
        String dataDumpStatusFileName = this.getClass().getResource("dataExportStatus.txt").getPath();
        ReflectionTestUtils.setField(dataDumpExportService, "dataDumpStatusFileName", dataDumpStatusFileName);
        String validationMessage =dataDumpExportService.validateIncomingRequest(dataDumpRequest);
        assertTrue(validationMessage.contains(RecapConstants.DATADUMP_VALID_FETCHTYPE_ERR_MSG));
    }



    @Test
    public void validateIncomingRequestIncrementalFailure() {
        ReflectionTestUtils.setField(dataDumpExportService, "incrementalDateLimit", "");
        DataDumpRequest dataDumpRequest=getDataDumpRequest(Arrays.asList("PUL"),"PUL",Arrays.asList("RECAP"),RecapConstants.DATADUMP_FETCHTYPE_INCREMENTAL,RecapConstants.DATADUMP_TRANSMISSION_TYPE_FTP,RecapCommonConstants.DATE_FORMAT_YYYYMMDDHHMM);
        dataDumpRequest.setToEmailAddress("");
        Mockito.when(institutionDetailsRepository.findAllInstitutionCodeExceptHTC()).thenReturn(Arrays.asList("PUL","CUL","NYPL","HUL"));
        Mockito.when(imsLocationDetailsRepository.findAllImsLocationCode()).thenReturn(Arrays.asList("RECAP","HD"));
        String dataDumpStatusFileName = getClass().getResource("dataExportStatusInProgress.txt").getPath();
        ReflectionTestUtils.setField(dataDumpExportService, "dataDumpStatusFileName", dataDumpStatusFileName);
        ILSConfigProperties ilsConfigProperties=new  ILSConfigProperties();
        ilsConfigProperties.setEtlInitialDataLoadedDate(new SimpleDateFormat(RecapConstants.DATE_FORMAT_YYYYMMDD).format(new Date()));
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        String validationMessage =dataDumpExportService.validateIncomingRequest(dataDumpRequest);
        assertTrue(validationMessage.contains("The date used for incremental data dump precedes (or) is the date on which records for the institution PUL were created. Kindly use a later date or contact HTC Support  for assistance."));
        assertTrue(validationMessage.contains("The incremental Date limit is missing. "));
        assertTrue(validationMessage.contains(RecapConstants.DATADUMP_EMAIL_TO_ADDRESS_REQUIRED));
        assertTrue(validationMessage.contains(RecapConstants.INPROGRESS_ERR_MSG));
    }

    @Test
    public void validateIncomingRequestException() {
        ReflectionTestUtils.setField(dataDumpExportService, "incrementalDateLimit", "");
        ReflectionTestUtils.setField(dataDumpExportService, "propertyUtil", null);
        DataDumpRequest dataDumpRequest=getDataDumpRequest(Arrays.asList("PUL"),"PUL",Arrays.asList("RECAP"),RecapConstants.DATADUMP_FETCHTYPE_INCREMENTAL,RecapConstants.DATADUMP_TRANSMISSION_TYPE_FTP,RecapCommonConstants.DATE_FORMAT_YYYYMMDDHHMM);
        dataDumpRequest.setToEmailAddress("");
        Mockito.when(institutionDetailsRepository.findAllInstitutionCodeExceptHTC()).thenReturn(Arrays.asList("PUL","CUL","NYPL","HUL"));
        Mockito.when(imsLocationDetailsRepository.findAllImsLocationCode()).thenReturn(Arrays.asList("RECAP","HD"));
        String dataDumpStatusFileName = getClass().getResource("dataExportStatusInProgress.txt").getPath();
        ReflectionTestUtils.setField(dataDumpExportService, "dataDumpStatusFileName", dataDumpStatusFileName);
        String validationMessage =dataDumpExportService.validateIncomingRequest(dataDumpRequest);
        assertTrue(validationMessage.contains(RecapConstants.DATADUMP_EMAIL_TO_ADDRESS_REQUIRED));
        assertTrue(validationMessage.contains(RecapConstants.INPROGRESS_ERR_MSG));
    }

    @Test
    public void validateIncomingRequestDateFailure() {
        ReflectionTestUtils.setField(dataDumpExportService, "incrementalDateLimit", "5");
        DataDumpRequest dataDumpRequest=getDataDumpRequest(Arrays.asList("PUL"),"PUL",Arrays.asList("RECAP"),RecapConstants.DATADUMP_FETCHTYPE_INCREMENTAL,RecapConstants.DATADUMP_TRANSMISSION_TYPE_FTP,RecapCommonConstants.DATE_FORMAT_YYYYMMDDHHMM);
        dataDumpRequest.setToEmailAddress("");
        dataDumpRequest.setDate(new SimpleDateFormat(RecapCommonConstants.DATE_FORMAT_YYYYMMDDHHMM).format(new DateTime(new Date()).minusDays(7).toDate()));
        Mockito.when(institutionDetailsRepository.findAllInstitutionCodeExceptHTC()).thenReturn(Arrays.asList("PUL","CUL","NYPL","HUL"));
        Mockito.when(imsLocationDetailsRepository.findAllImsLocationCode()).thenReturn(Arrays.asList("RECAP","HD"));
        String dataDumpStatusFileName = getClass().getResource("dataExportStatusInProgress.txt").getPath();
        ReflectionTestUtils.setField(dataDumpExportService, "dataDumpStatusFileName", dataDumpStatusFileName);
        ILSConfigProperties ilsConfigProperties=new  ILSConfigProperties();
        ilsConfigProperties.setEtlInitialDataLoadedDate(new SimpleDateFormat(RecapConstants.DATE_FORMAT_YYYYMMDD).format(new Date()));
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        String validationMessage =dataDumpExportService.validateIncomingRequest(dataDumpRequest);
        assertTrue(validationMessage.contains("The date used for incremental data dump precedes (or) is the date on which records for the institution PUL were created. Kindly use a later date or contact HTC Support  for assistance."));
        assertTrue(validationMessage.contains(RecapConstants.DATADUMP_EMAIL_TO_ADDRESS_REQUIRED));
        assertTrue(validationMessage.contains(RecapConstants.INPROGRESS_ERR_MSG));
    }

    @Test
    public void validateIncomingRequestEmailFailure() {
        ReflectionTestUtils.setField(dataDumpExportService, "incrementalDateLimit", "");
        DataDumpRequest dataDumpRequest=getDataDumpRequest(Arrays.asList("PUL"),"PUL",Arrays.asList("RECAP"),RecapConstants.DATADUMP_FETCHTYPE_INCREMENTAL,RecapConstants.DATADUMP_TRANSMISSION_TYPE_FTP,RecapCommonConstants.DATE_FORMAT_YYYYMMDDHHMM);
        dataDumpRequest.setToEmailAddress("5");
        dataDumpRequest.setDate(null);
        Mockito.when(institutionDetailsRepository.findAllInstitutionCodeExceptHTC()).thenReturn(Arrays.asList("PUL","CUL","NYPL","HUL"));
        Mockito.when(imsLocationDetailsRepository.findAllImsLocationCode()).thenReturn(Arrays.asList("RECAP","HD"));
        String dataDumpStatusFileName = getClass().getResource("dataExportStatusInProgress.txt").getPath();
        ReflectionTestUtils.setField(dataDumpExportService, "dataDumpStatusFileName", dataDumpStatusFileName);
        ILSConfigProperties ilsConfigProperties=new  ILSConfigProperties();
        ilsConfigProperties.setEtlInitialDataLoadedDate(new SimpleDateFormat(RecapConstants.DATE_FORMAT_YYYYMMDD).format(new Date()));
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        String validationMessage =dataDumpExportService.validateIncomingRequest(dataDumpRequest);
        assertTrue(validationMessage.contains(RecapConstants.DATADUMP_DATE_ERR_MSG));
        assertTrue(validationMessage.contains(RecapConstants.INVALID_EMAIL_ADDRESS));
        assertTrue(validationMessage.contains(RecapConstants.INPROGRESS_ERR_MSG));
    }

    @Test
    public void validateIncomingRequestDeleted() {
        ReflectionTestUtils.setField(dataDumpExportService, "incrementalDateLimit", "5");
        DataDumpRequest dataDumpRequest = getDataDumpRequest(Arrays.asList("PUL"),"PUL",Arrays.asList("RECAP"),RecapConstants.DATADUMP_FETCHTYPE_DELETED,RecapConstants.DATADUMP_TRANSMISSION_TYPE_HTTP,RecapCommonConstants.DATE_FORMAT_YYYYMMDDHHMM);
        Mockito.when(institutionDetailsRepository.findAllInstitutionCodeExceptHTC()).thenReturn(Arrays.asList("PUL","CUL","NYPL","HUL"));
        Mockito.when(imsLocationDetailsRepository.findAllImsLocationCode()).thenReturn(Arrays.asList("RECAP","HD"));
        String dataDumpStatusFileName = getClass().getResource("dataExportStatus.txt").getPath();
        ReflectionTestUtils.setField(dataDumpExportService, "dataDumpStatusFileName", dataDumpStatusFileName);
        ILSConfigProperties ilsConfigProperties=new  ILSConfigProperties();
        ilsConfigProperties.setEtlInitialDataLoadedDate(new SimpleDateFormat(RecapConstants.DATE_FORMAT_YYYYMMDD).format(new DateTime(new Date()).minusDays(5).toDate()));
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        String validationMessage =dataDumpExportService.validateIncomingRequest(dataDumpRequest);
        assertNull(validationMessage);
    }

    @Test
    public void validateIncomingRequestFullDumpFailure() {
        DataDumpRequest dataDumpRequest=getDataDumpRequest(Arrays.asList("HTC","HTC"),"HTC",Arrays.asList("HTC"),RecapConstants.DATADUMP_FETCHTYPE_FULL,"",RecapCommonConstants.DATE_FORMAT_YYYYMMDDHHMM);
        Mockito.when(institutionDetailsRepository.findAllInstitutionCodeExceptHTC()).thenReturn(Arrays.asList("PUL","CUL","NYPL","HUL"));
        Mockito.when(imsLocationDetailsRepository.findAllImsLocationCode()).thenReturn(Arrays.asList("RECAP","HD"));
        String dataDumpStatusFileName = getClass().getResource("dataExportStatus.txt").getPath();
        ReflectionTestUtils.setField(dataDumpExportService, "dataDumpStatusFileName", dataDumpStatusFileName);
        String validationMessage =dataDumpExportService.validateIncomingRequest(dataDumpRequest);
        assertTrue(validationMessage.contains(RecapConstants.DATADUMP_VALID_INST_CODES_ERR_MSG));
        assertTrue(validationMessage.contains(RecapConstants.DATADUMP_MULTIPLE_INST_CODES_ERR_MSG));
        assertTrue(validationMessage.contains(RecapConstants.DATADUMP_VALID_REQ_INST_CODE_ERR_MSG));
        assertTrue(validationMessage.contains(RecapConstants.DATADUMP_VALID_IMS_DEPOSITORY_CODE_ERR_MSG));
        assertTrue(validationMessage.contains(RecapConstants.DATADUMP_TRANS_TYPE_ERR_MSG));
    }

    private DataDumpRequest getDataDumpRequest(List<String> institutionCodes,String requestingInstitutionCode,List<String> imsDepositoryCodes,String fetchType,String transmissionType,String date) {
        DataDumpRequest dataDumpRequest=new DataDumpRequest();
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setRequestingInstitutionCode(requestingInstitutionCode);
        dataDumpRequest.setImsDepositoryCodes(imsDepositoryCodes);
        dataDumpRequest.setFetchType(fetchType);
        dataDumpRequest.setTransmissionType(transmissionType);
        dataDumpRequest.setToEmailAddress("test@htcindia.com");
        dataDumpRequest.setDate(new SimpleDateFormat(date).format(new Date()));
        return dataDumpRequest;
    }

    private CollectionGroupEntity getCollectionGroupEntity(int id,String collectionGroupCode) {
        CollectionGroupEntity collectionGroupEntity=new CollectionGroupEntity();
        collectionGroupEntity.setId(id);
        collectionGroupEntity.setCollectionGroupCode(collectionGroupCode);
        return collectionGroupEntity;
    }
}
