package org.recap.camel.datadump;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbConstants;
import org.recap.model.csv.DataDumpFailureReport;
import org.recap.model.csv.DataDumpSuccessReport;
import org.recap.model.export.DataDumpRequest;
import org.recap.model.jparw.ETLRequestLogEntity;
import org.recap.model.jparw.ExportStatusEntity;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;
import org.recap.report.S3DataDumpFailureReportGenerator;
import org.recap.report.S3DataDumpSuccessReportGenerator;
import org.recap.repositoryrw.ETLRequestLogDetailsRepository;
import org.recap.repositoryrw.ExportStatusDetailsRepository;
import org.recap.repositoryrw.ReportDetailRepository;
import org.recap.service.email.datadump.DataDumpEmailService;
import org.recap.service.preprocessor.DataDumpExportService;
import org.recap.util.datadump.DataDumpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

/**
 * Created by hemalathas on 19/4/17.
 */
public class DataExportEmailProcessorUT extends BaseTestCaseUT {

    @InjectMocks
    DataExportEmailProcessor dataExportEmailProcessor;

    @Mock
    ETLRequestLogDetailsRepository etlRequestLogDetailsRepository;

    @Mock
    ExportStatusDetailsRepository exportStatusDetailsRepository;

    @Mock
    DataDumpUtil dataDumpUtil;

    @Mock
    DataDumpExportService dataDumpExportService;

    @Mock
    ReportDetailRepository reportDetailRepository;

    @Mock
    DataDumpEmailService dataDumpEmailService;

    @Mock
    S3DataDumpFailureReportGenerator s3DataDumpFailureReportGenerator;

    @Mock
    S3DataDumpSuccessReportGenerator s3DataDumpSuccessReportGenerator;

    @Mock
    ProducerTemplate producerTemplate;

    @Value("${etl.data.dump.fetchtype.full}")
    private String fetchTypeFull;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(dataExportEmailProcessor, "dataDumpStatusFileName", "src/test/resources/org/recap/service/formatter/datadump/princeton.xml");
    }

    @Test
    public void testDataExportEmailProcess() throws Exception {
        dataExportEmailProcessor.setTransmissionType("2");
        dataExportEmailProcessor.setInstitutionCodes(Arrays.asList("PUL", "CUL"));
        dataExportEmailProcessor.setRequestingInstitutionCode("NYPL");
        dataExportEmailProcessor.setFolderName("test");
        dataExportEmailProcessor.setToEmailId("test@email.com");
        dataExportEmailProcessor.setRequestId("1");
        dataExportEmailProcessor.setFetchType("1");
        dataExportEmailProcessor.setImsDepositoryCodes(Arrays.asList("HD", "CU"));
        dataExportEmailProcessor.setEltRequestId(1);
        dataExportEmailProcessor.setRequestFromSwagger(Boolean.TRUE);
        assertNotNull(dataExportEmailProcessor.getTransmissionType());
        assertNotNull(dataExportEmailProcessor.getInstitutionCodes());
        assertNotNull(dataExportEmailProcessor.getRequestingInstitutionCode());
        assertNotNull(dataExportEmailProcessor.getFolderName());
        assertNotNull(dataExportEmailProcessor.getToEmailId());
        assertNotNull(dataExportEmailProcessor.getRequestId());
        assertNotNull(dataExportEmailProcessor.getFetchType());
        assertNotNull(dataExportEmailProcessor.getImsDepositoryCodes());
        assertNotNull(dataExportEmailProcessor.getEltRequestId());
        assertNotNull(dataExportEmailProcessor.getDataDumpEmailService());
        assertTrue(dataExportEmailProcessor.getRequestFromSwagger());
    }

    private ReportEntity saveReportEntity(String type) {
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName("test");
        reportEntity.setCreatedDate(new Date());
        reportEntity.setType(type);
        reportEntity.setInstitutionName("CUL");

        ReportDataEntity reportDataEntity1 = new ReportDataEntity();
        reportDataEntity1.setHeaderName(ScsbConstants.HEADER_FETCH_TYPE);
        reportDataEntity1.setHeaderValue(ScsbConstants.DATADUMP_FETCHTYPE_INCREMENTAL);

        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        reportDataEntities.add(getReportDataEntity(ScsbConstants.NUM_BIBS_EXPORTED, ScsbConstants.DATADUMP_FETCHTYPE_INCREMENTAL));
        reportDataEntities.add(getReportDataEntity(ScsbConstants.FAILED_BIBS, ScsbConstants.DATADUMP_FETCHTYPE_INCREMENTAL));
        reportDataEntities.add(getReportDataEntity(ScsbConstants.EXPORTED_ITEM_COUNT, ScsbConstants.DATADUMP_FETCHTYPE_INCREMENTAL));
        reportDataEntities.add(getReportDataEntity(ScsbConstants.HEADER_FETCH_TYPE, ScsbConstants.DATADUMP_FETCHTYPE_INCREMENTAL));
        reportDataEntities.add(getReportDataEntity(ScsbConstants.HEADER_FETCH_TYPE, ScsbConstants.DATADUMP_FETCHTYPE_DELETED));
        reportDataEntities.add(getReportDataEntity(ScsbConstants.HEADER_FETCH_TYPE, "3"));


        reportEntity.setReportDataEntities(reportDataEntities);

        return reportEntity;
    }

    private ReportDataEntity getReportDataEntity(String headerName, String headerValue) {
        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setId(1);
        reportDataEntity.setHeaderName(headerName);
        reportDataEntity.setHeaderValue(headerValue);
        reportDataEntity.setRecordNum("223");
        assertNotNull(reportDataEntity.getRecordNum());
        return reportDataEntity;
    }

    @Test
    public void testProcess() {
        String[] fetchTypes = {"1", "2", "10"};
        for (String fetchType :
                fetchTypes) {
            ReportDataEntity reportDataEntity = new ReportDataEntity();
            reportDataEntity.setHeaderName("FetchType");
            reportDataEntity.setHeaderValue("3");
            List<ReportEntity> reportEntities = new ArrayList<>();
            ReportEntity reportEntity = new ReportEntity();
            reportEntity.setCreatedDate(new Date());
            reportEntity.setInstitutionName("CUL");
            reportEntity.setType("BatchExport");
            reportEntity.setFileName("requestId");
            reportEntity.setReportDataEntities(Arrays.asList(reportDataEntity));

            reportEntities.add(reportEntity);
            dataExportEmailProcessor.setRequestId("sampleRecordForEtlLoadTest.xml");
            String dataHeader = ";currentPageCount#1";
            CamelContext ctx = new DefaultCamelContext();
            Exchange ex = new DefaultExchange(ctx);
            Message in = ex.getIn();
            ex.setMessage(in);
            ex.setIn(in);
            Map<String, Object> mapdata = new HashMap<>();
            mapdata.put("batchHeaders", dataHeader);
            ReflectionTestUtils.setField(dataExportEmailProcessor, "toEmailId", "temp");
            ReflectionTestUtils.setField(dataExportEmailProcessor, "folderName", "te/mp/er/s");
            ReflectionTestUtils.setField(dataExportEmailProcessor, "institutionCodes", Arrays.asList("CUL"));
            ReflectionTestUtils.setField(dataExportEmailProcessor, "transmissionType", "0");
            ReflectionTestUtils.setField(dataExportEmailProcessor, "dataDumpEmailService", dataDumpEmailService);
            ReflectionTestUtils.setField(dataExportEmailProcessor, "fetchType", fetchType);
            ReflectionTestUtils.setField(dataExportEmailProcessor, "fetchTypeFull", fetchTypeFull);
            ReflectionTestUtils.setField(dataExportEmailProcessor, "reportFileName", "reportFileName");
            reportDataEntity.setHeaderValue("2");
            reportDataEntity.setHeaderValue("1");
            ReflectionTestUtils.invokeMethod(dataExportEmailProcessor, "processEmail", "2", "2", "2", "1", "CUL");
            DataDumpFailureReport dataDumpFailureReport = new DataDumpFailureReport();
            DataDumpSuccessReport dataDumpSuccessReport = new DataDumpSuccessReport();
            List<ReportEntity> byFileName = new ArrayList<>();
            byFileName.add(saveReportEntity(ScsbConstants.BATCH_EXPORT_SUCCESS));
            byFileName.add(saveReportEntity(ScsbConstants.BATCH_EXPORT_FAILURE));
            Mockito.when(reportDetailRepository.findByFileName(Mockito.anyString())).thenReturn(byFileName);
            Mockito.when(s3DataDumpFailureReportGenerator.getDataDumpFailureReport(Mockito.anyList(), Mockito.anyString())).thenReturn(dataDumpFailureReport);
            Mockito.when(s3DataDumpSuccessReportGenerator.getDataDumpSuccessReport(Mockito.anyList(), Mockito.anyString())).thenReturn(dataDumpSuccessReport);
            try {
                dataExportEmailProcessor.process(ex);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Test
    public void updateDataDumpStatus() {
        ReflectionTestUtils.setField(dataExportEmailProcessor, "isRequestFromSwagger", Boolean.TRUE);
        ReflectionTestUtils.invokeMethod(dataExportEmailProcessor, "updateDataDumpStatus");
    }

    @Test
    public void updateStatusInFile() {
        ReflectionTestUtils.invokeMethod(dataExportEmailProcessor, "updateStatusInFile");
    }

    @Test
    public void updateStatusInDB() {
        ETLRequestLogEntity etlRequestLogEntity = getEtlRequestLogEntity();
        ExportStatusEntity exportStatusEntity = getExportStatusEntity();
        DataDumpRequest dataDumpRequest = getDataDumpRequest();
        Mockito.when(etlRequestLogDetailsRepository.findById(any())).thenReturn(Optional.of(etlRequestLogEntity));
        Mockito.when(exportStatusDetailsRepository.findByExportStatusCode(ScsbConstants.COMPLETED)).thenReturn(exportStatusEntity);
        Mockito.when(etlRequestLogDetailsRepository.saveAndFlush(any())).thenReturn(etlRequestLogEntity);
        Mockito.when(dataDumpUtil.checkAndPrepareAwaitingReqIfAny()).thenReturn(dataDumpRequest);
        Mockito.when(dataDumpExportService.startDataDumpProcess(any())).thenReturn("");
        ReflectionTestUtils.invokeMethod(dataExportEmailProcessor, "updateStatusInDB");
    }

    private DataDumpRequest getDataDumpRequest() {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setFetchType("0");
        dataDumpRequest.setRequestingInstitutionCode("NYPL");
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        dataDumpRequest.setCollectionGroupIds(cgIds);
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("CUL");
        institutionCodes.add("NYPL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setTransmissionType("2");
        dataDumpRequest.setOutputFileFormat(ScsbConstants.XML_FILE_FORMAT);
        dataDumpRequest.setDateTimeString(getDateTimeString());
        dataDumpRequest.setEtlRequestId(1);
        return dataDumpRequest;
    }

    private String getDateTimeString() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(ScsbConstants.DATE_FORMAT_DDMMMYYYYHHMM);
        return sdf.format(date);
    }

    private ExportStatusEntity getExportStatusEntity() {
        ExportStatusEntity exportStatusEntity = new ExportStatusEntity();
        exportStatusEntity.setId(1);
        exportStatusEntity.setExportStatusCode("Complete");
        exportStatusEntity.setExportStatusDesc("Complete");
        return exportStatusEntity;
    }

    private ETLRequestLogEntity getEtlRequestLogEntity() {
        ETLRequestLogEntity etlRequestLogEntity = new ETLRequestLogEntity();
        etlRequestLogEntity.setId(1);
        etlRequestLogEntity.setExportStatusEntity(new ExportStatusEntity());
        etlRequestLogEntity.setCompleteTime(new Date());
        etlRequestLogEntity.setCollectionGroupIds("001,002,003");
        etlRequestLogEntity.setEmailIds("emailids");
        etlRequestLogEntity.setRequestedTime(new Date());
        etlRequestLogEntity.setFetchType("Pull");
        etlRequestLogEntity.setExportStatusId(1);
        etlRequestLogEntity.setImsRepositoryCodes("IMRC");
        etlRequestLogEntity.setMessage("msg");
        etlRequestLogEntity.setInstCodeToExport("ECExport");
        etlRequestLogEntity.setOutputFormat("Format");
        etlRequestLogEntity.setRequestingInstCode("22");
        etlRequestLogEntity.setUserName("test");
        etlRequestLogEntity.setTransmissionType("transmission");
        etlRequestLogEntity.setProvidedDate(new Date());
        return etlRequestLogEntity;
    }
}