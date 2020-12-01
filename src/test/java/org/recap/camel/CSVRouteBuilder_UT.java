package org.recap.camel;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.csv.FailureReportReCAPCSVRecord;
import org.recap.model.csv.ReCAPCSVFailureRecord;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;
import org.recap.repositoryrw.ReportDetailRepository;
import org.recap.util.ReCAPCSVFailureRecordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by peris on 8/16/16.
 */
public class CSVRouteBuilder_UT extends BaseTestCase {


    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Autowired
    ProducerTemplate producer;

    @Autowired
    ConsumerTemplate consumer;

    @Value("${etl.report.directory}")
    private String reportDirectory;

    @Test
    public void generateCSV() throws Exception {
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();

        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName("test.xml");
        reportEntity.setCreatedDate(new Date());
        reportEntity.setType(RecapCommonConstants.FAILURE);
        reportEntity.setInstitutionName("PUL");

        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setHeaderName(RecapCommonConstants.ITEM_BARCODE);
        reportDataEntity.setHeaderValue("103");
        reportDataEntities.add(reportDataEntity);

        ReportDataEntity reportDataEntity2 = new ReportDataEntity();
        reportDataEntity2.setHeaderName(RecapCommonConstants.CUSTOMER_CODE);
        reportDataEntity2.setHeaderValue("PA");
        reportDataEntities.add(reportDataEntity2);

        ReportDataEntity reportDataEntity3 = new ReportDataEntity();
        reportDataEntity3.setHeaderName(RecapCommonConstants.LOCAL_ITEM_ID);
        reportDataEntity3.setHeaderValue("10412");
        reportDataEntities.add(reportDataEntity3);

        ReportDataEntity reportDataEntity4 = new ReportDataEntity();
        reportDataEntity4.setHeaderName(RecapCommonConstants.OWNING_INSTITUTION);
        reportDataEntity4.setHeaderValue("PUL");
        reportDataEntities.add(reportDataEntity4);

        ReportDataEntity reportDataEntity5 = new ReportDataEntity();
        reportDataEntity5.setHeaderName(RecapCommonConstants.ERROR_DESCRIPTION);
        reportDataEntity5.setHeaderValue("At least one $245 must be present.");
        reportDataEntities.add(reportDataEntity5);

        reportEntity.setReportDataEntities(reportDataEntities);

        reportDetailRepository.save(reportEntity);

        List<ReportEntity> byFileName = reportDetailRepository.findByFileName("test.xml");

        ReportEntity savedReportEntity = byFileName.get(0);

        FailureReportReCAPCSVRecord failureReportReCAPCSVRecord = new ReCAPCSVFailureRecordGenerator().prepareFailureReportReCAPCSVRecord(savedReportEntity);
        ReCAPCSVFailureRecord reCAPCSVFailureRecord = new ReCAPCSVFailureRecord();
        reCAPCSVFailureRecord.setFailureReportReCAPCSVRecordList(Arrays.asList(failureReportReCAPCSVRecord));
        reCAPCSVFailureRecord.setFileName("test.xml");
        reCAPCSVFailureRecord.setInstitutionName("PUL");
        reCAPCSVFailureRecord.setReportType(RecapCommonConstants.FAILURE);


        producer.sendBody(RecapConstants.CSV_FAILURE_Q, reCAPCSVFailureRecord);

        Thread.sleep(1000);

        String ddMMMyyyy = new SimpleDateFormat(RecapCommonConstants.DATE_FORMAT_FOR_FILE_NAME).format(new Date());
        String expectedGeneratedFileName = "test"+"-Failure"+"-"+ddMMMyyyy+".csv";

        File directory = new File(reportDirectory);
        assertTrue(directory.isDirectory());

        boolean directoryContains = new File(directory, expectedGeneratedFileName).exists();
        assertTrue(directory.isDirectory());

       // FileUtils.forceDelete(new File(reportDirectory+File.separator+expectedGeneratedFileName));

    }
}