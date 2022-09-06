package org.recap.controller;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.camel.EtlDataLoadProcessor;
import org.recap.camel.RecordProcessor;
import org.recap.model.etl.EtlLoadRequest;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;
import org.recap.report.ReportGenerator;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.repository.HoldingsDetailsRepository;
import org.recap.repository.ItemDetailsRepository;
import org.recap.repository.XmlRecordRepository;
import org.recap.repositoryrw.ReportDetailRepository;
import org.recap.util.CommonUtil;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;

import java.beans.PropertyEditor;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertNotNull;

/**
 * Created by chenchulakshmig on 14/7/16.
 */

public class EtlDataLoadControllerUT extends BaseTestCaseUT{

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
    @Mock
    private CommonUtil commonUtil;
    @Value("${etl.report.directory}")
    private String reportDirectory;

    @Mock
    HoldingsDetailsRepository holdingsDetailsRepository;
    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    ProducerTemplate producer;

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
    public void getInstitution() {
        Mockito.when(commonUtil.findAllInstitutionCodesExceptSupportInstitution()).thenReturn(Arrays.asList("PUL", "CUL", "NYPL", "HL"));
        List<String> institutionListExceptSupportInstitution = etlDataLoadController.getInstitution();
        assertNotNull(institutionListExceptSupportInstitution);
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
    public void testReports() throws Exception {
        String[] filename = {"test", ""};
        for (String file :
                filename) {
            Mockito.when(reportGenerator.generateReport(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(file);
            String fileName = "test.xml";
            List<ReportDataEntity> reportDataEntities = new ArrayList<>();
            ReportEntity reportEntity = new ReportEntity();
            reportEntity.setFileName(fileName);
            reportEntity.setCreatedDate(new Date());
            reportEntity.setType(ScsbCommonConstants.FAILURE);
            reportEntity.setInstitutionName("NYPL");

            ReportDataEntity reportDataEntity = new ReportDataEntity();
            reportDataEntity.setHeaderName(ScsbCommonConstants.ITEM_BARCODE);
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
            etlLoadRequest.setReportType(ScsbCommonConstants.FAILURE);
            etlLoadRequest.setDateFrom(from);
            etlLoadRequest.setDateTo(to);
            etlLoadRequest.setTransmissionType(ScsbCommonConstants.FILE_SYSTEM);
            etlLoadRequest.setOwningInstitutionName("NYPL");
            etlLoadRequest.setReportInstitutionName("NYPL");
            etlLoadRequest.setOperationType("ETL");
            String dateString = new SimpleDateFormat(ScsbCommonConstants.DATE_FORMAT_FOR_FILE_NAME).format(new Date());
            String reportFileName = "test" + "-Failure" + "-" + dateString + ".csv";

            etlDataLoadController.generateReport(etlLoadRequest, bindingResult, model);
            Thread.sleep(1000);

            assertNotNull(reportFileName);
        }
    }

    @Test
    public void testEtlLoadRequest() {
        EtlLoadRequest etlLoadRequest = new EtlLoadRequest();
        etlLoadRequest.setFileName("test");
        etlLoadRequest.setFile(multipartFile);
        etlLoadRequest.setUserName("john");
        etlLoadRequest.setReportFileName("test");
        etlLoadRequest.setReportType(ScsbCommonConstants.FAILURE);
        etlLoadRequest.setDateFrom(new Date());
        etlLoadRequest.setDateTo(new Date());
        etlLoadRequest.setTransmissionType(ScsbCommonConstants.FILE_SYSTEM);
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

    @Test
    public void bulkIngestTest(){
        EtlLoadRequest etlLoadRequest = new EtlLoadRequest();
        etlLoadRequest.setFileName("test");
        etlLoadRequest.setFile(multipartFile);
        etlLoadRequest.setUserName("john");
        etlLoadRequest.setReportFileName("test");
        etlLoadRequest.setReportType(ScsbCommonConstants.FAILURE);
        etlLoadRequest.setDateFrom(new Date());
        etlLoadRequest.setDateTo(new Date());
        etlLoadRequest.setTransmissionType(ScsbCommonConstants.FILE_SYSTEM);
        etlLoadRequest.setOwningInstitutionName("NYPL");
        etlLoadRequest.setReportInstitutionName("NYPL");
        etlLoadRequest.setOperationType("ETL");
        etlLoadRequest.setBatchSize(1);

        BindingResult result = new BindingResult() {
            @Override
            public Object getTarget() {
                return null;
            }

            @Override
            public Map<String, Object> getModel() {
                return null;
            }

            @Override
            public Object getRawFieldValue(String field) {
                return null;
            }

            @Override
            public PropertyEditor findEditor(String field, Class<?> valueType) {
                return null;
            }

            @Override
            public PropertyEditorRegistry getPropertyEditorRegistry() {
                return null;
            }

            @Override
            public String[] resolveMessageCodes(String errorCode) {
                return new String[0];
            }

            @Override
            public String[] resolveMessageCodes(String errorCode, String field) {
                return new String[0];
            }

            @Override
            public void addError(ObjectError error) {

            }

            @Override
            public String getObjectName() {
                return null;
            }

            @Override
            public void setNestedPath(String nestedPath) {

            }

            @Override
            public String getNestedPath() {
                return null;
            }

            @Override
            public void pushNestedPath(String subPath) {

            }

            @Override
            public void popNestedPath() throws IllegalStateException {

            }

            @Override
            public void reject(String errorCode) {

            }

            @Override
            public void reject(String errorCode, String defaultMessage) {

            }

            @Override
            public void reject(String errorCode, Object[] errorArgs, String defaultMessage) {

            }

            @Override
            public void rejectValue(String field, String errorCode) {

            }

            @Override
            public void rejectValue(String field, String errorCode, String defaultMessage) {

            }

            @Override
            public void rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage) {

            }

            @Override
            public void addAllErrors(Errors errors) {

            }

            @Override
            public boolean hasErrors() {
                return false;
            }

            @Override
            public int getErrorCount() {
                return 0;
            }

            @Override
            public List<ObjectError> getAllErrors() {
                return null;
            }

            @Override
            public boolean hasGlobalErrors() {
                return false;
            }

            @Override
            public int getGlobalErrorCount() {
                return 0;
            }

            @Override
            public List<ObjectError> getGlobalErrors() {
                return null;
            }

            @Override
            public ObjectError getGlobalError() {
                return null;
            }

            @Override
            public boolean hasFieldErrors() {
                return false;
            }

            @Override
            public int getFieldErrorCount() {
                return 0;
            }

            @Override
            public List<FieldError> getFieldErrors() {
                return null;
            }

            @Override
            public FieldError getFieldError() {
                return null;
            }

            @Override
            public boolean hasFieldErrors(String field) {
                return false;
            }

            @Override
            public int getFieldErrorCount(String field) {
                return 0;
            }

            @Override
            public List<FieldError> getFieldErrors(String field) {
                return null;
            }

            @Override
            public FieldError getFieldError(String field) {
                return null;
            }

            @Override
            public Object getFieldValue(String field) {
                return null;
            }

            @Override
            public Class<?> getFieldType(String field) {
                return null;
            }
        };

        Model model = new Model() {
            @Override
            public Model addAttribute(String attributeName, Object attributeValue) {
                return null;
            }

            @Override
            public Model addAttribute(Object attributeValue) {
                return null;
            }

            @Override
            public Model addAllAttributes(Collection<?> attributeValues) {
                return null;
            }

            @Override
            public Model addAllAttributes(Map<String, ?> attributes) {
                return null;
            }

            @Override
            public Model mergeAttributes(Map<String, ?> attributes) {
                return null;
            }

            @Override
            public boolean containsAttribute(String attributeName) {
                return false;
            }

            @Override
            public Object getAttribute(String attributeName) {
                return null;
            }

            @Override
            public Map<String, Object> asMap() {
                return null;
            }
        };

        EtlDataLoadProcessor etlDataLoadProcessor = new EtlDataLoadProcessor();
        etlDataLoadProcessor.setBatchSize(123);
        etlDataLoadProcessor.setFileName("fileName");
        etlDataLoadProcessor.setInstitutionName("HD");
        etlDataLoadProcessor.setXmlRecordRepository(xmlRecordRepository);
        etlDataLoadProcessor.setBibliographicDetailsRepository(bibliographicDetailsRepository);
        etlDataLoadProcessor.setHoldingsDetailsRepository(holdingsDetailsRepository);
        etlDataLoadProcessor.setItemDetailsRepository(itemDetailsRepository);
        etlDataLoadProcessor.setProducer(producer);
        etlDataLoadProcessor.setRecordProcessor(recordProcessor);
        etlDataLoadProcessor.startLoadProcess();

        etlDataLoadController.bulkIngest(etlLoadRequest, result, model);
    }
}