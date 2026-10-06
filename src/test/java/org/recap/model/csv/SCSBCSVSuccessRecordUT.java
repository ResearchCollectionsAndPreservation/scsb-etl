package org.recap.model.csv;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SCSBCSVSuccessRecordUT extends BaseTestCaseUT {

    @Test
    public void testReCAPCSVSuccessRecord() {

        SCSBCSVSuccessRecord SCSBCSVSuccessRecord = new SCSBCSVSuccessRecord();

        SCSBCSVSuccessRecord.setSuccessReportSCSBCSVRecordList(Arrays.asList(new SuccessReportSCSBCSVRecord()));
        SCSBCSVSuccessRecord.setFileName("Record");
        SCSBCSVSuccessRecord.setInstitutionName("PUL");
        SCSBCSVSuccessRecord.setReportType("initial");
        SCSBCSVSuccessRecord.setReportFileName("ReportFile");
        SCSBCSVSuccessRecord.setTotalBibHoldingsLoaded(14662);
        SCSBCSVSuccessRecord.setTotalBibItemsLoaded(2677);
        SCSBCSVSuccessRecord.setTotalBibsLoaded(45234);
        SCSBCSVSuccessRecord.setTotalRecordsInFile(3352);
        SCSBCSVSuccessRecord.setTotalItemsLoaded(9473);
        SCSBCSVSuccessRecord.setTotalHoldingsLoaded(7355);

        assertNotNull(SCSBCSVSuccessRecord.getFileName());
        assertNotNull(SCSBCSVSuccessRecord.getInstitutionName());
        assertNotNull(SCSBCSVSuccessRecord.getSuccessReportSCSBCSVRecordList());
        assertNotNull(SCSBCSVSuccessRecord.getReportFileName());
        assertNotNull(SCSBCSVSuccessRecord.getReportType());
        assertNotNull(SCSBCSVSuccessRecord.getTotalRecordsInFile());
        assertNotNull(SCSBCSVSuccessRecord.getTotalBibHoldingsLoaded());
        assertNotNull(SCSBCSVSuccessRecord.getTotalBibItemsLoaded());
        assertNotNull(SCSBCSVSuccessRecord.getTotalBibsLoaded());
        assertNotNull(SCSBCSVSuccessRecord.getTotalItemsLoaded());
        assertNotNull(SCSBCSVSuccessRecord.getTotalHoldingsLoaded());

    }
}
