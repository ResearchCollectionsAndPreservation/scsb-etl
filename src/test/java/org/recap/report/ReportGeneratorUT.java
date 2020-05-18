package org.recap.report;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.ReportDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by peris on 8/17/16.
 */
public class ReportGeneratorUT extends BaseTestCase {

    @Autowired
    ReportDetailRepository reportDetailRepository;

    private String fileName = "test.xml";

    @Value("${etl.report.directory}")
    private String reportDirectory;

    @Value("${etl.dump.report.directory}")
    private String dataDumpReportDirectory;

    @Autowired
    ReportGenerator reportGenerator;

    @Mock
    ReportDetailRepository mockReportDetailsRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void generateFailureReportTest() throws Exception {

        ReportEntity savedReportEntity1 = saveFailureReportEntity();

        String generatedReportFileName = generateReport(savedReportEntity1.getCreatedDate(), "ETL",savedReportEntity1.getType(), savedReportEntity1.getInstitutionName(), RecapConstants.FILE_SYSTEM);

        assertNotNull(generatedReportFileName);

        File directory = new File(reportDirectory);
        assertTrue(directory.isDirectory());

        boolean directoryContains = new File(directory, generatedReportFileName).exists();
        assertTrue(directoryContains);
    }

    @Test
    public void generateSuccessReportTest() throws Exception {

        ReportEntity savedReportEntity1 = saveSuccessReportEntity();

        String generatedReportFileName = generateReport(savedReportEntity1.getCreatedDate(), "ETL",savedReportEntity1.getType(), savedReportEntity1.getInstitutionName(), RecapConstants.FILE_SYSTEM);

        assertNotNull(generatedReportFileName);

        File directory = new File(reportDirectory);
        assertTrue(directory.isDirectory());

        boolean directoryContains = new File(directory, generatedReportFileName).exists();
        assertTrue(directoryContains);
    }

    @Test
    public void generateDataDumpFileSystemSuccessReportTest() throws Exception {

        ReportEntity savedReportEntity1 = saveDataDumpSuccessReport();
        String generatedReportFileName = dataDumpGenerateReport(savedReportEntity1.getCreatedDate(), "BatchExport", RecapConstants.SUCCESS, savedReportEntity1.getInstitutionName(), RecapConstants.FILE_SYSTEM,savedReportEntity1.getFileName());

        assertNotNull(generatedReportFileName);

        File directory = new File(dataDumpReportDirectory);
        assertTrue(directory.isDirectory());

        boolean directoryContains = new File(directory, generatedReportFileName).exists();
        assertTrue(directoryContains);
    }

    @Test
    public void generateDataDumpFtpSuccessReportTest() throws Exception {

        ReportEntity savedReportEntity1 = saveDataDumpSuccessReport();

        String generatedReportFileName = dataDumpGenerateReport(savedReportEntity1.getCreatedDate(), "BatchExport", RecapConstants.SUCCESS, savedReportEntity1.getInstitutionName(), RecapConstants.FTP,savedReportEntity1.getFileName());

        assertNotNull(generatedReportFileName);
    }

    @Test
    public void generateDataDumpFileSystemFailureReportTest() throws Exception {

        ReportEntity savedReportEntity1 = saveDataDumpFailureReport();

        String generatedReportFileName = dataDumpGenerateReport(savedReportEntity1.getCreatedDate(), "BatchExport", RecapConstants.FAILURE, savedReportEntity1.getInstitutionName(), RecapConstants.FILE_SYSTEM,savedReportEntity1.getFileName());

        assertNotNull(generatedReportFileName);

        File directory = new File(dataDumpReportDirectory);
        assertTrue(directory.isDirectory());

        boolean directoryContains = new File(directory, generatedReportFileName).exists();
        assertTrue(directoryContains);
    }

    @Test
    public void generateDataDumpFtpFailureReportTest() throws Exception {

        ReportEntity savedReportEntity1 = saveDataDumpFailureReport();

        String generatedReportFileName = dataDumpGenerateReport(savedReportEntity1.getCreatedDate(), "BatchExport", RecapConstants.FAILURE, savedReportEntity1.getInstitutionName(), RecapConstants.FTP,savedReportEntity1.getFileName());

        assertNotNull(generatedReportFileName);
    }


    @Test
    public void generateFailureReportForTwoEntity() throws Exception {
        ReportEntity savedReportEntity1 = saveFailureReportEntity();
        ReportEntity savedReportEntity2 = saveFailureReportEntity();

        String generatedReportFileName = generateReport(savedReportEntity1.getCreatedDate(), "ETL",savedReportEntity1.getType(), savedReportEntity1.getInstitutionName(), RecapConstants.FILE_SYSTEM);

        assertNotNull(generatedReportFileName);

        File directory = new File(reportDirectory);
        assertTrue(directory.isDirectory());

        boolean directoryContains = new File(directory, generatedReportFileName).exists();
        assertTrue(directoryContains);

    }

    @Test
    public void uploadFailureReportToFTP() throws Exception {
        ReportEntity savedReportEntity = saveFailureReportEntity();

        String generatedReportFileName = generateReport(savedReportEntity.getCreatedDate(), "ETL",savedReportEntity.getType(), savedReportEntity.getInstitutionName(), RecapConstants.FTP);

        assertNotNull(generatedReportFileName);
    }

    @Test
    public void uploadSuccessReportToFTP() throws Exception {
        ReportEntity savedReportEntity = saveSuccessReportEntity();

        String generatedReportFileName = generateReport(savedReportEntity.getCreatedDate(), "ETL",savedReportEntity.getType(), savedReportEntity.getInstitutionName(), RecapConstants.FTP);

        assertNotNull(generatedReportFileName);
    }

    @Test
    public void generateReportWithoutFileName() throws Exception {
        ReportEntity savedSuccessReportEntity1 = saveSuccessReportEntity();
        ReportEntity savedSuccessReportEntity2 = saveSuccessReportEntity();
        fileName = "";
        String generatedReportFileName = generateReport(savedSuccessReportEntity1.getCreatedDate(), "ETL",savedSuccessReportEntity1.getType(), savedSuccessReportEntity1.getInstitutionName(), RecapConstants.FILE_SYSTEM);

        assertNotNull(generatedReportFileName);

        File directory = new File(reportDirectory);
        assertTrue(directory.isDirectory());

        boolean directoryContains = new File(directory, generatedReportFileName).exists();
        assertTrue(directoryContains);
    }

    private ReportEntity saveFailureReportEntity() {
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();

        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(fileName);
        reportEntity.setCreatedDate(new Date());
        reportEntity.setType(RecapConstants.FAILURE);
        reportEntity.setInstitutionName("PUL");

        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setHeaderName(RecapConstants.ITEM_BARCODE);
        reportDataEntity.setHeaderValue("103");
        reportDataEntities.add(reportDataEntity);

        ReportDataEntity reportDataEntity2 = new ReportDataEntity();
        reportDataEntity2.setHeaderName(RecapConstants.CUSTOMER_CODE);
        reportDataEntity2.setHeaderValue("PA");
        reportDataEntities.add(reportDataEntity2);

        ReportDataEntity reportDataEntity3 = new ReportDataEntity();
        reportDataEntity3.setHeaderName(RecapConstants.LOCAL_ITEM_ID);
        reportDataEntity3.setHeaderValue("10412");
        reportDataEntities.add(reportDataEntity3);

        ReportDataEntity reportDataEntity4 = new ReportDataEntity();
        reportDataEntity4.setHeaderName(RecapConstants.OWNING_INSTITUTION);
        reportDataEntity4.setHeaderValue("PUL");
        reportDataEntities.add(reportDataEntity4);

        reportEntity.setReportDataEntities(reportDataEntities);

        return reportDetailRepository.save(reportEntity);
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
        fileNameLoadedEntity.setHeaderName(RecapConstants.FILE_NAME);
        fileNameLoadedEntity.setHeaderValue(fileName);
        reportDataEntities.add(fileNameLoadedEntity);

        reportEntity.setFileName(fileName);
        reportEntity.setCreatedDate(new Date());
        reportEntity.setType(RecapConstants.SUCCESS);
        reportEntity.setReportDataEntities(reportDataEntities);
        reportEntity.setInstitutionName("PUL");

        ReportEntity savedReportEntity = reportDetailRepository.save(reportEntity);
        return savedReportEntity;
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

        ReportEntity savedReportEntity = reportDetailRepository.save(reportEntity);
        return savedReportEntity;
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
        fileNameLoadedEntity.setHeaderName(RecapConstants.FILE_NAME);
        fileNameLoadedEntity.setHeaderValue(fileName);
        reportDataEntities.add(fileNameLoadedEntity);

        reportEntity.setFileName("2017-02-01 13:42");
        reportEntity.setCreatedDate(new Date());
        reportEntity.setType("BatchExportFailure");
        reportEntity.setReportDataEntities(reportDataEntities);
        reportEntity.setInstitutionName("PUL");

        ReportEntity savedReportEntity = reportDetailRepository.save(reportEntity);
        return savedReportEntity;
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