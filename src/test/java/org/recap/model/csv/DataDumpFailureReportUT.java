package org.recap.model.csv;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 18/4/17.
 */
public class DataDumpFailureReportUT extends BaseTestCase{

    @Test
    public void testDataDumpFailureReport(){
        DataDumpFailureReport dataDumpFailureReport = new DataDumpFailureReport();
        dataDumpFailureReport.setInstitutionCodes("PUL");
        dataDumpFailureReport.setRequestingInstitution("CUL");
        dataDumpFailureReport.setFetchType("1");
        dataDumpFailureReport.setExportFromDate(new Date().toString());
        dataDumpFailureReport.setCollectionGroupIds("Open");
        dataDumpFailureReport.setTransmissionType("1");
        dataDumpFailureReport.setExportFormat("1");
        dataDumpFailureReport.setToEmailId("hemalatha.s@htcindia.com");
        dataDumpFailureReport.setFailedBibs("test");
        dataDumpFailureReport.setFailureCause("test");

        assertNotNull(dataDumpFailureReport.getInstitutionCodes());
        assertNotNull(dataDumpFailureReport.getRequestingInstitution());
        assertNotNull(dataDumpFailureReport.getFetchType());
        assertNotNull(dataDumpFailureReport.getExportFromDate());
        assertNotNull(dataDumpFailureReport.getCollectionGroupIds());
        assertNotNull(dataDumpFailureReport.getTransmissionType());
        assertNotNull(dataDumpFailureReport.getExportFormat());
        assertNotNull(dataDumpFailureReport.getToEmailId());
        assertNotNull(dataDumpFailureReport.getFailedBibs());
        assertNotNull(dataDumpFailureReport.getFailureCause());


    }

}