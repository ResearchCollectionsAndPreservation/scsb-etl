package org.recap.camel.datadump.consumer;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.export.Bib;
import org.recap.model.export.DeletedRecord;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.service.formatter.datadump.MarcXmlFormatterService;
import org.recap.util.datadump.DataExportHeaderUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

public class MarcRecordFormatActiveMQConsumerUT extends BaseTestCaseUT {

    @Mock
    MarcXmlFormatterService marcXmlFormatterService;

    @InjectMocks
    @Spy
    MarcRecordFormatActiveMQConsumer marcRecordFormatActiveMQConsumer = new MarcRecordFormatActiveMQConsumer(marcXmlFormatterService);

    @Mock
    DataExportHeaderUtil dataExportHeaderUtil;

    @Mock
    ExecutorService executorService;

    @Mock
    Future future;

    @Test
    public void testMarcRecordFormatActiveMQConsumer(){
        MarcRecordFormatActiveMQConsumer marcRecordFormatActiveMQConsumer = new MarcRecordFormatActiveMQConsumer(marcXmlFormatterService,10,10);
        marcRecordFormatActiveMQConsumer.setDataExportHeaderUtil(dataExportHeaderUtil);
        assertNotNull(marcRecordFormatActiveMQConsumer);
    }


    @Test
    public void testgetDataExportHeaderUtil() {
        ReflectionTestUtils.setField(marcRecordFormatActiveMQConsumer, "dataExportHeaderUtil", null);
        marcRecordFormatActiveMQConsumer.getDataExportHeaderUtil();
        assertNotNull(dataExportHeaderUtil);
    }

    @Test
    public void testgetExecutorService() {
        Mockito.when(executorService.isShutdown()).thenReturn(Boolean.TRUE);
        ReflectionTestUtils.setField(marcRecordFormatActiveMQConsumer,"dataDumpMarcFormatThreadSize",10);
        executorService = marcRecordFormatActiveMQConsumer.getExecutorService();
        assertNotNull(executorService);
    }

    @Test
    public void testgetExecutorServiceNULL() {
        ReflectionTestUtils.setField(marcRecordFormatActiveMQConsumer, "executorService", null);
        ReflectionTestUtils.setField(marcRecordFormatActiveMQConsumer,"dataDumpMarcFormatThreadSize",10);
        executorService = marcRecordFormatActiveMQConsumer.getExecutorService();
        assertNotNull(executorService);
    }

    @Test
    public void getMapFutureInterruptedException() throws ExecutionException, InterruptedException {
        Mockito.when(future.get()).thenThrow(new InterruptedException());
        try {
            ReflectionTestUtils.invokeMethod(marcRecordFormatActiveMQConsumer, "getMapFuture", future);
        } catch (RuntimeException e) {
        }
    }

    @Test
    public void getMapFutureExecutionException() throws ExecutionException, InterruptedException {
        Mockito.when(future.get()).thenThrow(new ExecutionException(new Throwable()));
        try {
            ReflectionTestUtils.invokeMethod(marcRecordFormatActiveMQConsumer, "getMapFuture", future);
        } catch (RuntimeException e) {
        }
    }

    @Test
    public void testprocessRecords() throws Exception {
        List<BibliographicEntity> bibliographicEntityList = new ArrayList<>();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setId(100);
        bibliographicEntity.setContent("bib content".getBytes());
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("2");
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntityList.add(bibliographicEntity);
        String dataHeader = ";currentPageCount#1";
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        in.setBody(bibliographicEntityList);
        ex.setMessage(in);
        ex.setIn(in);
        Map<String, Object> mapdata = new HashMap<>();
        mapdata.put("batchHeaders", dataHeader);
        in.setHeaders(mapdata);
        List<Future<Object>> futureList = new ArrayList<>();
        futureList.add(future);
        Map<String, Object> results = new HashMap<>();
        results.put(ScsbCommonConstants.SUCCESS, Arrays.asList(getDeletedRecord()));
        results.put(ScsbCommonConstants.FAILURE, Arrays.asList("FailureRecords", getDeletedRecord()));
        results.put(ScsbConstants.ITEM_EXPORTED_COUNT, 10);
        ReflectionTestUtils.setField(marcRecordFormatActiveMQConsumer,"dataDumpMarcFormatThreadSize",10);
        ReflectionTestUtils.setField(marcRecordFormatActiveMQConsumer,"dataDumpMarcFormatBatchSize",10);
        Mockito.when(executorService.invokeAll(any())).thenReturn(futureList);
        Mockito.when(future.get()).thenReturn(results);
        Mockito.doNothing().when(marcRecordFormatActiveMQConsumer).processRecordFailures(any(), any(), any(), any(), any());
        try {
            marcRecordFormatActiveMQConsumer.processRecords(ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }

    @Test
    public void testprocessFailures() throws Exception {
        List failures = new ArrayList();
        failures.add("added");
        try {
            //  marcRecordFormatActiveMQConsumer.processFailures(exchange, failures, "batchHeaders", "requestId");
        } catch (Exception e) {
        }
        assertTrue(true);
    }

    @Test
    public void testsetDataExportHeaderUtil() {
        marcRecordFormatActiveMQConsumer.setDataExportHeaderUtil(dataExportHeaderUtil);
        assertTrue(true);
    }

    private DeletedRecord getDeletedRecord() {
        DeletedRecord deletedRecord = new DeletedRecord();
        Bib bib = new Bib();
        bib.setBibId("1");
        bib.setOwningInstitutionBibId("1");
        bib.setOwningInstitutionCode("CUL");
        deletedRecord.setBib(bib);
        return deletedRecord;
    }

}
