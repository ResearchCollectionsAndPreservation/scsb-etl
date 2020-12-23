package org.recap.util.datadump;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;
import org.recap.model.csv.DataDumpSuccessReport;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DataDumpSuccessReportGeneratorUT extends BaseTestCaseUT {

    @InjectMocks
    DataDumpSuccessReportGenerator dataDumpSuccessReportGenerator;

    @Test
    public void prepareDataDumpCSVSuccessRecord(){
        ReportEntity reportEntity=new ReportEntity();
        List<ReportDataEntity> reportDataEntities=new ArrayList<>();
        ReportDataEntity reportDataEntity=new ReportDataEntity();
        reportDataEntity.setHeaderName("test");
        reportDataEntity.setHeaderValue("test");
        reportDataEntities.add(reportDataEntity);
        reportEntity.setReportDataEntities(reportDataEntities);
        DataDumpSuccessReport dataDumpSuccessReport=dataDumpSuccessReportGenerator.prepareDataDumpCSVSuccessRecord(reportEntity);
        assertNotNull(dataDumpSuccessReport);
    }

    @Test
    public void getGetterMethod(){
        Method getGetterMethod=dataDumpSuccessReportGenerator.getGetterMethod("test");
        assertNull(getGetterMethod);
    }
}
