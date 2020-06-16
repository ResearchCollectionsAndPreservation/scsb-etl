package org.recap.camel;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.ReportDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by peris on 8/12/16.
 */
public class ReportRoutesBuilder_UT extends BaseTestCase {

    @Autowired
    private ProducerTemplate producer;

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Test
    public void failureReportEntity() throws Exception {
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setType(RecapConstants.FAILURE);
        reportEntity.setFileName("test.xml");
        reportEntity.setCreatedDate(new Date());
        reportEntity.setInstitutionName("CUL");

        ReportDataEntity reportDataEntity1 = new ReportDataEntity();
        reportDataEntity1.setHeaderName("barcode");
        reportDataEntity1.setHeaderValue("123");

        ArrayList<ReportDataEntity> reportDataEntities = new ArrayList<>();
        reportDataEntities.add(reportDataEntity1);
        reportEntity.setReportDataEntities(reportDataEntities);

        producer.sendBody(RecapConstants.REPORT_Q, reportEntity);

        Thread.sleep(1000);

        List<ReportEntity> savedReportEntity = reportDetailRepository.findByFileName(reportEntity.getFileName());
        assertNotNull(savedReportEntity);
        assertNotNull(savedReportEntity.get(0));

    }
}
