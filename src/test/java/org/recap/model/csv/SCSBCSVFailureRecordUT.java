package org.recap.model.csv;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SCSBCSVFailureRecordUT extends BaseTestCaseUT {

    @Test
    public void testReCAPCSVFailureRecord() {
        SCSBCSVFailureRecord SCSBCSVFailureRecord = new SCSBCSVFailureRecord();

        SCSBCSVFailureRecord.setFailureReportSCSBCSVRecordList(Arrays.asList(new FailureReportSCSBCSVRecord()));
        SCSBCSVFailureRecord.setLocalItemId("23456");
        SCSBCSVFailureRecord.setCollectionGroupDesignation("CGD");
        SCSBCSVFailureRecord.setCustomerCode("23456");
        SCSBCSVFailureRecord.setCreateDateItem(new Date());
        SCSBCSVFailureRecord.setFileName("Record");
        SCSBCSVFailureRecord.setErrorDescription("Error");
        SCSBCSVFailureRecord.setInstitutionName("PUL");
        SCSBCSVFailureRecord.setExceptionMessage("Exception");
        SCSBCSVFailureRecord.setItemBarcode("456823");
        SCSBCSVFailureRecord.setLastUpdatedDateItem(new Date());
        SCSBCSVFailureRecord.setOwningInstitution("PUL");
        SCSBCSVFailureRecord.setOwningInstitutionBibId("1");
        SCSBCSVFailureRecord.setOwningInstitutionHoldingsId("1");
        SCSBCSVFailureRecord.setReportType("initial");
        SCSBCSVFailureRecord.setTitle("Title");

        assertNotNull(SCSBCSVFailureRecord.getExceptionMessage());
        assertNotNull(SCSBCSVFailureRecord.getFailureReportSCSBCSVRecordList());
        assertNotNull(SCSBCSVFailureRecord.getCollectionGroupDesignation());
        assertNotNull(SCSBCSVFailureRecord.getCustomerCode());
        assertNotNull(SCSBCSVFailureRecord.getCreateDateItem());
        assertNotNull(SCSBCSVFailureRecord.getErrorDescription());
        assertNotNull(SCSBCSVFailureRecord.getFileName());
        assertNotNull(SCSBCSVFailureRecord.getCreateDateItem());
        assertNotNull(SCSBCSVFailureRecord.getInstitutionName());
        assertNotNull(SCSBCSVFailureRecord.getItemBarcode());
        assertNotNull(SCSBCSVFailureRecord.getLastUpdatedDateItem());
        assertNotNull(SCSBCSVFailureRecord.getLocalItemId());
        assertNotNull(SCSBCSVFailureRecord.getOwningInstitution());
        assertNotNull(SCSBCSVFailureRecord.getOwningInstitutionBibId());
        assertNotNull(SCSBCSVFailureRecord.getOwningInstitutionHoldingsId());
        assertNotNull(SCSBCSVFailureRecord.getReportType());
        assertNotNull(SCSBCSVFailureRecord.getTitle());

    }
}
