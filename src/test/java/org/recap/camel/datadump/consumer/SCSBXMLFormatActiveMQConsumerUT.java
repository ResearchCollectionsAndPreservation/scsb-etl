package org.recap.camel.datadump.consumer;

import org.apache.camel.*;
import org.apache.camel.impl.*;
import org.apache.camel.support.*;
import org.junit.*;
import org.mockito.*;
import org.recap.*;
import org.recap.model.jaxb.*;
import org.recap.model.jaxb.marc.*;
import org.recap.service.formatter.datadump.*;
import org.recap.util.*;

import javax.persistence.*;
import java.util.*;

public class SCSBXMLFormatActiveMQConsumerUT extends BaseTestCase {

    SCSBXMLFormatActiveMQConsumer scsbXMLFormatActiveMQConsumer;
    @Mock
    SCSBXmlFormatterService scsbXmlFormatterService;
    @Mock
    XmlFormatter xmlFormatter;
    @Before
    public void testBefore(){
        scsbXmlFormatterService = Mockito.mock(SCSBXmlFormatterService.class);
        xmlFormatter = Mockito.mock(XmlFormatter.class);
    }
    @Test
    public void testProcessSCSBXmlString(){
        scsbXMLFormatActiveMQConsumer = new SCSBXMLFormatActiveMQConsumer(scsbXmlFormatterService,xmlFormatter);
        List<BibRecord> records = new ArrayList<>();
        Bib bib = new Bib();
        bib.setOwningInstitutionId("101");
        bib.setOwningInstitutionBibId("111");
        BibRecord bibRecord = new BibRecord();
        bibRecord.setBib(bib);
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
        String dataHeader=";requestId#1";
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        ex.setMessage(in);
        in.setBody(records);
        ex.setIn(in);
        Map<String,Object> mapdata = new HashMap<>();
        mapdata.put("batchHeaders",dataHeader);
        in.setHeaders(mapdata);
        try {
            scsbXMLFormatActiveMQConsumer.processSCSBXmlString(ex);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
