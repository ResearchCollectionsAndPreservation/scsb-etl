package org.recap.controller;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbConstants;
import org.recap.TestUtil;
import org.recap.camel.dynamicrouter.DynamicRouteBuilder;
import org.recap.service.executor.datadump.DataDumpSchedulerExecutorService;
import org.recap.util.CommonUtil;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataDumpSequenceRestControllerUT extends BaseTestCaseUT {
    @InjectMocks
    DataDumpSequenceRestController dataDumpSequenceRestController;

    @Mock
    CommonUtil commonUtil;

    @Mock
    DynamicRouteBuilder dynamicRouteBuilder;

    @Mock
    DataDumpSchedulerExecutorService dataDumpSchedulerExecutorService;


    @Test
    public void testExportDataDump() {
        Mockito.when(commonUtil.findAllInstitutionCodesExceptSupportInstitution()).thenReturn(TestUtil.getInstitutionCodeExceptSupportInstitution());
        Mockito.when(dataDumpSchedulerExecutorService.initiateDataDumpForScheduler(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(ScsbConstants.DATADUMP_PROCESS_STARTED);
        String exportDataDump = dataDumpSequenceRestController.exportDataDump(new Date().toString());
        assertEquals(ScsbConstants.DATADUMP_PROCESS_STARTED, exportDataDump);
    }
}
