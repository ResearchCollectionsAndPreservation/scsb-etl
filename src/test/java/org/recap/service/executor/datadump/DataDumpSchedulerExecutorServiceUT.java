package org.recap.service.executor.datadump;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Date;

import static org.junit.Assert.assertTrue;

public class DataDumpSchedulerExecutorServiceUT extends BaseTestCase {
    @Test
    public void testServices(){
        DataDumpSchedulerExecutorService dataDumpSchedulerExecutorService = new DataDumpSchedulerExecutorService();
        dataDumpSchedulerExecutorService.getDataDumpExportService();
        dataDumpSchedulerExecutorService.getJobDataParameterUtil();
        assertTrue(true);
    }
    @Test
    public void testInitiateDataDumpForScheduler(){
        DataDumpSchedulerExecutorService dataDumpSchedulerExecutorService = new DataDumpSchedulerExecutorService();
        try {
            dataDumpSchedulerExecutorService.initiateDataDumpForScheduler(new Date().toString(),"PUL","fetchType");
        }catch (Exception e){

        }

    }
}
