package org.recap.util;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbConstants;
import org.recap.model.csv.SuccessReportSCSBCSVRecord;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SCSBCSVSuccessRecordGeneratorUT extends BaseTestCaseUT {

    @InjectMocks
    SCSBCSVSuccessRecordGenerator SCSBCSVSuccessRecordGenerator;

    @Test
    public void prepareFailureReportReCAPCSVRecordInnerException() {
        ReportEntity reportEntity = getReportEntity();
        SuccessReportSCSBCSVRecord successReportSCSBCSVRecord = SCSBCSVSuccessRecordGenerator.prepareSuccessReportReCAPCSVRecord(reportEntity);
        assertNotNull(successReportSCSBCSVRecord);
    }

    @Test
    public void getGetterMethod() {
        Method method = SCSBCSVSuccessRecordGenerator.getGetterMethod(ScsbConstants.HEADER_FETCH_TYPE);
        assertNull(method);
    }

    private ReportEntity getReportEntity() {
        ReportEntity reportEntity = new ReportEntity();
        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setHeaderName(ScsbConstants.HEADER_FETCH_TYPE);
        reportDataEntity.setHeaderValue(ScsbConstants.DATADUMP_FETCHTYPE_INCREMENTAL);
        reportEntity.setReportDataEntities(Arrays.asList(reportDataEntity));
        reportEntity.setCreatedDate(new Date());
        return reportEntity;
    }
}
