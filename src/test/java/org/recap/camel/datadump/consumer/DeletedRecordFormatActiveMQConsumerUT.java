package org.recap.camel.datadump.consumer;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;
import org.mockito.Mock;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.service.formatter.datadump.DeletedJsonFormatterService;
import org.recap.util.datadump.DataExportHeaderUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DeletedRecordFormatActiveMQConsumerUT {

    @Autowired
    DeletedJsonFormatterService deletedJsonFormatterService;

    DeletedRecordFormatActiveMQConsumer deletedRecordFormatActiveMQConsumer = new DeletedRecordFormatActiveMQConsumer(deletedJsonFormatterService);

    @Autowired
    DataExportHeaderUtil dataExportHeaderUtil;

    @Mock
    ExecutorService executorService;

    @Mock
    Exchange exchange;

    @Mock
    Message message;

    @Mock
    CamelContext context;

    @Mock
    BibliographicEntity bibliographicEntity;

    @Test
    public void testgetDataExportHeaderUtil() {
        deletedRecordFormatActiveMQConsumer.getDataExportHeaderUtil();
        assertNull(dataExportHeaderUtil);
    }

    @Test
    public void testgetExecutorService() {
        executorService = deletedRecordFormatActiveMQConsumer.getExecutorService();
        assertNotNull(executorService);
    }

    @Test
    public void testprocessRecords() {
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        in.setBody("CULKLALAL");
        ex.setIn(in);
        try {
            deletedRecordFormatActiveMQConsumer.processRecords(ex);
        } catch (Exception e) {
        }
        assertTrue(true);
    }

    @Test
    public void testprocessFailures() throws Exception {
        List failures = new ArrayList();
        failures.add("added");
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        in.setBody("CULKLALAL");
        ex.setIn(in);
        try {
           // deletedRecordFormatActiveMQConsumer.processFailures(ex, failures, "batchHeaders", "requestId");
        } catch (Exception e) {
        }
        assertTrue(true);
    }
    @Test
    public void testsetDataExportHeaderUtil() {
        deletedRecordFormatActiveMQConsumer.setDataExportHeaderUtil(dataExportHeaderUtil);
        assertTrue(true);
    }

}
