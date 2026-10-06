package org.recap.model.csv;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by hemalathas on 18/4/17.
 */
public class DataDumpFailureReportUT extends BaseTestCaseUT {

    @Test
    public void testDataDumpFailureReport() {
        DataDumpFailureReport dataDumpFailureReport = new DataDumpFailureReport();
        dataDumpFailureReport.setInstitutionCodes("PUL");
        dataDumpFailureReport.setRequestingInstitution("CUL");
        dataDumpFailureReport.setFetchType("1");
        dataDumpFailureReport.setExportFromDate(new Date().toString());
        dataDumpFailureReport.setCollectionGroupIds("Open");
        dataDumpFailureReport.setTransmissionType("1");
        dataDumpFailureReport.setImsDepositoryCodes("IMDC");
        dataDumpFailureReport.setExportFormat("1");
        dataDumpFailureReport.setFailedBibs("test");
        dataDumpFailureReport.setFailureCause("test");

        assertNotNull(dataDumpFailureReport.getInstitutionCodes());
        assertNotNull(dataDumpFailureReport.getRequestingInstitution());
        assertNotNull(dataDumpFailureReport.getFetchType());
        assertNotNull(dataDumpFailureReport.getExportFromDate());
        assertNotNull(dataDumpFailureReport.getCollectionGroupIds());
        assertNotNull(dataDumpFailureReport.getTransmissionType());
        assertNotNull(dataDumpFailureReport.getExportFormat());
        assertNotNull(dataDumpFailureReport.getFailedBibs());
        assertNotNull(dataDumpFailureReport.getImsDepositoryCodes());
        assertNotNull(dataDumpFailureReport.getFailureCause());


    }

}