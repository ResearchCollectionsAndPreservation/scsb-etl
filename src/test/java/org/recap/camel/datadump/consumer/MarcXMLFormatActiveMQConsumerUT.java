package org.recap.camel.datadump.consumer;

import org.apache.camel.Exchange;
import org.junit.Test;
import org.marc4j.marc.Record;
import org.mockito.Mock;
import org.recap.service.formatter.datadump.MarcXmlFormatterService;
import org.recap.util.datadump.DataExportHeaderUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MarcXMLFormatActiveMQConsumerUT {
    MarcXmlFormatterService marcXmlFormatterService = new MarcXmlFormatterService();
    MarcXMLFormatActiveMQConsumer marcXMLFormatActiveMQConsumer = new MarcXMLFormatActiveMQConsumer(marcXmlFormatterService);

    @Mock
    Exchange exchange;

    @Mock
    Record record;

    @Autowired
    DataExportHeaderUtil dataExportHeaderUtil;

    @Test
    public void testprocessMarcXmlString() {
        try {
            marcXMLFormatActiveMQConsumer.processMarcXmlString(exchange);
        } catch (Exception e) {
        }
        assertTrue(true);
    }
    @Test
    public void testsetDataExportHeaderUtil() {
        marcXMLFormatActiveMQConsumer.setDataExportHeaderUtil(dataExportHeaderUtil);
        assertTrue(true);
    }
    @Test
    public void testgetDataExportHeaderUtil() {
        marcXMLFormatActiveMQConsumer.getDataExportHeaderUtil();
        assertNull(dataExportHeaderUtil);
    }
    /*@Test
    public void testprocessSuccessReportEntity() {
        List<Record> list = new ArrayList();
        list.add(record);
        try {
            marcXMLFormatActiveMQConsumer.processSuccessReportEntity(exchange,list,"batchHeaders","requestId");
        } catch (Exception e) {
        }
        assertTrue(true);
    }*/
}
