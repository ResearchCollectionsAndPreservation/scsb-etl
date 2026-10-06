package org.recap.report;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbConstants;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;
import org.recap.repositoryrw.ReportDetailRepository;
import org.recap.util.datadump.DataExportHeaderUtil;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CommonReportGeneratorUT extends BaseTestCaseUT {

    @Mock
    ReportDetailRepository mockReportDetailRepository;

    @Mock
    DataExportHeaderUtil dataExportHeaderUtil;

    @InjectMocks
    CommonReportGenerator commonReportGenerator;

    @Mock
    ProducerTemplate producerTemplate;

    @BeforeEach
    public void init() {
        commonReportGenerator = new CommonReportGenerator();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcess() {
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        ex.setMessage(in);
        ex.setProperty(ScsbConstants.INST_NAME, "CUL");
        GenericFile<Object> camelFileExchangeFile=new GenericFile<>();
        camelFileExchangeFile.setFileName("file");
        ex.setProperty(ScsbConstants.CAMEL_EXCHANGE_FILE, camelFileExchangeFile);
        Map<String, Object> mapdata = new HashMap<>();
        mapdata.put("institutionName", "CUL");
        in.setHeaders(mapdata);
        ex.setIn(in);
        commonReportGenerator.process(ex, "institutionName", mockReportDetailRepository);
        try{commonReportGenerator.processRecordFailures(ex, Arrays.asList("Test"), "batchHeaders", "requestId", dataExportHeaderUtil);}catch (Exception e){e.printStackTrace();}
    }

    @Test
    public void generateFailureReport(){
        List<ReportEntity> reportEntities = new ArrayList<>();
        reportEntities.add(getReportEntity());
        String fileName = "Test";
        String reportQueue = "1";
        String generateSuccessReport = commonReportGenerator.generateFailureReport(reportEntities,fileName,reportQueue);
        assertNotNull(generateSuccessReport);
    }

    @Test
    public void generateFailureReportNull(){
        List<ReportEntity> reportEntities = new ArrayList<>();
        String fileName = "Test";
        String reportQueue = "1";
        String generateSuccessReport = commonReportGenerator.generateFailureReport(reportEntities,fileName,reportQueue);
        assertNull(generateSuccessReport);
    }

    @Test
    public void generateSuccessReport(){
        List<ReportEntity> reportEntities = new ArrayList<>();
        reportEntities.add(getReportEntity());
        String fileName = "Test";
        String reportQueue = "1";
        String generateSuccessReport = commonReportGenerator.generateSuccessReport(reportEntities,fileName,reportQueue);
        assertNotNull(generateSuccessReport);
    }
    @Test
    public void generateSuccessReportNull(){
        List<ReportEntity> reportEntities = new ArrayList<>();
        String fileName = "Test";
        String reportQueue = "1";
        String generateSuccessReport = commonReportGenerator.generateSuccessReport(reportEntities,fileName,reportQueue);
        assertNull(generateSuccessReport);
    }

    @Test
    public void generateDataDumpFailureReport(){
        List<ReportEntity> reportEntities = new ArrayList<>();
        reportEntities.add(getReportEntity());
        String fileName = "Test";
        commonReportGenerator.generateDataDumpFailureReport(reportEntities,fileName);
    }

    @Test
    public void processSuccessReport(){
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        ex.setMessage(in);
        try {
            commonReportGenerator.processSuccessReport(ex, 1, "headers", "1", dataExportHeaderUtil);
        }catch (Exception e){}
    }

    @Test
    public void processReport(){
        HashMap<String, String> stringHashMap = commonReportGenerator.processReport("headers", "1", dataExportHeaderUtil);
        assertNotNull(stringHashMap);
    }

    private ReportEntity getReportEntity() {
        ReportEntity reportEntity = new ReportEntity();
        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setHeaderName(ScsbConstants.HEADER_FETCH_TYPE);
        reportDataEntity.setHeaderValue(ScsbConstants.DATADUMP_FETCHTYPE_INCREMENTAL);
        reportEntity.setReportDataEntities(Arrays.asList(reportDataEntity));
        reportEntity.setCreatedDate(new Date());
        return reportEntity;
    }
}
