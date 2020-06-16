package org.recap.camel.datadump;

import org.apache.camel.Exchange;
import org.junit.Test;
import org.mockito.Mock;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.ReportDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 19/4/17.
 */
public class DataExportEmailProcessorUT extends BaseTestCase{

    @Autowired
    DataExportEmailProcessor dataExportEmailProcessor;

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Mock
    Exchange exchange;

    @Test
    public void testDataExportEmailProcess() throws Exception {
        ReportEntity reportEntity = saveReportEntity();
        dataExportEmailProcessor.setTransmissionType("2");
        dataExportEmailProcessor.setInstitutionCodes(Arrays.asList("PUL","CUL"));
        dataExportEmailProcessor.setRequestingInstitutionCode("NYPL");
        dataExportEmailProcessor.setFolderName("test");
        dataExportEmailProcessor.setToEmailId("hemalatha.s@htcindia.com");
        dataExportEmailProcessor.setRequestId(reportEntity.getFileName());
        dataExportEmailProcessor.setFetchType("1");
        dataExportEmailProcessor.process(exchange);
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
        reportEntity.setType(RecapConstants.FAILURE);
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

}