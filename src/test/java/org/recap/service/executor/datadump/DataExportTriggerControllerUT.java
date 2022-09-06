package org.recap.service.executor.datadump;

import org.apache.camel.ProducerTemplate;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.controller.DataExportTriggerController;
import org.recap.model.export.DataDumpRequest;
import org.recap.service.DataExportHelperService;
import org.recap.service.email.datadump.DataDumpEmailService;
import org.recap.util.PropertyUtil;
import org.recap.util.datadump.DataDumpUtil;
import org.springframework.test.util.ReflectionTestUtils;


/**
 * @author Chittoor Charan Raj
 */
public class DataExportTriggerControllerUT extends BaseTestCaseUT {

    @InjectMocks
    DataExportTriggerController controller;

//    @Mock
//    @Value("${datadump.dynamic.trigger}")
//    Boolean isActive;

    @Mock
    DataExportHelperService dataExportHelperService;

    @Mock
    DataDumpUtil dataDumpUtil;

    @Mock
    PropertyUtil propertyUtil;

    @Mock
    ProducerTemplate producer;

    @Mock
    DataDumpEmailService dataDumpEmailService;


    @Test
    public void sendEmailForDataDumpTriggerTest(){
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        ReflectionTestUtils.invokeMethod(controller, "sendEmailForDataDumpTrigger", dataDumpRequest);
    }

    @Test
    public void isDataDumpTriggerValidTest(){
        Mockito.when(dataExportHelperService.checkIfAnyExportIsAwaiting()).thenReturn(false);
        Mockito.when(dataExportHelperService.checkIfAnyExportIsAwaiting()).thenReturn(true);
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        Mockito.when(dataDumpUtil.prepareRequestForExistingAwaiting()).thenReturn(dataDumpRequest);
        Boolean valid = controller.isDataDumpTriggerValid();
        Assert.assertEquals(valid, true);
    }

    @Test
    public void validateDatadumpTriggerTest(){
        Boolean aBoolean = controller.validateDatadumpTrigger();
        Assert.assertEquals(aBoolean, false);
    }

}
