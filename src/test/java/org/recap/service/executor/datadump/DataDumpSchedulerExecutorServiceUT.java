package org.recap.service.executor.datadump;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.util.datadump.*;
import org.springframework.beans.factory.annotation.*;

import java.util.Date;

import static org.junit.Assert.assertTrue;

public class DataDumpSchedulerExecutorServiceUT extends BaseTestCase {

    @Autowired
    DataDumpSchedulerExecutorService dataDumpSchedulerExecutorService;


    @Test
    public void testServices(){
        dataDumpSchedulerExecutorService.getDataDumpExportService();
        dataDumpSchedulerExecutorService.getJobDataParameterUtil();
        assertTrue(true);
    }
    @Test
    public void testInitiateDataDumpForScheduler(){
        try {
            dataDumpSchedulerExecutorService.initiateDataDumpForScheduler(new Date().toString(),"PUL","fetchType");
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
