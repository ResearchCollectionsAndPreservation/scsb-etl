package org.recap.service.executor.datadump;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.ILSConfigProperties;
import org.recap.service.DataExportValidateService;
import org.recap.service.preprocessor.DataDumpExportService;
import org.recap.util.PropertyUtil;
import org.recap.util.datadump.DataDumpUtil;
import org.recap.util.datadump.JobDataParameterUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class DataDumpSchedulerExecutorServiceUT extends BaseTestCaseUT {

    @InjectMocks
    DataDumpSchedulerExecutorService dataDumpSchedulerExecutorService;

    @Mock
    JobDataParameterUtil jobDataParameterUtil;

    @Mock
    DataDumpExportService dataDumpExportService;

    @Mock
    DataExportValidateService dataExportValidateService;

    @Mock
    DataDumpUtil dataDumpUtil;

    @Mock
    PropertyUtil propertyUtil;

    @Test
    public void testInitiateDataDumpForScheduler() {
        Map<String, String> requestParameterMap = new HashMap<>();
        requestParameterMap.put(ScsbConstants.FETCH_TYPE, ScsbConstants.FETCH_TYPE);
        Mockito.when(jobDataParameterUtil.buildJobRequestParameterMap(ScsbConstants.EXPORT_FETCH_TYPE_INSTITUTION)).thenReturn(requestParameterMap);
        Mockito.doNothing().when(dataDumpUtil).setDataDumpRequest(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),any());
        ILSConfigProperties ilsConfigProperties = new ILSConfigProperties();
        ilsConfigProperties.setEmailDataDumpTo("to");
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        Mockito.when(dataExportValidateService.validateIncomingRequest(any())).thenReturn(ScsbCommonConstants.SUCCESS);
        String initiateDataDumpForScheduler = dataDumpSchedulerExecutorService.initiateDataDumpForScheduler(new Date().toString(), "PUL", "");
        assertEquals(ScsbCommonConstants.SUCCESS, initiateDataDumpForScheduler);
    }

    @Test
    public void testInitiateDataDumpForSchedulerStart() {
        Map<String, String> requestParameterMap = new HashMap<>();
        requestParameterMap.put(ScsbConstants.FETCH_TYPE, ScsbConstants.FETCH_TYPE);
        Mockito.when(jobDataParameterUtil.buildJobRequestParameterMap(ScsbConstants.EXPORT_FETCH_TYPE_INSTITUTION)).thenReturn(requestParameterMap);
        Mockito.doNothing().when(dataDumpUtil).setDataDumpRequest(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),any());
        ILSConfigProperties ilsConfigProperties = new ILSConfigProperties();
        ilsConfigProperties.setEmailDataDumpTo("to");
        Mockito.when(propertyUtil.getILSConfigProperties(anyString())).thenReturn(ilsConfigProperties);
        Mockito.when(dataExportValidateService.validateIncomingRequest(any())).thenReturn(null);
        Mockito.when(dataDumpExportService.startDataDumpProcess(any())).thenReturn(ScsbCommonConstants.SUCCESS);
        String initiateDataDumpForScheduler = dataDumpSchedulerExecutorService.initiateDataDumpForScheduler(new Date().toString(), "PUL", "");
        assertEquals(ScsbCommonConstants.SUCCESS, initiateDataDumpForScheduler);
    }
}
