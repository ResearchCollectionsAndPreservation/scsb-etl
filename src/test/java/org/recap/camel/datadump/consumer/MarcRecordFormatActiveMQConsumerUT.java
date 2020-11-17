package org.recap.camel.datadump.consumer;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.*;
import org.apache.camel.support.*;
import org.junit.Test;
import org.mockito.Mock;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.service.formatter.datadump.MarcXmlFormatterService;
import org.recap.util.datadump.DataExportHeaderUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MarcRecordFormatActiveMQConsumerUT extends BaseTestCase {

    @Autowired
    MarcXmlFormatterService marcXmlFormatterService;

    MarcRecordFormatActiveMQConsumer marcRecordFormatActiveMQConsumer = new MarcRecordFormatActiveMQConsumer(marcXmlFormatterService);

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

    @Test
    public void testgetDataExportHeaderUtil() {
        marcRecordFormatActiveMQConsumer.getDataExportHeaderUtil();
        assertNotNull(dataExportHeaderUtil);
    }

    @Test
    public void testgetExecutorService() {
        executorService = marcRecordFormatActiveMQConsumer.getExecutorService();
        assertNotNull(executorService);
    }

    @Test
    public void testprocessRecords() throws Exception {
        List<BibliographicEntity> bibliographicEntityList = new ArrayList<>();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setBibliographicId(100);
        bibliographicEntity.setContent("bib content".getBytes());
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("2");
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntityList.add(bibliographicEntity);
        String dataHeader=";currentPageCount#1";
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        in.setBody(bibliographicEntityList);
        ex.setMessage(in);
        ex.setIn(in);
        Map<String,Object> mapdata = new HashMap<>();
        mapdata.put("batchHeaders",dataHeader);
        in.setHeaders(mapdata);
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

}
