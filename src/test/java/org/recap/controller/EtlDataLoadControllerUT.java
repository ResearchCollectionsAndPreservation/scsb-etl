package org.recap.controller;

import org.apache.camel.CamelContext;
import org.apache.camel.ServiceStatus;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.camel.RecordProcessor;
import org.recap.model.etl.EtlLoadRequest;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;
import org.recap.report.ReportGenerator;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.repositoryrw.ReportDetailRepository;
import org.recap.repository.XmlRecordRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
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

/**
 * Created by chenchulakshmig on 14/7/16.
 */


@RunWith(PowerMockRunner.class)
@PrepareForTest(ServiceStatus.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
public class EtlDataLoadControllerUT extends BaseTestCaseUT {

    @InjectMocks
    EtlDataLoadController etlDataLoadController;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    XmlRecordRepository xmlRecordRepository;

    @Mock
    ReportDetailRepository reportDetailRepository;

    @Mock
    MultipartFile multipartFile;

    @Value("${etl.report.directory}")
    private String reportDirectory;

    @Mock
    Model model;

    @Mock
    BindingResult bindingResult;

    @Mock
    RecordProcessor recordProcessor;

    @Mock
    CamelContext camelContext;

    @Mock
    ReportGenerator reportGenerator;


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
    }

    @Test
    public void uploadFiles1() throws Exception {
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
        etlDataLoadController.uploadFiles(etlLoadRequest, bindingResult, model);
    }

    @Test
    public void testBulkIngest() throws Exception {
        ReflectionTestUtils.setField(etlDataLoadController,"recordProcessor",recordProcessor);
        ServiceStatus serviceStatus= PowerMockito.mock(ServiceStatus.class);
        Mockito.when(camelContext.getStatus()).thenReturn(serviceStatus);
        Mockito.when(serviceStatus.isStarted()).thenReturn(true);
        uploadFiles();
        EtlLoadRequest etlLoadRequest = new EtlLoadRequest();
        etlLoadRequest.setFileName("SampleRecord.xml");
        etlLoadRequest.setBatchSize(1000);
        etlLoadRequest.setUserName(StringUtils.isBlank(etlLoadRequest.getUserName()) ? "etl" : etlLoadRequest.getUserName());
        etlLoadRequest.setOwningInstitutionName("NYPL");
        etlDataLoadController.bulkIngest(etlLoadRequest, bindingResult, model);

        String report = etlDataLoadController.report();
        assertNotNull(report);
    }

    @Test
    public void testReports() throws Exception {
        String[] filename={"test",""};
        for (String file:
        filename) {
            Mockito.when(reportGenerator.generateReport(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(file);
        String fileName = "test.xml";
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(fileName);
        reportEntity.setCreatedDate(new Date());
        reportEntity.setType(RecapCommonConstants.FAILURE);
        reportEntity.setInstitutionName("NYPL");

        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setHeaderName(RecapCommonConstants.ITEM_BARCODE);
        reportDataEntity.setHeaderValue("103");
        reportDataEntities.add(reportDataEntity);

        reportEntity.setReportDataEntities(reportDataEntities);

        Calendar cal = Calendar.getInstance();
        Date from = reportEntity.getCreatedDate();
        cal.setTime(from);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        from = cal.getTime();
        Date to = reportEntity.getCreatedDate();
        cal.setTime(to);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        to = cal.getTime();

        EtlLoadRequest etlLoadRequest = new EtlLoadRequest();
        etlLoadRequest.setReportFileName(fileName);
        etlLoadRequest.setReportType(RecapCommonConstants.FAILURE);
        etlLoadRequest.setDateFrom(from);
        etlLoadRequest.setDateTo(to);
        etlLoadRequest.setTransmissionType(RecapCommonConstants.FILE_SYSTEM);
        etlLoadRequest.setOwningInstitutionName("NYPL");
        etlLoadRequest.setReportInstitutionName("NYPL");
        etlLoadRequest.setOperationType("ETL");
        String dateString = new SimpleDateFormat(RecapCommonConstants.DATE_FORMAT_FOR_FILE_NAME).format(new Date());
        String reportFileName = "test"+"-Failure"+"-"+dateString+".csv";

        etlDataLoadController.generateReport(etlLoadRequest, bindingResult, model);
        Thread.sleep(1000);

        assertNotNull(reportFileName);
        }
    }

    @Test
    public void testEtlLoadRequest(){
        EtlLoadRequest etlLoadRequest = new EtlLoadRequest();
        etlLoadRequest.setFileName("test");
        etlLoadRequest.setFile(multipartFile);
        etlLoadRequest.setUserName("john");
        etlLoadRequest.setReportFileName("test");
        etlLoadRequest.setReportType(RecapCommonConstants.FAILURE);
        etlLoadRequest.setDateFrom(new Date());
        etlLoadRequest.setDateTo(new Date());
        etlLoadRequest.setTransmissionType(RecapCommonConstants.FILE_SYSTEM);
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