package org.recap.model.csv;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FailureReportSCSBCSVRecordUT extends BaseTestCaseUT {

    @Test
    public void testFailureReportReCAPCSVRecord() {
        FailureReportSCSBCSVRecord failureReportSCSBCSVRecord = new FailureReportSCSBCSVRecord();

        failureReportSCSBCSVRecord.setCollectionGroupDesignation("CGD");
        failureReportSCSBCSVRecord.setCustomerCode("1333");
        failureReportSCSBCSVRecord.setCreateDateItem("test");
        failureReportSCSBCSVRecord.setErrorDescription("Error");
        failureReportSCSBCSVRecord.setExceptionMessage("Exception");
        failureReportSCSBCSVRecord.setItemBarcode("23452");
        failureReportSCSBCSVRecord.setLastUpdatedDateItem(new Date().toString());
        failureReportSCSBCSVRecord.setOwningInstitution("23452");
        failureReportSCSBCSVRecord.setLocalItemId("23452");
        failureReportSCSBCSVRecord.setOwningInstitutionBibId("1");
        failureReportSCSBCSVRecord.setOwningInstitutionHoldingsId("1");
        failureReportSCSBCSVRecord.setTitle("Title");

        assertNotNull(failureReportSCSBCSVRecord.getExceptionMessage());
        assertNotNull(failureReportSCSBCSVRecord.getOwningInstitutionBibId());
        assertNotNull(failureReportSCSBCSVRecord.getCollectionGroupDesignation());
        assertNotNull(failureReportSCSBCSVRecord.getCustomerCode());
        assertNotNull(failureReportSCSBCSVRecord.getCreateDateItem());
        assertNotNull(failureReportSCSBCSVRecord.getErrorDescription());
        assertNotNull(failureReportSCSBCSVRecord.getItemBarcode());
        assertNotNull(failureReportSCSBCSVRecord.getLocalItemId());
        assertNotNull(failureReportSCSBCSVRecord.getOwningInstitutionHoldingsId());
        assertNotNull(failureReportSCSBCSVRecord.getOwningInstitution());
        assertNotNull(failureReportSCSBCSVRecord.getLastUpdatedDateItem());
        assertNotNull(failureReportSCSBCSVRecord.getTitle());
    }
}
