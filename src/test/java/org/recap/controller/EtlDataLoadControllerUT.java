package org.recap.controller;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.etl.EtlLoadRequest;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.XmlRecordEntity;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.repository.ReportDetailRepository;
import org.recap.repository.XmlRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by chenchulakshmig on 14/7/16.
 */
public class EtlDataLoadControllerUT extends BaseTestCase {

    @Autowired
    EtlDataLoadController etlDataLoadController;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    XmlRecordRepository xmlRecordRepository;

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Mock
    MultipartFile multipartFile;

    @Value("${etl.report.directory}")
    private String reportDirectory;

    @Mock
    Model model;

    @Mock
    BindingResult bindingResult;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void uploadFiles() throws Exception {
        assertNotNull(etlDataLoadController);

        URL resource = getClass().getResource("SampleRecord.xml");
        assertNotNull(resource);
        File file = new File(resource.toURI());
        assertNotNull(file);

        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "text/plain", IOUtils.toByteArray(input));
        assertNotNull(multipartFile);

        EtlLoadRequest etlLoadRequest = new EtlLoadRequest();
        etlLoadRequest.setFile(multipartFile);

        etlDataLoadController.uploadFiles(etlLoadRequest, bindingResult, model);

        Thread.sleep(1000);

        Page<XmlRecordEntity> xmlRecordEntities = xmlRecordRepository.findByXmlFileName(PageRequest.of(0, 10), "SampleRecord.xml");
        assertNotNull(xmlRecordEntities);
        List<XmlRecordEntity> xmlRecordEntityList = xmlRecordEntities.getContent();
        assertNotNull(xmlRecordEntityList);
        assertTrue(xmlRecordEntityList.size() > 0);
    }

    @Test
    public void testBulkIngest() throws Exception {
        uploadFiles();
        EtlLoadRequest etlLoadRequest = new EtlLoadRequest();
        etlLoadRequest.setFileName("SampleRecord.xml");
        etlLoadRequest.setBatchSize(1000);
        etlLoadRequest.setUserName(StringUtils.isBlank(etlLoadRequest.getUserName()) ? "etl" : etlLoadRequest.getUserName());
        etlLoadRequest.setOwningInstitutionName("NYPL");
        etlDataLoadController.bulkIngest(etlLoadRequest, bindingResult, model);
        Thread.sleep(1000);

        String report = etlDataLoadController.report();
        assertNotNull(report);
        System.out.println(report);

        BibliographicEntity bibliographicEntity = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(3, ".b153286131");
        assertNotNull(bibliographicEntity);
        assertNotNull(bibliographicEntity.getHoldingsEntities());
        assertNotNull(bibliographicEntity.getItemEntities());
    }

    @Test
    public void testReports() throws Exception {
        String fileName = "test.xml";

        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(fileName);
        reportEntity.setCreatedDate(new Date());
        reportEntity.setType(RecapConstants.FAILURE);
        reportEntity.setInstitutionName("NYPL");

        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setHeaderName(RecapConstants.ITEM_BARCODE);
        reportDataEntity.setHeaderValue("103");
        reportDataEntities.add(reportDataEntity);

        reportEntity.setReportDataEntities(reportDataEntities);

        ReportEntity savedReportEntity = reportDetailRepository.save(reportEntity);
        assertNotNull(savedReportEntity);

        Calendar cal = Calendar.getInstance();
        Date from = savedReportEntity.getCreatedDate();
        cal.setTime(from);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        from = cal.getTime();
        Date to = savedReportEntity.getCreatedDate();
        cal.setTime(to);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        to = cal.getTime();

        EtlLoadRequest etlLoadRequest = new EtlLoadRequest();
        etlLoadRequest.setReportFileName(fileName);
        etlLoadRequest.setReportType(RecapConstants.FAILURE);
        etlLoadRequest.setDateFrom(from);
        etlLoadRequest.setDateTo(to);
        etlLoadRequest.setTransmissionType(RecapConstants.FILE_SYSTEM);
        etlLoadRequest.setOwningInstitutionName("NYPL");
        etlLoadRequest.setReportInstitutionName("NYPL");
        etlLoadRequest.setOperationType("ETL");
        String dateString = new SimpleDateFormat(RecapConstants.DATE_FORMAT_FOR_FILE_NAME).format(new Date());
        String reportFileName = "test"+"-Failure"+"-"+dateString+".csv";

        etlDataLoadController.generateReport(etlLoadRequest, bindingResult, model);
        Thread.sleep(1000);

        assertNotNull(reportFileName);

        File directory = new File(reportDirectory);
        assertTrue(directory.isDirectory());

        boolean directoryContains = new File(directory, reportFileName).exists();
        assertTrue(directoryContains);
    }

    @Test
    public void testEtlLoadRequest(){
        EtlLoadRequest etlLoadRequest = new EtlLoadRequest();
        etlLoadRequest.setFileName("test");
        etlLoadRequest.setFile(multipartFile);
        etlLoadRequest.setUserName("john");
        etlLoadRequest.setReportFileName("test");
        etlLoadRequest.setReportType(RecapConstants.FAILURE);
        etlLoadRequest.setDateFrom(new Date());
        etlLoadRequest.setDateTo(new Date());
        etlLoadRequest.setTransmissionType(RecapConstants.FILE_SYSTEM);
        etlLoadRequest.setOwningInstitutionName("NYPL");
        etlLoadRequest.setReportInstitutionName("NYPL");
        etlLoadRequest.setOperationType("ETL");
        etlLoadRequest.setBatchSize(1);
        assertNotNull(etlLoadRequest.getFileName());
        assertNotNull(etlLoadRequest.getBatchSize());
        assertNotNull(etlLoadRequest.getFile());
        assertNotNull(etlLoadRequest.getUserName());
        assertNotNull(etlLoadRequest.getOwningInstitutionName());
        assertNotNull(etlLoadRequest.getReportFileName());
        assertNotNull(etlLoadRequest.getReportType());
        assertNotNull(etlLoadRequest.getOperationType());
        assertNotNull(etlLoadRequest.getTransmissionType());
        assertNotNull(etlLoadRequest.getReportInstitutionName());
        assertNotNull(etlLoadRequest.getDateFrom());
        assertNotNull(etlLoadRequest.getDateTo());
    }

}