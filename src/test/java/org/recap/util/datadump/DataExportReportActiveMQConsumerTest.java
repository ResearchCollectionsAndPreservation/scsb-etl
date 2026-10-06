package org.recap.util.datadump;

import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.camel.datadump.consumer.DataExportReportActiveMQConsumer;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;
import org.recap.repositoryrw.ReportDetailRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * Created by peris on 11/11/16.
 */
public class DataExportReportActiveMQConsumerTest {

    @InjectMocks
    DataExportReportActiveMQConsumer dataExportReportActiveMQConsumer;
    @Mock
    ReportDetailRepository mockReportDetailsRepository;
    @Mock
    ProducerTemplate producerTemplate;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void processNewSuccessReportEntity() throws Exception {
        DataExportReportActiveMQConsumer dataExportReportActiveMQConsumer = new DataExportReportActiveMQConsumer();
        HashMap values = new HashMap();
        values.put(ScsbConstants.REQUESTING_INST_CODE, "PUL");
        values.put(ScsbConstants.NUM_RECORDS, "12");
        values.put(ScsbConstants.NUM_BIBS_EXPORTED, "Num Bibs Exported");
        values.put(ScsbConstants.BATCH_EXPORT, "Batch Export");
        values.put(ScsbCommonConstants.REQUEST_ID, "PUL-2017-12-12 11");

        dataExportReportActiveMQConsumer.setReportDetailRepository(mockReportDetailsRepository);

        ReportEntity reportEntity = dataExportReportActiveMQConsumer.saveSuccessReportEntity(values);

        assertNotNull(reportEntity);


    }

    @Test
    public void getFetchTypeFullDump() {
        String fetchTypeNumber = "10";
        String fetchType = dataExportReportActiveMQConsumer.getFetchType(fetchTypeNumber);
        assertNotNull(fetchType);
        assertEquals("Full Dump", fetchType);
    }

    @Test
    public void getFetchTypeIncremental() {
        String fetchTypeNumber = "1";
        String fetchType = dataExportReportActiveMQConsumer.getFetchType(fetchTypeNumber);
        assertNotNull(fetchType);
        assertEquals("Incremental", fetchType);
    }

    @Test
    public void getFetchTypeDeleted() {
        String fetchTypeNumber = "2";
        String fetchType = dataExportReportActiveMQConsumer.getFetchType(fetchTypeNumber);
        assertNotNull(fetchType);
        assertEquals("Deleted", fetchType);
    }

    @Test
    public void getFetchTypeExport() {
        String fetchTypeNumber = "0";
        String fetchType = dataExportReportActiveMQConsumer.getFetchType(fetchTypeNumber);
        assertNotNull(fetchType);
        assertEquals("Export", fetchType);
    }

    @Test
    public void processExistingSuccessReportEntity() throws Exception {
        DataExportReportActiveMQConsumer dataExportReportActiveMQConsumer = new DataExportReportActiveMQConsumer();
        HashMap values = new HashMap();
        values.put(ScsbConstants.REQUESTING_INST_CODE, "PUL");
        values.put(ScsbConstants.NUM_RECORDS, "12");
        values.put(ScsbConstants.NUM_BIBS_EXPORTED, "NoOfBibsExported");
        values.put(ScsbConstants.BATCH_EXPORT, ScsbConstants.BATCH_EXPORT_SUCCESS);
        values.put(ScsbCommonConstants.REQUEST_ID, "PUL-2017-12-12 11");
        values.put(ScsbConstants.ITEM_EXPORTED_COUNT, 1);
        dataExportReportActiveMQConsumer.setReportDetailRepository(mockReportDetailsRepository);

        ReportEntity savedReportEntity = dataExportReportActiveMQConsumer.saveSuccessReportEntity(values);


        Mockito.when(mockReportDetailsRepository.findByFileNameAndType("PUL-2017-12-12 11", ScsbConstants.BATCH_EXPORT_SUCCESS)).thenReturn(Arrays.asList(savedReportEntity));
        values.put(ScsbConstants.NUM_RECORDS, "10");
        ReportEntity updatedReportEntity = dataExportReportActiveMQConsumer.saveSuccessReportEntity(values);
        assertNotNull(updatedReportEntity);
        List<ReportDataEntity> updatedReportDataEntities = updatedReportEntity.getReportDataEntities();
        for (Iterator<ReportDataEntity> iterator = updatedReportDataEntities.iterator(); iterator.hasNext(); ) {
            ReportDataEntity reportDataEntity = iterator.next();
            if (reportDataEntity.getHeaderName().equals("NoOfBibsExported")) {
                assertEquals("22", reportDataEntity.getHeaderValue());
            }
        }
    }

    @Test
    public void processNewFailureReportEntity() throws Exception {
        HashMap values = new HashMap();
        values.put(ScsbConstants.REQUESTING_INST_CODE, "PUL");
        values.put(ScsbConstants.FAILED_BIBS, "2");
        values.put(ScsbConstants.INSTITUTION_CODES, "PUL");
        values.put(ScsbConstants.FETCH_TYPE, "Full");
        values.put(ScsbConstants.FAILURE_LIST, Arrays.asList("PUL\\*CUL", "RECALL\\*FAILED"));
        values.put(ScsbConstants.FAILURE_CAUSE, "Bad happened");
        values.put(ScsbConstants.BATCH_EXPORT, "Batch Export");
        values.put(ScsbCommonConstants.REQUEST_ID, "PUL-2017-12-12 11");
        dataExportReportActiveMQConsumer.setReportDetailRepository(mockReportDetailsRepository);

        ReportEntity savedReportEntity = dataExportReportActiveMQConsumer.saveFailureReportEntity(values);
        assertNotNull(savedReportEntity);
    }

    @Test
    public void processNewFailureReportEntityWithFileName() throws Exception {
        HashMap values = new HashMap();
        values.put(ScsbConstants.REQUESTING_INST_CODE, "PUL");
        values.put(ScsbConstants.FAILED_BIBS, "2");
        values.put(ScsbConstants.INSTITUTION_CODES, "PUL");
        values.put(ScsbConstants.FETCH_TYPE, "Full");
        values.put(ScsbConstants.FAILURE_LIST, Arrays.asList("PUL\\*CUL", "RECALL\\*FAILED"));
        values.put(ScsbConstants.FAILURE_CAUSE, "Bad happened");
        values.put(ScsbConstants.BATCH_EXPORT, "Batch Export");
        values.put(ScsbCommonConstants.REQUEST_ID, "PUL-2017-12-12 11");
        values.put(ScsbConstants.NUM_RECORDS, "123");
        dataExportReportActiveMQConsumer.setReportDetailRepository(mockReportDetailsRepository);
        Mockito.when(mockReportDetailsRepository.findByFileNameAndType(any(), anyString())).thenReturn(Arrays.asList(getReportEntity()));
        ReportEntity savedReportEntity = dataExportReportActiveMQConsumer.saveFailureReportEntity(values);
        assertNotNull(savedReportEntity);
    }

    @Test
    public void processExistingFailureReportEntity() throws Exception {
        HashMap values = new HashMap();
        values.put(ScsbConstants.REQUESTING_INST_CODE, "PUL");
        values.put(ScsbConstants.INSTITUTION_CODES, "PUL");
        values.put(ScsbConstants.FETCH_TYPE, "Incremental");
        values.put(ScsbConstants.FAILURE_LIST, Arrays.asList("PUL\\*CUL", "RECALL\\*FAILED"));
        values.put(ScsbConstants.FAILED_BIBS, "2");
        values.put(ScsbConstants.FAILURE_CAUSE, "Bad happened");
        values.put(ScsbConstants.BATCH_EXPORT, "Batch Export");
        values.put(ScsbCommonConstants.REQUEST_ID, "PUL-2017-12-12 11");
        dataExportReportActiveMQConsumer.setReportDetailRepository(mockReportDetailsRepository);

        ReportEntity savedReportEntity = dataExportReportActiveMQConsumer.saveFailureReportEntity(values);


        Mockito.when(mockReportDetailsRepository.findByFileNameAndType("PUL-2017-12-12 11", ScsbConstants.BATCH_EXPORT_FAILURE)).thenReturn(Arrays.asList(savedReportEntity));
        values.put(ScsbConstants.NUM_RECORDS, "3");
        ReportEntity updatedReportEntity = dataExportReportActiveMQConsumer.saveFailureReportEntity(values);
        assertNotNull(updatedReportEntity);
        List<ReportDataEntity> updatedReportDataEntities = updatedReportEntity.getReportDataEntities();
        for (Iterator<ReportDataEntity> iterator = updatedReportDataEntities.iterator(); iterator.hasNext(); ) {
            ReportDataEntity reportDataEntity = iterator.next();
            if (reportDataEntity.getHeaderName().equals(ScsbConstants.FAILED_BIBS)) {
                assertEquals("5", reportDataEntity.getHeaderValue());
            }
        }
    }

    private ReportEntity getReportEntity() {
        ReportEntity reportEntity = new ReportEntity();
        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setHeaderName(ScsbConstants.FAILED_BIBS);
        reportDataEntity.setHeaderValue(ScsbConstants.DATADUMP_FETCHTYPE_INCREMENTAL);
        reportEntity.setReportDataEntities(Arrays.asList(reportDataEntity));
        reportEntity.setCreatedDate(new Date());
        return reportEntity;
    }
}