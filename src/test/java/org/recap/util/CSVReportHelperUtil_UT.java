package org.recap.util;

import org.junit.Test;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.csv.FailureReportReCAPCSVRecord;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by peris on 8/16/16.
 */
public class CSVReportHelperUtil_UT {

    @Test
    public void generateCSVRecord() throws Exception {
        ReCAPCSVFailureRecordGenerator reCAPCSVRecordGenerator = new ReCAPCSVFailureRecordGenerator();

        ReportEntity reportEntity = new ReportEntity();
        List dataEntities = new ArrayList<>();

        ReportDataEntity barcodeReportDataEntity = new ReportDataEntity();
        barcodeReportDataEntity.setHeaderName(RecapCommonConstants.ITEM_BARCODE);
        barcodeReportDataEntity.setHeaderValue("1231");
        dataEntities.add(barcodeReportDataEntity);

        ReportDataEntity callNumberReportDataEntity = new ReportDataEntity();
        callNumberReportDataEntity.setHeaderName(RecapCommonConstants.LOCAL_ITEM_ID);
        callNumberReportDataEntity.setHeaderValue("1231");
        dataEntities.add(callNumberReportDataEntity);

        ReportDataEntity errrorDescriptionDataEntity = new ReportDataEntity();
        errrorDescriptionDataEntity.setHeaderName(RecapCommonConstants.ERROR_DESCRIPTION);
        errrorDescriptionDataEntity.setHeaderValue("At least on subfield should be there for $245");
        dataEntities.add(errrorDescriptionDataEntity);

        reportEntity.setReportDataEntities(dataEntities);

        FailureReportReCAPCSVRecord failureReportReCAPCSVRecord =
                reCAPCSVRecordGenerator.prepareFailureReportReCAPCSVRecord(reportEntity);

        assertNotNull(failureReportReCAPCSVRecord);

    }

}