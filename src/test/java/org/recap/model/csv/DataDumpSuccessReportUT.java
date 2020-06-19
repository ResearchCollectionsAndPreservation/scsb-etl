package org.recap.model.csv;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 19/4/17.
 */
public class DataDumpSuccessReportUT extends BaseTestCase{


    @Test
    public void testDataDumpSuccessReport(){
        DataDumpSuccessReport dataDumpSuccessReport = new DataDumpSuccessReport();
        dataDumpSuccessReport.setInstitutionCodes("PUL");
        dataDumpSuccessReport.setRequestingInstitution("CUL");
        dataDumpSuccessReport.setFetchType("1");
        dataDumpSuccessReport.setExportFromDate(new Date().toString());
        dataDumpSuccessReport.setCollectionGroupIds("1");
        dataDumpSuccessReport.setTransmissionType("1");
        dataDumpSuccessReport.setExportFormat("1");
        dataDumpSuccessReport.setToEmailId("hemalatha.s@htcindia.com");
        dataDumpSuccessReport.setNoOfBibsExported("5");

        assertNotNull(dataDumpSuccessReport.getInstitutionCodes());
        assertNotNull(dataDumpSuccessReport.getRequestingInstitution());
        assertNotNull(dataDumpSuccessReport.getFetchType());
        assertNotNull(dataDumpSuccessReport.getExportFromDate());
        assertNotNull(dataDumpSuccessReport.getCollectionGroupIds());
        assertNotNull(dataDumpSuccessReport.getTransmissionType());
        assertNotNull(dataDumpSuccessReport.getExportFormat());
        assertNotNull(dataDumpSuccessReport.getToEmailId());
        assertNotNull(dataDumpSuccessReport.getNoOfBibsExported());
    }

}