package org.recap.service.executor.datadump;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.Assert.assertTrue;

public class DataDumpSchedulerExecutorServiceUT extends BaseTestCase {

    @Autowired
    DataDumpSchedulerExecutorService dataDumpSchedulerExecutorService;


    @Test
    public void testServices() {
        dataDumpSchedulerExecutorService.getDataDumpExportService();
        dataDumpSchedulerExecutorService.getJobDataParameterUtil();
        ReflectionTestUtils.invokeMethod(dataDumpSchedulerExecutorService,"getExportJobNameByInstitution","CUL");
        ReflectionTestUtils.invokeMethod(dataDumpSchedulerExecutorService,"getExportJobNameByInstitution","PUL");
        ReflectionTestUtils.invokeMethod(dataDumpSchedulerExecutorService,"getExportJobNameByInstitution","NYPL");
        ReflectionTestUtils.invokeMethod(dataDumpSchedulerExecutorService,"getToEmailAddress","NYPL");
        ReflectionTestUtils.invokeMethod(dataDumpSchedulerExecutorService,"getToEmailAddress","CUL");
        ReflectionTestUtils.invokeMethod(dataDumpSchedulerExecutorService,"getToEmailAddress","PUL");
        assertTrue(true);
    }

    @Test
    public void testInitiateDataDumpForScheduler() {
        try {
            dataDumpSchedulerExecutorService.initiateDataDumpForScheduler(new Date().toString(), "PUL", "fetchType");
            dataDumpSchedulerExecutorService.initiateDataDumpForScheduler(new Date().toString(), "CUL", "fetchType");
            dataDumpSchedulerExecutorService.initiateDataDumpForScheduler(new Date().toString(), "NYPL", "fetchType");
            dataDumpSchedulerExecutorService.initiateDataDumpForScheduler(new Date().toString(), "NYPLL", "fetchType");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
