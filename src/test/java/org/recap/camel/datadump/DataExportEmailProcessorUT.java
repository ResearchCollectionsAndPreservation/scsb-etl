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
import org.recap.BaseTestCase;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.csv.DataDumpFailureReport;
import org.recap.model.csv.DataDumpSuccessReport;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;
import org.recap.report.S3DataDumpFailureReportGenerator;
import org.recap.report.S3DataDumpSuccessReportGenerator;
import org.recap.repositoryrw.ReportDetailRepository;
import org.recap.service.email.datadump.DataDumpEmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 19/4/17.
 */
public class DataExportEmailProcessorUT extends BaseTestCaseUT {

    @InjectMocks
    DataExportEmailProcessor dataExportEmailProcessor;

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
        ReflectionTestUtils.setField(dataExportEmailProcessor,"dataDumpStatusFileName","${user.home}/data-dump/dataExportStatus.txt");
    }

    @Test
    public void testDataExportEmailProcess() throws Exception {
        dataExportEmailProcessor.setTransmissionType("2");
        dataExportEmailProcessor.setInstitutionCodes(Arrays.asList("PUL", "CUL"));
        dataExportEmailProcessor.setRequestingInstitutionCode("NYPL");
        dataExportEmailProcessor.setFolderName("test");
        dataExportEmailProcessor.setToEmailId("hemalatha.s@htcindia.com");
        dataExportEmailProcessor.setRequestId("1");
        dataExportEmailProcessor.setFetchType("1");
        assertNotNull(dataExportEmailProcessor.getTransmissionType());
        assertNotNull(dataExportEmailProcessor.getInstitutionCodes());
        assertNotNull(dataExportEmailProcessor.getRequestingInstitutionCode());
        assertNotNull(dataExportEmailProcessor.getFolderName());
        assertNotNull(dataExportEmailProcessor.getToEmailId());
        assertNotNull(dataExportEmailProcessor.getRequestId());
        assertNotNull(dataExportEmailProcessor.getFetchType());
    }

    private ReportEntity saveReportEntity(String type) {
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName("test");
        reportEntity.setCreatedDate(new Date());
        reportEntity.setType(type);
        reportEntity.setInstitutionName("CUL");

        ReportDataEntity reportDataEntity1 = new ReportDataEntity();
        reportDataEntity1.setHeaderName(RecapConstants.HEADER_FETCH_TYPE);
        reportDataEntity1.setHeaderValue(RecapConstants.DATADUMP_FETCHTYPE_INCREMENTAL);

        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        reportDataEntities.add(getReportDataEntity(RecapConstants.NUM_BIBS_EXPORTED,RecapConstants.DATADUMP_FETCHTYPE_INCREMENTAL));
        reportDataEntities.add(getReportDataEntity(RecapConstants.FAILED_BIBS,RecapConstants.DATADUMP_FETCHTYPE_INCREMENTAL));
        reportDataEntities.add(getReportDataEntity(RecapConstants.EXPORTED_ITEM_COUNT,RecapConstants.DATADUMP_FETCHTYPE_INCREMENTAL));
        reportDataEntities.add(getReportDataEntity(RecapConstants.HEADER_FETCH_TYPE,RecapConstants.DATADUMP_FETCHTYPE_INCREMENTAL));
        reportDataEntities.add(getReportDataEntity(RecapConstants.HEADER_FETCH_TYPE,RecapConstants.DATADUMP_FETCHTYPE_DELETED));
        reportDataEntities.add(getReportDataEntity(RecapConstants.HEADER_FETCH_TYPE,"3"));


        reportEntity.setReportDataEntities(reportDataEntities);

        return reportEntity;
    }

    private ReportDataEntity getReportDataEntity(String headerName, String headerValue) {
        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setHeaderName(headerName);
        reportDataEntity.setHeaderValue(headerValue);
        return reportDataEntity;
    }

    @Test
    public void testProcess() {
        String[] fetchTypes={"1","2","10"};
        for (String fetchType:
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
        ReflectionTestUtils.setField(dataExportEmailProcessor,"toEmailId","temp");
        ReflectionTestUtils.setField(dataExportEmailProcessor,"folderName","te/mp/er/s");
        ReflectionTestUtils.setField(dataExportEmailProcessor,"institutionCodes",Arrays.asList("CUL"));
        ReflectionTestUtils.setField(dataExportEmailProcessor,"transmissionType","0");
        ReflectionTestUtils.setField(dataExportEmailProcessor,"dataDumpEmailService",dataDumpEmailService);
        ReflectionTestUtils.setField(dataExportEmailProcessor,"fetchType",fetchType);
        ReflectionTestUtils.setField(dataExportEmailProcessor,"fetchTypeFull",fetchTypeFull);
        ReflectionTestUtils.setField(dataExportEmailProcessor,"reportFileName","reportFileName");
        reportDataEntity.setHeaderValue("2");
        reportDataEntity.setHeaderValue("1");
        ReflectionTestUtils.invokeMethod(dataExportEmailProcessor,"processEmail","2","2","2","1","CUL");
        DataDumpFailureReport dataDumpFailureReport=new DataDumpFailureReport();
        DataDumpSuccessReport dataDumpSuccessReport=new DataDumpSuccessReport();
        List<ReportEntity> byFileName=new ArrayList<>();
        byFileName.add(saveReportEntity(RecapConstants.BATCH_EXPORT_SUCCESS));
        byFileName.add(saveReportEntity(RecapConstants.BATCH_EXPORT_FAILURE));
        Mockito.when(reportDetailRepository.findByFileName(Mockito.anyString())).thenReturn(byFileName);
        Mockito.when(s3DataDumpFailureReportGenerator.getDataDumpFailureReport(Mockito.anyList(),Mockito.anyString())).thenReturn(dataDumpFailureReport);
        Mockito.when(s3DataDumpSuccessReportGenerator.getDataDumpSuccessReport(Mockito.anyList(),Mockito.anyString())).thenReturn(dataDumpSuccessReport);
        try {
            dataExportEmailProcessor.process(ex);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    }
}