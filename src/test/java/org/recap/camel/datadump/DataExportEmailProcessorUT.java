package org.recap.camel.datadump;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.RecapCommonConstants;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.ReportDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;

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
public class DataExportEmailProcessorUT extends BaseTestCase {

    @Autowired
    DataExportEmailProcessor dataExportEmailProcessor;
    @Mock
    ReportDetailRepository reportDetailRepository;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDataExportEmailProcess() throws Exception {
        ReportEntity reportEntity = saveReportEntity();
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

    private ReportEntity saveReportEntity() {
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();

        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName("test");
        reportEntity.setCreatedDate(new Date());
        reportEntity.setType(RecapCommonConstants.FAILURE);
        reportEntity.setInstitutionName("CUL");

        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setHeaderName("Barcode");
        reportDataEntity.setHeaderValue("103");
        reportDataEntities.add(reportDataEntity);

        ReportDataEntity reportDataEntity2 = new ReportDataEntity();
        reportDataEntity2.setHeaderName("CallNumber");
        reportDataEntity2.setHeaderValue("X123");
        reportDataEntities.add(reportDataEntity2);

        ReportDataEntity reportDataEntity3 = new ReportDataEntity();
        reportDataEntity3.setHeaderName("ItemId");
        reportDataEntity3.setHeaderValue("10412");
        reportDataEntities.add(reportDataEntity3);

        ReportDataEntity reportDataEntity4 = new ReportDataEntity();
        reportDataEntity4.setHeaderName("Institution");
        reportDataEntity4.setHeaderValue("CUL");
        reportDataEntities.add(reportDataEntity4);

        reportEntity.setReportDataEntities(reportDataEntities);

        return reportDetailRepository.save(reportEntity);
    }

    @Test
    public void testProcess() {
        dataExportEmailProcessor.setRequestId("sampleRecordForEtlLoadTest.xml");
        String dataHeader = ";currentPageCount#1";
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        ex.setMessage(in);
        ex.setIn(in);
        Map<String, Object> mapdata = new HashMap<>();
        mapdata.put("batchHeaders", dataHeader);
        try {
            dataExportEmailProcessor.process(ex);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}