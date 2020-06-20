package org.recap.repository;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by SheikS on 8/8/2016.
 */
public class ReportDetailRepositoryUT extends BaseTestCase {

    @Autowired
    private ReportDetailRepository reportDetailRepository;

    @Test
    public void testSaveReportEntity() {

        ReportEntity savedReportEntity = saveReportEntity();

        assertNotNull(savedReportEntity);
        assertNotNull(savedReportEntity.getId());
        List<ReportDataEntity> savedReportDataEntities = savedReportEntity.getReportDataEntities();
        for (Iterator<ReportDataEntity> iterator = savedReportDataEntities.iterator(); iterator.hasNext(); ) {
            ReportDataEntity savedReportDataEntity = iterator.next();
            System.out.println(savedReportDataEntity.getHeaderName() + " : " + savedReportDataEntity.getHeaderValue());
        }
    }

    @Test
    public void saveAndFindReportEntity() {
        ReportEntity reportEntity = saveReportEntity();

        List<ReportEntity> reportEntities = reportDetailRepository.findByFileName(reportEntity.getFileName());

        assertNotNull(reportEntities);
        ReportEntity reportEntity1 = reportEntities.get(0);
        assertNotNull(reportEntity1);
        assertNotNull(reportEntity1.getReportDataEntities());
    }

    private ReportEntity saveReportEntity() {
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();

        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName("test.xml");
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

    @Test
    public void testFindByFileAndDateRange() throws Exception {
        ReportEntity reportEntity = saveReportEntity();
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

        List<ReportEntity> reportEntities = reportDetailRepository.findByFileAndDateRange("test.xml", from, to);
        assertNotNull(reportEntities);
        assertNotNull(reportEntities.get(0));
    }

    @Test
    public void testFindByFileAndType() throws Exception {
        ReportEntity reportEntity = saveReportEntity();

        List<ReportEntity> reportEntities = reportDetailRepository.findByFileNameAndType(reportEntity.getFileName(), reportEntity.getType());
        assertNotNull(reportEntities);
        assertNotNull(reportEntities.get(0));
    }

    @Test
    public void testFindByTypeAndDateRange() throws Exception {
        ReportEntity reportEntity = saveReportEntity();
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

        List<ReportEntity> reportEntities = reportDetailRepository.findByTypeAndDateRange(reportEntity.getType(), from, to);
        assertNotNull(reportEntities);
        assertNotNull(reportEntities.get(0));
    }

    @Test
    public void testFindByFileAndTypeAndDateRange() throws Exception {
        ReportEntity reportEntity = saveReportEntity();
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

        List<ReportEntity> reportEntities = reportDetailRepository.findByFileAndTypeAndDateRange(reportEntity.getFileName(), reportEntity.getType(), from, to);
        assertNotNull(reportEntities);
        assertNotNull(reportEntities.get(0));
    }

    @Test
    public void testFindByFileNameAndInstitutionName() throws Exception {
        ReportEntity reportEntity = saveReportEntity();

        List<ReportEntity> reportEntities = reportDetailRepository.findByFileNameAndInstitutionName(reportEntity.getFileName(), reportEntity.getInstitutionName());
        assertNotNull(reportEntities);
        assertNotNull(reportEntities.get(0));
    }

    @Test
    public void testFindByFileNameAndInstitutionNameAndType() throws Exception {
        ReportEntity reportEntity = saveReportEntity();

        List<ReportEntity> reportEntities = reportDetailRepository.findByFileNameAndInstitutionNameAndType(reportEntity.getFileName(), reportEntity.getInstitutionName(), reportEntity.getType());
        assertNotNull(reportEntities);
        assertNotNull(reportEntities.get(0));
    }

    @Test
    public void testFindByFileAndInstitutionAndTypeAndDateRange() throws Exception {
        ReportEntity reportEntity = saveReportEntity();
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

        List<ReportEntity> reportEntities = reportDetailRepository.findByFileAndInstitutionAndTypeAndDateRange(reportEntity.getFileName(), reportEntity.getInstitutionName(), reportEntity.getType(), from, to);
        assertNotNull(reportEntities);
        assertNotNull(reportEntities.get(0));
    }

    @Test
    public void testFindByInstitutionAndTypeAndDateRange() throws Exception {
        ReportEntity reportEntity = saveReportEntity();
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

        List<ReportEntity> reportEntities = reportDetailRepository.findByInstitutionAndTypeAndDateRange(reportEntity.getInstitutionName(), reportEntity.getType(), from, to);
        assertNotNull(reportEntities);
        assertNotNull(reportEntities.get(0));
    }

}