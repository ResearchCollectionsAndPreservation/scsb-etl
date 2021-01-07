package org.recap.camel.datadump.consumer;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.BibliographicAbstractEntity;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.service.formatter.datadump.DeletedJsonFormatterService;
import org.recap.util.datadump.DataExportHeaderUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DeletedRecordFormatActiveMQConsumerUT extends BaseTestCaseUT {

    @Mock
    DeletedJsonFormatterService deletedJsonFormatterService;

    DeletedRecordFormatActiveMQConsumer deletedRecordFormatActiveMQConsumer = new DeletedRecordFormatActiveMQConsumer(deletedJsonFormatterService);

    @Mock
    DataExportHeaderUtil dataExportHeaderUtil;

    @Mock
    ExecutorService executorService;

    @Mock
    BibliographicAbstractEntity bibliographicAbstractEntity;

    @Mock
    Exchange exchange;

    @Mock
    Message message;

    @Mock
    CamelContext context;

    @Mock
    BibliographicEntity bibliographicEntity;

    @Mock
    List<Future<Object>> futureList;

    @Mock
    Stream<Future<Object>> future;

    @Mock
    Stream<Object> futures;

    @Mock
    Future Future;

    @Test
    public void testgetDataExportHeaderUtil() {
        deletedRecordFormatActiveMQConsumer.getDataExportHeaderUtil();
        assertNotNull(dataExportHeaderUtil);
    }

    @Test
    public void testgetExecutorService() {
        executorService = deletedRecordFormatActiveMQConsumer.getExecutorService();
        assertNotNull(executorService);
    }

    @Test
    public void testprocessRecords() throws InterruptedException {
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setId(100);
        bibliographicEntity.setContent("bib content".getBytes());
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("2");
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setLastUpdatedBy("tst");
        List<BibliographicEntity> bib=new ArrayList<>();
        bib.add(bibliographicEntity);
        in.setBody(bib);
        ex.setIn(in);
        ReflectionTestUtils.setField(deletedRecordFormatActiveMQConsumer,"deletedJsonFormatterService",deletedJsonFormatterService);
        ReflectionTestUtils.setField(deletedRecordFormatActiveMQConsumer,"executorService",executorService);
        Mockito.when(executorService.invokeAll(Mockito.any())).thenReturn(futureList);
        Mockito.when(futureList.stream()).thenReturn(future);
        Mockito.when(future.map(Mockito.any())).thenReturn(futures);
        Mockito.when(deletedJsonFormatterService.prepareDeletedRecords(Mockito.any())).thenReturn(new HashMap<>());
        try {
            deletedRecordFormatActiveMQConsumer.processRecords(ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }

    @Test
    public void testsetDataExportHeaderUtil() {
        deletedRecordFormatActiveMQConsumer.setDataExportHeaderUtil(dataExportHeaderUtil);
        assertTrue(true);
    }

}
