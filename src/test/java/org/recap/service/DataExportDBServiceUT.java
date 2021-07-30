package org.recap.service;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.jparw.ETLRequestLogEntity;
import org.recap.model.jparw.ExportStatusEntity;
import org.recap.repositoryrw.ETLRequestLogDetailsRepository;
import org.recap.repositoryrw.ExportStatusDetailsRepository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DataExportDBServiceUT extends BaseTestCaseUT {

    @InjectMocks
    DataExportDBService dataExportDBService;

    @Mock
    ETLRequestLogDetailsRepository etlRequestLogDetailsRepository;

    @Mock
    ExportStatusDetailsRepository exportStatusDetailsRepository;

    @Test
    public void saveETLRequestToDB(){
        ETLRequestLogEntity etlRequestLogEntity = getEtlRequestLogEntity();
        Mockito.when(etlRequestLogDetailsRepository.saveAndFlush(etlRequestLogEntity)).thenReturn(etlRequestLogEntity);
        ETLRequestLogEntity logEntity = dataExportDBService.saveETLRequestToDB(etlRequestLogEntity);
        assertNotNull(logEntity);
        assertEquals(etlRequestLogEntity,logEntity);
    }

    @Test
    public void findByExportStatusCode(){
        String statusCode = "COMPLETE";
        ExportStatusEntity exportStatusEntity = getExportStatusEntity();
        Mockito.when(exportStatusDetailsRepository.findByExportStatusCode(statusCode)).thenReturn(exportStatusEntity);
        ExportStatusEntity statusEntity = dataExportDBService.findByExportStatusCode(statusCode);
        assertNotNull(statusEntity);
        assertEquals(exportStatusEntity,statusEntity);
    }

    @Test
    public void findAllStatusById(){
        Integer id = 1;
        ETLRequestLogEntity etlRequestLogEntity = getEtlRequestLogEntity();
        Mockito.when(etlRequestLogDetailsRepository.findAllByExportStatusId(id)).thenReturn(Arrays.asList(etlRequestLogEntity));
        List<ETLRequestLogEntity> etlRequestLogEntities = dataExportDBService.findAllStatusById(id);
        assertNotNull(etlRequestLogEntities);
        assertEquals(Arrays.asList(etlRequestLogEntity),etlRequestLogEntities);
    }

    @Test
    public void findAllStatusForS3OrderByRequestedTime(){
        Integer statusId = 1;
        String fetchTypeId = "1";
        ETLRequestLogEntity etlRequestLogEntity = getEtlRequestLogEntity();
        Mockito.when(etlRequestLogDetailsRepository.findByExportStatusIdAndTransmissionTypeOrderByRequestedTime(statusId,fetchTypeId)).thenReturn(Arrays.asList(etlRequestLogEntity));
        List<ETLRequestLogEntity> etlRequestLogEntities = dataExportDBService.findAllStatusForS3OrderByRequestedTime(statusId,fetchTypeId);
        assertNotNull(etlRequestLogEntities);
        assertEquals(Arrays.asList(etlRequestLogEntity),etlRequestLogEntities);
    }

    @Test
    public void findByExportStatusIdAndTransmissionType(){
        Integer statusId = 1;
        String fetchTypeId = "1";
        ETLRequestLogEntity etlRequestLogEntity = getEtlRequestLogEntity();
        Mockito.when(etlRequestLogDetailsRepository.findByExportStatusIdAndTransmissionType(statusId,fetchTypeId)).thenReturn(Arrays.asList(etlRequestLogEntity));
        List<ETLRequestLogEntity> etlRequestLogEntities = dataExportDBService.findByExportStatusIdAndTransmissionType(statusId,fetchTypeId);
        assertNotNull(etlRequestLogEntities);
        assertEquals(Arrays.asList(etlRequestLogEntity),etlRequestLogEntities);
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
