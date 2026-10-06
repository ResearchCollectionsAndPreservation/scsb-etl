package org.recap.model.csv;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DataExportFailureReportUT extends BaseTestCaseUT {

    @Test
    public void testDataExportFailureReport() {
        DataExportFailureReport dataExportFailureReport = new DataExportFailureReport();

        dataExportFailureReport.setFailureReason("Bad Request");
        dataExportFailureReport.setReportType("Full");
        dataExportFailureReport.setFilename("ExportReport");
        dataExportFailureReport.setOwningInstitutionBibId("1");
        dataExportFailureReport.setRequestingInstitutionCode("1");

        assertNotNull(dataExportFailureReport.getFailureReason());
        assertNotNull(dataExportFailureReport.getReportType());
        assertNotNull(dataExportFailureReport.getFilename());
        assertNotNull(dataExportFailureReport.getOwningInstitutionBibId());
        assertNotNull(dataExportFailureReport.getRequestingInstitutionCode());
    }
}
