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
import org.recap.model.jaxb.*;
import org.recap.model.jaxb.marc.ContentType;
import org.recap.service.formatter.datadump.SCSBXmlFormatterService;
import org.recap.util.XmlFormatter;
import org.recap.util.datadump.DataExportHeaderUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

public class SCSBXMLFormatActiveMQConsumerUT extends BaseTestCaseUT {

    @InjectMocks
    @Spy
    SCSBXMLFormatActiveMQConsumer scsbXMLFormatActiveMQConsumer;

    @Mock
    SCSBXmlFormatterService scsbXmlFormatterService;

    @Mock
    XmlFormatter xmlFormatter;

    @Mock
    DataExportHeaderUtil dataExportHeaderUtil;

    @Test
    public void testProcessSCSBXmlString() throws Exception {
        BibRecord bibRecord = new BibRecord();
        bibRecord.setBib(getBib());
        extracted();
        String dataHeader = ";requestId#1";
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        ex.setMessage(in);
        in.setBody(Arrays.asList(bibRecord));
        ex.setIn(in);
        Map<String, Object> mapdata = new HashMap<>();
        mapdata.put("batchHeaders", dataHeader);
        in.setHeaders(mapdata);
        scsbXMLFormatActiveMQConsumer.setDataExportHeaderUtil(dataExportHeaderUtil);
        Mockito.when(scsbXmlFormatterService.getSCSBXmlForBibRecords(any())).thenReturn("testXMl");
        Mockito.doNothing().when(scsbXMLFormatActiveMQConsumer).processSuccessReport(any(), any(), any(), any(), any());
        String result = scsbXMLFormatActiveMQConsumer.processSCSBXmlString(ex);
        assertNotNull(result);
        assertEquals("testXMl", result);
    }

    @Test
    public void testProcessSCSBXmlStringException() throws Exception {
        BibRecord bibRecord = new BibRecord();
        bibRecord.setBib(getBib());
        extracted();
        String dataHeader = ";requestId#1";
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        ex.setMessage(in);
        in.setBody(Arrays.asList(bibRecord));
        ex.setIn(in);
        Map<String, Object> mapdata = new HashMap<>();
        mapdata.put("batchHeaders", dataHeader);
        in.setHeaders(mapdata);
        scsbXMLFormatActiveMQConsumer.setDataExportHeaderUtil(dataExportHeaderUtil);
        Mockito.when(scsbXmlFormatterService.getSCSBXmlForBibRecords(any())).thenReturn("testXMl");
        try {
            String result = scsbXMLFormatActiveMQConsumer.processSCSBXmlString(ex);
        } catch (Exception e) {
        }
    }

    @Test
    public void getDataExportHeaderUtil() {
        SCSBXMLFormatActiveMQConsumer scsbXMLFormatActiveMQConsumer = new SCSBXMLFormatActiveMQConsumer(scsbXmlFormatterService, xmlFormatter);
        DataExportHeaderUtil dataExportHeaderUtil = scsbXMLFormatActiveMQConsumer.getDataExportHeaderUtil();
        assertNotNull(dataExportHeaderUtil);
    }

    private void extracted() {
        Holdings holdings = new Holdings();
        Holding holding1 = new Holding();
        holding1.setOwningInstitutionHoldingsId("h-101");
        holding1.setContent(new ContentType());
        Items items = new Items();
        items.setContent(new ContentType());
        holding1.setItems(Arrays.asList(items));
        Holding holding2 = new Holding();
        holding2.setOwningInstitutionHoldingsId("h-102");
        holdings.setHolding(Arrays.asList(holding1, holding2));
    }

    private Bib getBib() {
        Bib bib = new Bib();
        bib.setOwningInstitutionId("101");
        bib.setOwningInstitutionBibId("111");
        return bib;
    }
}
