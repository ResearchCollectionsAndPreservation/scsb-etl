package org.recap.model.csv;

import org.junit.Test;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 18/4/17.
 */
public class SuccessReportReCAPCSVRecordUT extends BaseTestCase{

    @Test
    public void testSuccessReportReCAPCSVRecord(){
        SuccessReportReCAPCSVRecord successReportReCAPCSVRecord = new SuccessReportReCAPCSVRecord();
        successReportReCAPCSVRecord.setFileName("test");
        successReportReCAPCSVRecord.setTotalRecordsInFile("test");
        successReportReCAPCSVRecord.setTotalBibsLoaded("10");
        successReportReCAPCSVRecord.setTotalHoldingsLoaded("10");
        successReportReCAPCSVRecord.setTotalBibHoldingsLoaded("10");
        successReportReCAPCSVRecord.setTotalItemsLoaded("10");
        successReportReCAPCSVRecord.setTotalBibItemsLoaded("10");
        assertNotNull(successReportReCAPCSVRecord.getFileName());
        assertNotNull(successReportReCAPCSVRecord.getTotalRecordsInFile());
        assertNotNull(successReportReCAPCSVRecord.getTotalBibsLoaded());
        assertNotNull(successReportReCAPCSVRecord.getTotalHoldingsLoaded());
        assertNotNull(successReportReCAPCSVRecord.getTotalBibHoldingsLoaded());
        assertNotNull(successReportReCAPCSVRecord.getTotalItemsLoaded());
        assertNotNull(successReportReCAPCSVRecord.getTotalBibItemsLoaded());
    }

}