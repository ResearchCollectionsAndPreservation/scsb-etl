package org.recap.service.executor.datadump;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.ILSConfigProperties;
import org.recap.service.preprocessor.DataDumpExportService;
import org.recap.util.PropertyUtil;
import org.recap.util.datadump.JobDataParameterUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DataDumpSchedulerExecutorServiceUT extends BaseTestCaseUT {

    @InjectMocks
    DataDumpSchedulerExecutorService dataDumpSchedulerExecutorService;

    @Mock
    JobDataParameterUtil jobDataParameterUtil;

    @Mock
    DataDumpExportService dataDumpExportService;

    @Mock
    PropertyUtil propertyUtil;

    @Test
    public void testInitiateDataDumpForScheduler() {
        Map<String, String> requestParameterMap=new HashMap<>();
        requestParameterMap.put(RecapConstants.FETCH_TYPE,RecapConstants.FETCH_TYPE);
        Mockito.when(jobDataParameterUtil.buildJobRequestParameterMap(RecapConstants.EXPORT_FETCH_TYPE_INSTITUTION)).thenReturn(requestParameterMap);
        Mockito.doNothing().when(dataDumpExportService).setDataDumpRequest(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.anyString());
        ILSConfigProperties ilsConfigProperties=new ILSConfigProperties();
        ilsConfigProperties.setEmailDataDumpTo("to");
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        Mockito.when(dataDumpExportService.validateIncomingRequest(Mockito.any())).thenReturn(RecapCommonConstants.SUCCESS);
        String initiateDataDumpForScheduler=dataDumpSchedulerExecutorService.initiateDataDumpForScheduler(new Date().toString(), "PUL", "");
        assertEquals(RecapCommonConstants.SUCCESS,initiateDataDumpForScheduler);
    }

    @Test
    public void testInitiateDataDumpForSchedulerStart() {
        Map<String, String> requestParameterMap=new HashMap<>();
        requestParameterMap.put(RecapConstants.FETCH_TYPE,RecapConstants.FETCH_TYPE);
        Mockito.when(jobDataParameterUtil.buildJobRequestParameterMap(RecapConstants.EXPORT_FETCH_TYPE_INSTITUTION)).thenReturn(requestParameterMap);
        Mockito.doNothing().when(dataDumpExportService).setDataDumpRequest(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.anyString());
        ILSConfigProperties ilsConfigProperties=new ILSConfigProperties();
        ilsConfigProperties.setEmailDataDumpTo("to");
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        Mockito.when(dataDumpExportService.validateIncomingRequest(Mockito.any())).thenReturn(null);
        Mockito.when(dataDumpExportService.startDataDumpProcess(Mockito.any())).thenReturn(RecapCommonConstants.SUCCESS);
        String initiateDataDumpForScheduler=dataDumpSchedulerExecutorService.initiateDataDumpForScheduler(new Date().toString(), "PUL", "");
        assertEquals(RecapCommonConstants.SUCCESS,initiateDataDumpForScheduler);
    }
}
