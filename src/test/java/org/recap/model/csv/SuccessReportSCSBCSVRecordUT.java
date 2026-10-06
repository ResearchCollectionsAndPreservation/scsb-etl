package org.recap.model.csv;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by hemalathas on 18/4/17.
 */
public class SuccessReportSCSBCSVRecordUT extends BaseTestCaseUT {

    @Test
    public void testSuccessReportReCAPCSVRecord() {
        SuccessReportSCSBCSVRecord successReportSCSBCSVRecord = new SuccessReportSCSBCSVRecord();
        successReportSCSBCSVRecord.setFileName("test");
        successReportSCSBCSVRecord.setTotalRecordsInFile("test");
        successReportSCSBCSVRecord.setTotalBibsLoaded("10");
        successReportSCSBCSVRecord.setTotalHoldingsLoaded("10");
        successReportSCSBCSVRecord.setTotalBibHoldingsLoaded("10");
        successReportSCSBCSVRecord.setTotalItemsLoaded("10");
        successReportSCSBCSVRecord.setTotalBibItemsLoaded("10");
        assertNotNull(successReportSCSBCSVRecord.getFileName());
        assertNotNull(successReportSCSBCSVRecord.getTotalRecordsInFile());
        assertNotNull(successReportSCSBCSVRecord.getTotalBibsLoaded());
        assertNotNull(successReportSCSBCSVRecord.getTotalHoldingsLoaded());
        assertNotNull(successReportSCSBCSVRecord.getTotalBibHoldingsLoaded());
        assertNotNull(successReportSCSBCSVRecord.getTotalItemsLoaded());
        assertNotNull(successReportSCSBCSVRecord.getTotalBibItemsLoaded());
    }

}