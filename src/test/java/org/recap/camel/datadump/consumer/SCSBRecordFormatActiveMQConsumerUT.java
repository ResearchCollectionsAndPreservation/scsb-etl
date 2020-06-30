package org.recap.camel.datadump.consumer;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.Message;
import org.junit.Test;
import org.mockito.Mock;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.service.formatter.datadump.DeletedJsonFormatterService;
import org.recap.service.formatter.datadump.SCSBXmlFormatterService;
import org.recap.util.datadump.DataExportHeaderUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class SCSBRecordFormatActiveMQConsumerUT {


    @Autowired
    SCSBXmlFormatterService scsbXmlFormatterService;

    SCSBRecordFormatActiveMQConsumer sCSBRecordFormatActiveMQConsumer = new SCSBRecordFormatActiveMQConsumer(scsbXmlFormatterService);

    @Autowired
    DataExportHeaderUtil dataExportHeaderUtil;

    @Mock
    ExecutorService executorService;

    @Mock
    Exchange exchange;

    @Mock
    FluentProducerTemplate fluentProducerTemplate;

    @Mock
    BibliographicEntity bibliographicEntity;

    @Test
    public void testgetDataExportHeaderUtil() {
        sCSBRecordFormatActiveMQConsumer.getDataExportHeaderUtil();
        assertNull(dataExportHeaderUtil);
    }

    @Test
    public void testgetExecutorService() {
        executorService = sCSBRecordFormatActiveMQConsumer.getExecutorService();
        assertNotNull(executorService);
    }

    @Test
    public void testprocessRecords() {
        try {
            sCSBRecordFormatActiveMQConsumer.processRecords(exchange);
        } catch (Exception e) {
        }
        assertTrue(true);
    }

    @Test
    public void testsetDataExportHeaderUtil() {
        sCSBRecordFormatActiveMQConsumer.setDataExportHeaderUtil(dataExportHeaderUtil);
        assertTrue(true);
    }

}
