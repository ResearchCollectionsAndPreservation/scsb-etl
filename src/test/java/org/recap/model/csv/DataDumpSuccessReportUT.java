package org.recap.model.csv;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by hemalathas on 19/4/17.
 */
public class DataDumpSuccessReportUT extends BaseTestCaseUT {


    @Test
    public void testDataDumpSuccessReport() {
        DataDumpSuccessReport dataDumpSuccessReport = new DataDumpSuccessReport();
        dataDumpSuccessReport.setInstitutionCodes("PUL");
        dataDumpSuccessReport.setRequestingInstitution("CUL");
        dataDumpSuccessReport.setFetchType("1");
        dataDumpSuccessReport.setExportFromDate(new Date().toString());
        dataDumpSuccessReport.setCollectionGroupIds("1");
        dataDumpSuccessReport.setTransmissionType("1");
        dataDumpSuccessReport.setImsDepositoryCodes("IMDC");
        dataDumpSuccessReport.setExportFormat("1");
        dataDumpSuccessReport.setNoOfBibsExported("5");
        dataDumpSuccessReport.setExportedItemCount("3");

        assertNotNull(dataDumpSuccessReport.getInstitutionCodes());
        assertNotNull(dataDumpSuccessReport.getRequestingInstitution());
        assertNotNull(dataDumpSuccessReport.getFetchType());
        assertNotNull(dataDumpSuccessReport.getExportFromDate());
        assertNotNull(dataDumpSuccessReport.getCollectionGroupIds());
        assertNotNull(dataDumpSuccessReport.getTransmissionType());
        assertNotNull(dataDumpSuccessReport.getExportFormat());
        assertNotNull(dataDumpSuccessReport.getImsDepositoryCodes());
        assertNotNull(dataDumpSuccessReport.getExportedItemCount());
        assertNotNull(dataDumpSuccessReport.getNoOfBibsExported());
    }

}