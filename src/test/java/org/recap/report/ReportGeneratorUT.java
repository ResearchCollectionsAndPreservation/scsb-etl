package org.recap.report;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;
import org.recap.repositoryrw.ReportDetailRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by peris on 8/17/16.
 */
public class ReportGeneratorUT extends BaseTestCaseUT {

    @Mock
    ReportDetailRepository reportDetailRepository;

    private String fileName = "test.xml";

    @Value("${etl.report.directory}")
    private String reportDirectory;

    @Value("${etl.data.dump.report.directory}")
    private String dataDumpReportDirectory;

    @InjectMocks
    ReportGenerator reportGenerator;

    @InjectMocks
    CSVFailureReportGenerator csvFailureReportGenerator;

    /**
     * The Csv success report generator.
     */
    @InjectMocks
    CSVSuccessReportGenerator csvSuccessReportGenerator;

    /**
     * The Ftp failure report generator.
     */
    @InjectMocks
    S3FailureReportGenerator s3FailureReportGenerator;

    /**
     * The Ftp success report generator.
     */
    @InjectMocks
    S3SuccessReportGenerator s3SuccessReportGenerator;

    /**
     * The Csv data dump success report generator.
     */
    @InjectMocks
    CSVDataDumpSuccessReportGenerator csvDataDumpSuccessReportGenerator;

    /**
     * The Csv data dump failure report generator.
     */
    @InjectMocks
    CSVDataDumpFailureReportGenerator csvDataDumpFailureReportGenerator;

    /**
     * The Ftp data dump success report generator.
     */
    @InjectMocks
    S3DataDumpSuccessReportGenerator s3DataDumpSuccessReportGenerator;

    /**
     * The Ftp data dump failure report generator.
     */
    @InjectMocks
    S3DataDumpFailureReportGenerator s3DataDumpFailureReportGenerator;

    @Mock
    ProducerTemplate producerTemplate;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void generateFailureReportTest() throws Exception {
        ReportEntity savedReportEntity1 = saveFailureReportEntity();
        List<ReportEntity> reportEntities=new ArrayList<>();
        reportEntities.add(savedReportEntity1);
        Mockito.when(reportDetailRepository.findByFileAndInstitutionAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(reportEntities);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface=csvFailureReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        String generatedReportFileName = generateReport(savedReportEntity1.getCreatedDate(),RecapConstants.OPERATION_TYPE_ETL,savedReportEntity1.getType(), savedReportEntity1.getInstitutionName(), RecapCommonConstants.FILE_SYSTEM);
        assertNotNull(generatedReportFileName);
    }

    @Test
    public void generateSuccessReportTest() throws Exception {
        ReportEntity savedReportEntity1 = saveSuccessReportEntity();
        List<ReportEntity> reportEntities=new ArrayList<>();
        reportEntities.add(savedReportEntity1);
        Mockito.when(reportDetailRepository.findByFileAndInstitutionAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(reportEntities);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface=csvSuccessReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        String generatedReportFileName = generateReport(savedReportEntity1.getCreatedDate(),RecapConstants.OPERATION_TYPE_ETL,savedReportEntity1.getType(), savedReportEntity1.getInstitutionName(), RecapCommonConstants.FILE_SYSTEM);
        assertNotNull(generatedReportFileName);
    }

    @Test
    public void generateDataDumpFileSystemSuccessReportTest() throws Exception {
        ReportEntity savedReportEntity1 = saveDataDumpSuccessReport();
        List<ReportEntity> reportEntities=new ArrayList<>();
        reportEntities.add(savedReportEntity1);
        Mockito.when(reportDetailRepository.findByInstitutionAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(reportEntities);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface=csvDataDumpSuccessReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        String generatedReportFileName = dataDumpGenerateReport(savedReportEntity1.getCreatedDate(),RecapConstants.BATCH_EXPORT, RecapCommonConstants.SUCCESS, savedReportEntity1.getInstitutionName(), RecapCommonConstants.FILE_SYSTEM,savedReportEntity1.getFileName());
        assertNotNull(generatedReportFileName);
    }

    @Test
    public void generateDataDumpS3SuccessReportTest() throws Exception {
        ReportEntity savedReportEntity1 = saveDataDumpSuccessReport();
        List<ReportEntity> reportEntities=new ArrayList<>();
        reportEntities.add(savedReportEntity1);
        Mockito.when(reportDetailRepository.findByInstitutionAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(reportEntities);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface=s3DataDumpSuccessReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        String generatedReportFileName = dataDumpGenerateReport(savedReportEntity1.getCreatedDate(),RecapConstants.BATCH_EXPORT, RecapCommonConstants.SUCCESS, savedReportEntity1.getInstitutionName(), RecapCommonConstants.FTP,savedReportEntity1.getFileName());
        assertNotNull(generatedReportFileName);
    }

    @Test
    public void generateDataDumpFileSystemFailureReportTest() throws Exception {
        ReportEntity savedReportEntity1 = saveDataDumpFailureReport();
        List<ReportEntity> reportEntities=new ArrayList<>();
        reportEntities.add(savedReportEntity1);
        Mockito.when(reportDetailRepository.findByInstitutionAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(reportEntities);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface=csvDataDumpFailureReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        String generatedReportFileName = dataDumpGenerateReport(savedReportEntity1.getCreatedDate(),RecapConstants.BATCH_EXPORT, RecapCommonConstants.FAILURE, savedReportEntity1.getInstitutionName(), RecapCommonConstants.FILE_SYSTEM,savedReportEntity1.getFileName());
        assertNotNull(generatedReportFileName);
    }

    @Test
    public void generateDataDumpS3FailureReportTest() throws Exception {
        ReportEntity savedReportEntity1 = saveDataDumpFailureReport();
        List<ReportEntity> reportEntities=new ArrayList<>();
        reportEntities.add(savedReportEntity1);
        Mockito.when(reportDetailRepository.findByInstitutionAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(reportEntities);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface=s3DataDumpFailureReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        String generatedReportFileName = dataDumpGenerateReport(savedReportEntity1.getCreatedDate(),RecapConstants.BATCH_EXPORT, RecapCommonConstants.FAILURE, savedReportEntity1.getInstitutionName(), RecapCommonConstants.FTP,savedReportEntity1.getFileName());
        assertNotNull(generatedReportFileName);
    }


    @Test
    public void generateFailureReportForTwoEntity() throws Exception {
        ReportEntity savedReportEntity1 = saveFailureReportEntity();
        List<ReportEntity> reportEntities=new ArrayList<>();
        reportEntities.add(savedReportEntity1);
        Mockito.when(reportDetailRepository.findByFileAndInstitutionAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(reportEntities);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface=csvFailureReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        String generatedReportFileName = generateReport(savedReportEntity1.getCreatedDate(),RecapConstants.OPERATION_TYPE_ETL,savedReportEntity1.getType(), savedReportEntity1.getInstitutionName(), RecapCommonConstants.FILE_SYSTEM);
        assertNotNull(generatedReportFileName);
 }

    @Test
    public void uploadFailureReportTos3() throws Exception {
        ReportEntity savedReportEntity = saveFailureReportEntity();
        List<ReportEntity> reportEntities=new ArrayList<>();
        reportEntities.add(savedReportEntity);
        Mockito.when(reportDetailRepository.findByFileAndInstitutionAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(reportEntities);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface=s3FailureReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        String generatedReportFileName = generateReport(savedReportEntity.getCreatedDate(),RecapConstants.OPERATION_TYPE_ETL,savedReportEntity.getType(), savedReportEntity.getInstitutionName(), RecapCommonConstants.FTP);
        assertNotNull(generatedReportFileName);
    }

    @Test
    public void uploadSuccessReportTos3() throws Exception {
        ReportEntity savedReportEntity = saveSuccessReportEntity();
        List<ReportEntity> reportEntities=new ArrayList<>();
        reportEntities.add(savedReportEntity);
        Mockito.when(reportDetailRepository.findByFileAndInstitutionAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(reportEntities);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface=s3SuccessReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        String generatedReportFileName = generateReport(savedReportEntity.getCreatedDate(),RecapConstants.OPERATION_TYPE_ETL,savedReportEntity.getType(), savedReportEntity.getInstitutionName(), RecapCommonConstants.FTP);
        assertNotNull(generatedReportFileName);
    }

    @Test
    public void generateReportWithoutFileName() throws Exception {
        ReportEntity savedSuccessReportEntity1 = saveSuccessReportEntity();
        fileName = "";
        List<ReportEntity> reportEntities=new ArrayList<>();
        reportEntities.add(savedSuccessReportEntity1);
        Mockito.when(reportDetailRepository.findByInstitutionAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(reportEntities);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface=csvSuccessReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        String generatedReportFileName = generateReport(savedSuccessReportEntity1.getCreatedDate(),RecapConstants.OPERATION_TYPE_ETL,savedSuccessReportEntity1.getType(), savedSuccessReportEntity1.getInstitutionName(), RecapCommonConstants.FILE_SYSTEM);
        assertNotNull(generatedReportFileName);
    }

    @Test
    public void getReportGenerators() throws Exception {
        List<ReportGeneratorInterface> reportGenerators=reportGenerator.getReportGenerators();
        assertNotNull(reportGenerators);
    }

    private ReportEntity saveFailureReportEntity() {
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();

        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(fileName);
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

        reportEntity.setReportDataEntities(reportDataEntities);

        return reportEntity;
    }

    private ReportEntity saveSuccessReportEntity() {
        ReportEntity reportEntity = new ReportEntity();
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();

        ReportDataEntity totalRecordsInFileEntity = new ReportDataEntity();
        totalRecordsInFileEntity.setHeaderName(RecapConstants.TOTAL_RECORDS_IN_FILE);
        totalRecordsInFileEntity.setHeaderValue(String.valueOf(10000));
        reportDataEntities.add(totalRecordsInFileEntity);

        ReportDataEntity totalBibsLoadedEntity = new ReportDataEntity();
        totalBibsLoadedEntity.setHeaderName(RecapConstants.TOTAL_BIBS_LOADED);
        totalBibsLoadedEntity.setHeaderValue(String.valueOf(10000));
        reportDataEntities.add(totalBibsLoadedEntity);

        ReportDataEntity totalHoldingsLoadedEntity = new ReportDataEntity();
        totalHoldingsLoadedEntity.setHeaderName(RecapConstants.TOTAL_HOLDINGS_LOADED);
        totalHoldingsLoadedEntity.setHeaderValue(String.valueOf(8000));
        reportDataEntities.add(totalHoldingsLoadedEntity);

        ReportDataEntity totalItemsLoadedEntity = new ReportDataEntity();
        totalItemsLoadedEntity.setHeaderName(RecapConstants.TOTAL_ITEMS_LOADED);
        totalItemsLoadedEntity.setHeaderValue(String.valueOf(12000));
        reportDataEntities.add(totalItemsLoadedEntity);

        ReportDataEntity totalBibHoldingsLoadedEntity = new ReportDataEntity();
        totalBibHoldingsLoadedEntity.setHeaderName(RecapConstants.TOTAL_BIB_HOLDINGS_LOADED);
        totalBibHoldingsLoadedEntity.setHeaderValue(String.valueOf(18000));
        reportDataEntities.add(totalBibHoldingsLoadedEntity);

        ReportDataEntity totalBiBItemsLoadedEntity = new ReportDataEntity();
        totalBiBItemsLoadedEntity.setHeaderName(RecapConstants.TOTAL_BIB_ITEMS_LOADED);
        totalBiBItemsLoadedEntity.setHeaderValue(String.valueOf(22000));
        reportDataEntities.add(totalBiBItemsLoadedEntity);

        ReportDataEntity fileNameLoadedEntity = new ReportDataEntity();
        fileNameLoadedEntity.setHeaderName(RecapCommonConstants.FILE_NAME);
        fileNameLoadedEntity.setHeaderValue(fileName);
        reportDataEntities.add(fileNameLoadedEntity);

        reportEntity.setFileName(fileName);
        reportEntity.setCreatedDate(new Date());
        reportEntity.setType(RecapCommonConstants.SUCCESS);
        reportEntity.setReportDataEntities(reportDataEntities);
        reportEntity.setInstitutionName("PUL");
        return reportEntity;
    }


    private ReportEntity saveDataDumpSuccessReport(){
        ReportEntity reportEntity = new ReportEntity();
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();

        ReportDataEntity numberOfBibExportReportEntity = new ReportDataEntity();
        numberOfBibExportReportEntity.setHeaderName("NoOfBibsExported");
        numberOfBibExportReportEntity.setHeaderValue("1");
        reportDataEntities.add(numberOfBibExportReportEntity);

        ReportDataEntity requestingInstitutionReportDataEntity = new ReportDataEntity();
        requestingInstitutionReportDataEntity.setHeaderName("RequestingInstitution");
        requestingInstitutionReportDataEntity.setHeaderValue("CUL");
        reportDataEntities.add(requestingInstitutionReportDataEntity);

        ReportDataEntity institutionReportDataEntity = new ReportDataEntity();
        institutionReportDataEntity.setHeaderName("InstitutionCodes");
        institutionReportDataEntity.setHeaderValue("PUL");
        reportDataEntities.add(institutionReportDataEntity);

        ReportDataEntity fetchTypeReportDataEntity = new ReportDataEntity();
        fetchTypeReportDataEntity.setHeaderName("FetchType");
        fetchTypeReportDataEntity.setHeaderValue("1");
        reportDataEntities.add(fetchTypeReportDataEntity);

        ReportDataEntity exportDateReportDataEntity = new ReportDataEntity();
        exportDateReportDataEntity.setHeaderName("ExportFromDate");
        exportDateReportDataEntity.setHeaderValue(String.valueOf(new Date()));
        reportDataEntities.add(exportDateReportDataEntity);

        ReportDataEntity collectionGroupReportDataEntity = new ReportDataEntity();
        collectionGroupReportDataEntity.setHeaderName("CollectionGroupIds");
        collectionGroupReportDataEntity.setHeaderValue(String.valueOf(1));
        reportDataEntities.add(collectionGroupReportDataEntity);

        ReportDataEntity transmissionTypeReportDataEntity = new ReportDataEntity();
        transmissionTypeReportDataEntity.setHeaderName("TransmissionType");
        transmissionTypeReportDataEntity.setHeaderValue("0");
        reportDataEntities.add(transmissionTypeReportDataEntity);

        ReportDataEntity exportFormatReportDataEntity = new ReportDataEntity();
        exportFormatReportDataEntity.setHeaderName("ExportFormat");
        exportFormatReportDataEntity.setHeaderValue("1");
        reportDataEntities.add(exportFormatReportDataEntity);

        ReportDataEntity emailIdReportDataEntity = new ReportDataEntity();
        emailIdReportDataEntity.setHeaderName("ToEmailId");
        emailIdReportDataEntity.setHeaderValue("0");
        reportDataEntities.add(emailIdReportDataEntity);

        reportEntity.setFileName("2017-02-01 13:41");
        reportEntity.setCreatedDate(new Date());
        reportEntity.setType("BatchExportSuccess");
        reportEntity.setReportDataEntities(reportDataEntities);
        reportEntity.setInstitutionName("PUL");

        return reportEntity;
    }

    private ReportEntity saveDataDumpFailureReport(){
        ReportEntity reportEntity = new ReportEntity();
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();

        ReportDataEntity requestingInstitutionReportDataEntity = new ReportDataEntity();
        requestingInstitutionReportDataEntity.setHeaderName("RequestingInstitution");
        requestingInstitutionReportDataEntity.setHeaderValue("CUL");
        reportDataEntities.add(requestingInstitutionReportDataEntity);

        ReportDataEntity institutionReportDataEntity = new ReportDataEntity();
        institutionReportDataEntity.setHeaderName("InstitutionCodes");
        institutionReportDataEntity.setHeaderValue("PUL");
        reportDataEntities.add(institutionReportDataEntity);

        ReportDataEntity fetchTypeReportDataEntity = new ReportDataEntity();
        fetchTypeReportDataEntity.setHeaderName("FetchType");
        fetchTypeReportDataEntity.setHeaderValue("1");
        reportDataEntities.add(fetchTypeReportDataEntity);

        ReportDataEntity exportDateReportDataEntity = new ReportDataEntity();
        exportDateReportDataEntity.setHeaderName("ExportFromDate");
        exportDateReportDataEntity.setHeaderValue(String.valueOf(new Date()));
        reportDataEntities.add(exportDateReportDataEntity);

        ReportDataEntity collectionGroupReportDataEntity = new ReportDataEntity();
        collectionGroupReportDataEntity.setHeaderName("CollectionGroupIds");
        collectionGroupReportDataEntity.setHeaderValue(String.valueOf(1));
        reportDataEntities.add(collectionGroupReportDataEntity);

        ReportDataEntity transmissionTypeReportDataEntity = new ReportDataEntity();
        transmissionTypeReportDataEntity.setHeaderName("TransmissionType");
        transmissionTypeReportDataEntity.setHeaderValue("0");
        reportDataEntities.add(transmissionTypeReportDataEntity);

        ReportDataEntity exportFormatReportDataEntity = new ReportDataEntity();
        exportFormatReportDataEntity.setHeaderName("ExportFormat");
        exportFormatReportDataEntity.setHeaderValue("1");
        reportDataEntities.add(exportFormatReportDataEntity);

        ReportDataEntity emailIdReportDataEntity = new ReportDataEntity();
        emailIdReportDataEntity.setHeaderName("ToEmailId");
        emailIdReportDataEntity.setHeaderValue("0");
        reportDataEntities.add(emailIdReportDataEntity);

        ReportDataEntity failedBibsEntity = new ReportDataEntity();
        failedBibsEntity.setHeaderName("FailedBibs");
        failedBibsEntity.setHeaderValue("1");
        reportDataEntities.add(failedBibsEntity);

        ReportDataEntity failureCauseEntity = new ReportDataEntity();
        failureCauseEntity.setHeaderName("FailureCause");
        failureCauseEntity.setHeaderValue("parsing exception");
        reportDataEntities.add(failureCauseEntity);

        ReportDataEntity fileNameLoadedEntity = new ReportDataEntity();
        fileNameLoadedEntity.setHeaderName(RecapCommonConstants.FILE_NAME);
        fileNameLoadedEntity.setHeaderValue(fileName);
        reportDataEntities.add(fileNameLoadedEntity);

        reportEntity.setFileName("2017-02-01 13:42");
        reportEntity.setCreatedDate(new Date());
        reportEntity.setType("BatchExportFailure");
        reportEntity.setReportDataEntities(reportDataEntities);
        reportEntity.setInstitutionName("PUL");
        return reportEntity;
    }

    public Date getFromDate(Date date){
        Calendar cal = Calendar.getInstance();
        Date from = date;
        cal.setTime(from);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        from = cal.getTime();
        return from;
    }

    public Date getToDate(Date date){
        Calendar cal = Calendar.getInstance();
        Date to = date;
        cal.setTime(to);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        to = cal.getTime();
        return to;
    }

    private String generateReport(Date createdDate, String operationType, String reportType, String institutionName, String transmissionType) throws InterruptedException {
        Calendar cal = Calendar.getInstance();
        Date from = createdDate;
        cal.setTime(from);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        from = cal.getTime();
        Date to = createdDate;
        cal.setTime(to);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        to = cal.getTime();

        String generatedFileName = reportGenerator.generateReport(fileName, operationType,reportType, institutionName, from, to, transmissionType);

        Thread.sleep(1000);

        return generatedFileName;
    }

    private String dataDumpGenerateReport(Date createdDate, String operationType, String reportType, String institutionName, String transmissionType,String dataDumpFileName) throws InterruptedException {
        Calendar cal = Calendar.getInstance();
        Date from = createdDate;
        cal.setTime(from);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        from = cal.getTime();
        Date to = createdDate;
        cal.setTime(to);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        to = cal.getTime();

        String generatedFileName = reportGenerator.generateReport(dataDumpFileName, operationType,reportType, institutionName, from, to, transmissionType);

        Thread.sleep(1000);

        return generatedFileName;
    }

    class CustomArgumentMatcher implements ArgumentMatcher {
        @Override
        public boolean matches(Object argument) {
            return false;
        }
    }

}
