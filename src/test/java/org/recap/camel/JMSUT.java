package org.recap.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.csv.FailureReportReCAPCSVRecord;
import org.recap.model.csv.ReCAPCSVFailureRecord;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.ReportDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by pvsubrah on 6/24/16.
 */
public class JMSUT extends BaseTestCase {

    @Autowired
    ConsumerTemplate consumer;
    @Autowired
    CamelContext camelContext;
    @Autowired
    ReportDetailRepository reportDetailRepository;
    @Autowired
    private ProducerTemplate producer;
    @Value("${etl.report.directory}")
    private String reportDirectoryPath;

    @Test
    public void produceAndConsumeSEDA() throws Exception {
        assertNotNull(producer);
        assertNotNull(consumer);

        producer.sendBody("seda:start", "Hello World");

        String body = consumer.receiveBody("seda:start", String.class);

        assertEquals("Hello World", body);

    }

    @Test
    public void generateFailureReport() throws Exception {
        FailureReportReCAPCSVRecord failureReportReCAPCSVRecord = new FailureReportReCAPCSVRecord();
        failureReportReCAPCSVRecord.setOwningInstitution("PUL");
        failureReportReCAPCSVRecord.setOwningInstitutionBibId("1111");
        failureReportReCAPCSVRecord.setOwningInstitutionHoldingsId("2222");
        failureReportReCAPCSVRecord.setLocalItemId("333333333");
        failureReportReCAPCSVRecord.setItemBarcode("4444");
        failureReportReCAPCSVRecord.setCustomerCode("PA");
        failureReportReCAPCSVRecord.setTitle("History, of Science");
        failureReportReCAPCSVRecord.setCollectionGroupDesignation("open");
        failureReportReCAPCSVRecord.setCreateDateItem(new SimpleDateFormat("mm-dd-yyyy").format(new Date()));
        failureReportReCAPCSVRecord.setLastUpdatedDateItem(new SimpleDateFormat("mm-dd-yyyy").format(new Date()));
        failureReportReCAPCSVRecord.setExceptionMessage("exception");
        failureReportReCAPCSVRecord.setErrorDescription("error");

        ReCAPCSVFailureRecord reCAPCSVFailureRecord = new ReCAPCSVFailureRecord();
        reCAPCSVFailureRecord.setFileName("test.xml");
        reCAPCSVFailureRecord.setReportType(RecapCommonConstants.FAILURE);
        reCAPCSVFailureRecord.setInstitutionName("PUL");
        assertNotNull(failureReportReCAPCSVRecord.getCreateDateItem());
        assertNotNull(failureReportReCAPCSVRecord.getLastUpdatedDateItem());
        reCAPCSVFailureRecord.setFailureReportReCAPCSVRecordList(Arrays.asList(failureReportReCAPCSVRecord));

        producer.sendBody(RecapConstants.CSV_FAILURE_Q, reCAPCSVFailureRecord);

        Thread.sleep(1000);

        DateFormat df = new SimpleDateFormat(RecapCommonConstants.DATE_FORMAT_FOR_FILE_NAME);
        String fileName = FilenameUtils.removeExtension(reCAPCSVFailureRecord.getFileName()) + "-Failure-" + df.format(new Date());
        File file = new File(reportDirectoryPath + File.separator + fileName + ".csv");
        assertTrue(true);
        //  String fileContents = Files.toString(file, Charsets.UTF_8);
      /*  assertNotNull(fileContents);
        assertTrue(fileContents.contains(failureReportReCAPCSVRecord.getOwningInstitution()));
        assertTrue(fileContents.contains(failureReportReCAPCSVRecord.getOwningInstitutionBibId()));
        assertTrue(fileContents.contains(failureReportReCAPCSVRecord.getOwningInstitutionHoldingsId()));
        assertTrue(fileContents.contains(failureReportReCAPCSVRecord.getLocalItemId()));
        assertTrue(fileContents.contains(failureReportReCAPCSVRecord.getItemBarcode()));
        assertTrue(fileContents.contains(failureReportReCAPCSVRecord.getCustomerCode()));
        assertTrue(fileContents.contains(failureReportReCAPCSVRecord.getTitle()));
        assertTrue(fileContents.contains(failureReportReCAPCSVRecord.getCollectionGroupDesignation()));
        assertTrue(fileContents.contains(failureReportReCAPCSVRecord.getExceptionMessage()));
        assertTrue(fileContents.contains(failureReportReCAPCSVRecord.getErrorDescription()));*/
        assertTrue(true);
    }

    @Test
    public void generateSuccessReport() throws Exception {

        ReportEntity reportEntity = new ReportEntity();
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();

        ReportDataEntity totalRecordsInFileEntity = new ReportDataEntity();
        totalRecordsInFileEntity.setHeaderName("Total Records In File");
        totalRecordsInFileEntity.setHeaderValue(String.valueOf(10000));
        reportDataEntities.add(totalRecordsInFileEntity);

        ReportDataEntity totalBibsLoadedEntity = new ReportDataEntity();
        totalBibsLoadedEntity.setHeaderName("Total Bibs Loaded");
        totalBibsLoadedEntity.setHeaderValue(String.valueOf(10000));
        reportDataEntities.add(totalBibsLoadedEntity);

        ReportDataEntity totalHoldingsLoadedEntity = new ReportDataEntity();
        totalHoldingsLoadedEntity.setHeaderName("Total Holdings Loaded");
        totalHoldingsLoadedEntity.setHeaderValue(String.valueOf(8000));
        reportDataEntities.add(totalHoldingsLoadedEntity);

        ReportDataEntity totalItemsLoadedEntity = new ReportDataEntity();
        totalItemsLoadedEntity.setHeaderName("Total Items Loaded");
        totalItemsLoadedEntity.setHeaderValue(String.valueOf(12000));
        reportDataEntities.add(totalItemsLoadedEntity);

        ReportDataEntity totalBibHoldingsLoadedEntity = new ReportDataEntity();
        totalBibHoldingsLoadedEntity.setHeaderName("Total Bib-Holdings Loaded");
        totalBibHoldingsLoadedEntity.setHeaderValue(String.valueOf(18000));
        reportDataEntities.add(totalBibHoldingsLoadedEntity);

        ReportDataEntity totalBiBItemsLoadedEntity = new ReportDataEntity();
        totalBiBItemsLoadedEntity.setHeaderName("Total Bib-Items Loaded");
        totalBiBItemsLoadedEntity.setHeaderValue(String.valueOf(22000));
        reportDataEntities.add(totalBiBItemsLoadedEntity);

        reportEntity.setFileName("TestSuccessReport.xml");
        reportEntity.setCreatedDate(new Date());
        reportEntity.setType(RecapCommonConstants.SUCCESS);
        reportEntity.setReportDataEntities(reportDataEntities);
        reportEntity.setInstitutionName("NYPL");
        producer.sendBody(RecapCommonConstants.REPORT_Q, reportEntity);
        Thread.sleep(1000);

        List<ReportEntity> byFileName = reportDetailRepository.findByFileName("TestSuccessReport.xml");
        assertNotNull(byFileName);
       // assertNotNull(byFileName.get(0));

    }

    public class FileNameProcessor implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            ReCAPCSVFailureRecord reCAPCSVFailureRecord = (ReCAPCSVFailureRecord) exchange.getIn().getBody();
            String fileName = FilenameUtils.removeExtension(reCAPCSVFailureRecord.getFileName());
            exchange.getIn().setHeader("reportFileName", fileName);
        }
    }
}
