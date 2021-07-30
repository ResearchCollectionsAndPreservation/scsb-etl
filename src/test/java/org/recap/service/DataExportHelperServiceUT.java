package org.recap.service;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbConstants;
import org.recap.camel.dynamicrouter.DynamicRouteBuilder;
import org.recap.model.export.DataDumpRequest;
import org.recap.model.jparw.ETLRequestLogEntity;
import org.recap.model.jparw.ExportStatusEntity;
import org.recap.service.preprocessor.DataDumpExportService;
import org.recap.util.datadump.DataDumpUtil;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

public class DataExportHelperServiceUT extends BaseTestCaseUT {

    @InjectMocks
    DataExportHelperService dataExportHelperService;

    @Mock
    DataExportDBService dataExportDBService;

    @Mock
    DataDumpUtil dataDumpUtil;

    @Mock
    DynamicRouteBuilder dynamicRouteBuilder;

    @Mock
    DataDumpExportService dataDumpExportService;

    @Test
    public void checkForExistingRequestAndStartForDATADUMPInprogress(){

        DataDumpRequest dataDumpRequest = getDataDumpRequest();
        dataDumpRequest.setTransmissionType(ScsbConstants.DATADUMP_TRANSMISSION_TYPE_S3);
        ExportStatusEntity exportStatusEntity = getExportStatusEntity();
        ETLRequestLogEntity etlRequestLogEntity = getEtlRequestLogEntity();
        Mockito.when(dataExportDBService.findByExportStatusCode(ScsbConstants.IN_PROGRESS)).thenReturn(exportStatusEntity);
        Mockito.when(dataExportDBService.findAllStatusById(exportStatusEntity.getId())).thenReturn(Arrays.asList(etlRequestLogEntity));
        Mockito.when(dataDumpUtil.prepareRequestForAwaiting(any(),any())).thenReturn(etlRequestLogEntity);
        Mockito.when(dataExportDBService.saveETLRequestToDB(any())).thenReturn(etlRequestLogEntity);
        String result = dataExportHelperService.checkForExistingRequestAndStart(dataDumpRequest);
        assertNotNull(result);
        assertEquals(ScsbConstants.EXPORT_MESSAGE,result);
    }
    @Test
    public void checkForExistingRequestAndStartForDATADUMPAwaiting(){

        DataDumpRequest dataDumpRequest = getDataDumpRequest();
        dataDumpRequest.setTransmissionType(ScsbConstants.DATADUMP_TRANSMISSION_TYPE_S3);
        ExportStatusEntity exportStatusEntity = getExportStatusEntity();
        ExportStatusEntity exportStatusEntity2 = new ExportStatusEntity();
        exportStatusEntity2.setId(2);
        ETLRequestLogEntity etlRequestLogEntity = getEtlRequestLogEntity();
        Mockito.when(dataExportDBService.findByExportStatusCode(ScsbConstants.IN_PROGRESS)).thenReturn(exportStatusEntity2);
        Mockito.when(dataExportDBService.findByExportStatusCode(ScsbConstants.AWAITING)).thenReturn(exportStatusEntity);
        Mockito.when(dataExportDBService.findByExportStatusIdAndTransmissionType(exportStatusEntity.getId(), ScsbConstants.DATADUMP_TRANSMISSION_TYPE_S3)).thenReturn(Arrays.asList(etlRequestLogEntity));
        Mockito.when(dataExportDBService.findByExportStatusIdAndTransmissionType(exportStatusEntity2.getId(), ScsbConstants.DATADUMP_TRANSMISSION_TYPE_S3)).thenReturn(Collections.EMPTY_LIST);
        Mockito.when(dataDumpUtil.prepareRequestForAwaiting(any(),any())).thenReturn(etlRequestLogEntity);
        Mockito.when(dataExportDBService.saveETLRequestToDB(any())).thenReturn(etlRequestLogEntity);
        Mockito.doNothing().when(dynamicRouteBuilder).addDataDumpExportRoutes();
        Mockito.when(dataDumpUtil.prepareRequestForExistingAwaiting()).thenReturn(dataDumpRequest);
        Mockito.when(dataDumpExportService.startDataDumpProcess(any())).thenReturn(ScsbConstants.EXPORT_MESSAGE);
        String result = dataExportHelperService.checkForExistingRequestAndStart(dataDumpRequest);
        assertNotNull(result);
        assertEquals(ScsbConstants.EXPORT_MESSAGE,result);
    }
    @Test
    public void checkForExistingRequestAndStartWithoutTransmission(){

        DataDumpRequest dataDumpRequest = getDataDumpRequest();
        dataDumpRequest.setTransmissionType("");
        ETLRequestLogEntity etlRequestLogEntity = getEtlRequestLogEntity();
        ExportStatusEntity exportStatusEntity = getExportStatusEntity();
        Mockito.when(dataDumpUtil.prepareRequestForAwaiting(any(),any())).thenReturn(etlRequestLogEntity);
        Mockito.when(dataExportDBService.saveETLRequestToDB(any())).thenReturn(etlRequestLogEntity);
       // Mockito.doNothing().when(dynamicRouteBuilder).addDataDumpExportRoutes();
        Mockito.when(dataDumpExportService.startDataDumpProcess(any())).thenReturn(ScsbConstants.EXPORT_MESSAGE);
        Mockito.when(dataExportDBService.findByExportStatusCode(ScsbConstants.IN_PROGRESS)).thenReturn(exportStatusEntity);
        Mockito.when(dataExportDBService.findAllStatusById(exportStatusEntity.getId())).thenReturn(Arrays.asList(etlRequestLogEntity));
        String result = dataExportHelperService.checkForExistingRequestAndStart(dataDumpRequest);
        assertNotNull(result);
        assertEquals(ScsbConstants.EXPORT_MESSAGE,result);
    }

    private DataDumpRequest getDataDumpRequest() {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setFetchType("0");
        dataDumpRequest.setRequestingInstitutionCode("NYPL");
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        dataDumpRequest.setCollectionGroupIds(cgIds);
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("CUL");
        institutionCodes.add("NYPL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setTransmissionType("2");
        dataDumpRequest.setOutputFileFormat(ScsbConstants.XML_FILE_FORMAT);
        dataDumpRequest.setDateTimeString(getDateTimeString());
        dataDumpRequest.setEtlRequestId(1);
        return dataDumpRequest;
    }
    private String getDateTimeString() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(ScsbConstants.DATE_FORMAT_DDMMMYYYYHHMM);
        return sdf.format(date);
    }
    private ExportStatusEntity getExportStatusEntity() {
        ExportStatusEntity exportStatusEntity = new ExportStatusEntity();
        exportStatusEntity.setId(1);
        exportStatusEntity.setExportStatusCode("Complete");
        exportStatusEntity.setExportStatusDesc("Complete");
        return exportStatusEntity;
    }
    private ETLRequestLogEntity getEtlRequestLogEntity() {
        ETLRequestLogEntity etlRequestLogEntity = new ETLRequestLogEntity();
        etlRequestLogEntity.setId(1);
        etlRequestLogEntity.setExportStatusEntity(new ExportStatusEntity());
        etlRequestLogEntity.setCompleteTime(new Date());
        etlRequestLogEntity.setCollectionGroupIds("001,002,003");
        etlRequestLogEntity.setEmailIds("emailids");
        etlRequestLogEntity.setRequestedTime(new Date());
        etlRequestLogEntity.setFetchType("Pull");
        etlRequestLogEntity.setExportStatusId(1);
        etlRequestLogEntity.setImsRepositoryCodes("IMRC");
        etlRequestLogEntity.setMessage("msg");
        etlRequestLogEntity.setInstCodeToExport("ECExport");
        etlRequestLogEntity.setOutputFormat("Format");
        etlRequestLogEntity.setRequestingInstCode("22");
        etlRequestLogEntity.setUserName("test");
        etlRequestLogEntity.setTransmissionType("transmission");
        etlRequestLogEntity.setProvidedDate(new Date());
        return etlRequestLogEntity;
    }
}
