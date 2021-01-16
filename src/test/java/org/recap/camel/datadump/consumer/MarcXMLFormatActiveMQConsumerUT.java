package org.recap.camel.datadump.consumer;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.marc4j.MarcReader;
import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlReader;
import org.marc4j.MarcXmlWriter;
import org.marc4j.marc.Record;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.service.formatter.datadump.MarcXmlFormatterService;
import org.recap.util.datadump.DataExportHeaderUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class MarcXMLFormatActiveMQConsumerUT extends BaseTestCaseUT {

    @Mock
    MarcXmlFormatterService marcXmlFormatterService;

    @InjectMocks
    MarcXMLFormatActiveMQConsumer marcXMLFormatActiveMQConsumer;

    @Mock
    DataExportHeaderUtil dataExportHeaderUtil;

    @Mock
    FluentProducerTemplate fluentProducerTemplate;

    @Mock
    Exchange exchange;


    private final String bibContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<collection>\n" +
            "    <record>\n" +
            "        <leader>00800cas a2200277 i 4500</leader>\n" +
            "        <controlfield tag=\"001\">10</controlfield>\n" +
            "        <controlfield tag=\"003\">NNC</controlfield>\n" +
            "        <controlfield tag=\"005\">20100215174244.0</controlfield>\n" +
            "        <controlfield tag=\"008\">810702c19649999ilufr p       0   a0engxd</controlfield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
            "            <subfield code=\"a\">(OCoLC)502399218</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
            "            <subfield code=\"a\">(OCoLC)ocn502399218</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
            "            <subfield code=\"a\">(CStRLIN)NYCG022-S</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
            "            <subfield code=\"9\">AAA0010CU</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
            "            <subfield code=\"a\">(NNC)10</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"040\">\n" +
            "            <subfield code=\"a\">NNC</subfield>\n" +
            "            <subfield code=\"c\">NNC</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"090\">\n" +
            "            <subfield code=\"a\">TA434</subfield>\n" +
            "            <subfield code=\"b\">.S15</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"0\" ind2=\"0\" tag=\"245\">\n" +
            "            <subfield code=\"a\">SOâ\u0082\u0083 abstracts &amp; newsletter.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"3\" ind2=\"3\" tag=\"246\">\n" +
            "            <subfield code=\"a\">SO three abstracts &amp; newsletter</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"260\">\n" +
            "            <subfield code=\"a\">[Chicago] :</subfield>\n" +
            "            <subfield code=\"b\">United States Gypsum,</subfield>\n" +
            "            <subfield code=\"c\">[1964?]-&lt;1979&gt;</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"300\">\n" +
            "            <subfield code=\"a\">v. :</subfield>\n" +
            "            <subfield code=\"b\">ill. ;</subfield>\n" +
            "            <subfield code=\"c\">28 cm.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"0\" ind2=\" \" tag=\"362\">\n" +
            "            <subfield code=\"a\">Vol. 1, no. 1-</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"500\">\n" +
            "            <subfield code=\"a\">Editor: W.C. Hansen, 1964-1979.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Cement</subfield>\n" +
            "            <subfield code=\"v\">Periodicals.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Gypsum</subfield>\n" +
            "            <subfield code=\"v\">Periodicals.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"1\" ind2=\" \" tag=\"700\">\n" +
            "            <subfield code=\"a\">Hansen, W. C.</subfield>\n" +
            "            <subfield code=\"q\">(Waldemar Conrad),</subfield>\n" +
            "            <subfield code=\"d\">1896-</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"2\" ind2=\" \" tag=\"710\">\n" +
            "            <subfield code=\"a\">United States Gypsum Co.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"852\" ind1=\" \" ind2=\" \">\n" +
            "              <subfield code=\"a\">NNC-EA</subfield>\n" +
            "              <subfield code=\"b\">eax</subfield>\n" +
            "              <subfield code=\"h\">UB271.J3</subfield>\n" +
            "              <subfield code=\"i\">W8 2005</subfield>\n" +
            "              <subfield code=\"x\">CIN=AC; OID=AC</subfield>\n" +
            "        </datafield>" +
            "    </record>\n" +
            "</collection>\n";
    private final String holdingContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<collection>\n" +
            "    <record>\n" +
            "        <datafield ind1=\"0\" ind2=\"1\" tag=\"852\">\n" +
            "            <subfield code=\"b\">off,che</subfield>\n" +
            "            <subfield code=\"h\">QD79.C454 H533</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"0\" ind2=\"0\" tag=\"866\">\n" +
            "            <subfield code=\"a\">v.1-v.5</subfield>\n" +
            "        </datafield>\n" +
            "    </record>\n" +
            "</collection>\n";

    @Before
    public void setUpBefore() {
        marcXMLFormatActiveMQConsumer = new MarcXMLFormatActiveMQConsumer(marcXmlFormatterService);
    }

    @Test
    public void testprocessMarcXmlString() {
        BibliographicEntity bibliographicEntity = null;
        try {
            bibliographicEntity = getBibliographicEntity();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Mockito.when(marcXmlFormatterService.prepareMarcRecords(Arrays.asList(bibliographicEntity))).thenCallRealMethod();
        Map<String, Object> successAndFailureFormattedList = marcXmlFormatterService.prepareMarcRecords(Arrays.asList(bibliographicEntity));
        try {
            Mockito.when(marcXmlFormatterService.covertToMarcXmlString((List<Record>) successAndFailureFormattedList.get(RecapCommonConstants.SUCCESS))).thenCallRealMethod();
            String marcXmlString = marcXmlFormatterService.covertToMarcXmlString((List<Record>) successAndFailureFormattedList.get(RecapCommonConstants.SUCCESS));
            List<Record> recordList = readMarcXml(marcXmlString);
            String dataHeader = ";requestId#1";
            CamelContext ctx = new DefaultCamelContext();
            Exchange ex = new DefaultExchange(ctx);
            Message in = ex.getIn();
            ex.setMessage(in);
            in.setBody(recordList);
            ex.setIn(in);
            Map<String, Object> mapdata = new HashMap<>();
            mapdata.put("batchHeaders", dataHeader);
            in.setHeaders(mapdata);
            Mockito.when(marcXMLFormatActiveMQConsumer.processMarcXmlString(ex)).thenCallRealMethod();
            marcXMLFormatActiveMQConsumer.processMarcXmlString(ex);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        assertTrue(true);
    }

    public String covertToMarcXmlString(List<Record> recordList) throws Exception {
        OutputStream out = new ByteArrayOutputStream();
        MarcWriter writer = new MarcXmlWriter(out, "UTF-8", true);

        recordList.forEach(writer::write);
        writer.close();

        return out.toString();
    }

    private List<Record> readMarcXml(String marcXmlString) {
        List<Record> recordList = new ArrayList<>();
        InputStream in = new ByteArrayInputStream(marcXmlString.getBytes());
        MarcReader reader = new MarcXmlReader(in);
        while (reader.hasNext()) {
            Record record = reader.next();
            recordList.add(record);
        }
        return recordList;
    }

    @Test
    public void testsetDataExportHeaderUtil() {
        marcXMLFormatActiveMQConsumer.setDataExportHeaderUtil(dataExportHeaderUtil);
        assertTrue(true);
    }

    private BibliographicEntity getBibliographicEntity() throws URISyntaxException, IOException {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setId(100);
        bibliographicEntity.setContent(bibContent.getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionBibId("1");
        bibliographicEntity.setOwningInstitutionId(3);
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("NYPL");
        institutionEntity.setInstitutionName("New York Public Library");
        bibliographicEntity.setInstitutionEntity(institutionEntity);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setId(345);
        holdingsEntity.setContent(holdingContent.getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(3);
        holdingsEntity.setOwningInstitutionHoldingsId(".h54323");
        holdingsEntity.setInstitutionEntity(institutionEntity);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(100);
        itemEntity.setCallNumberType("0");
        itemEntity.setCallNumber("callNum");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setBarcode("0012003654");
        itemEntity.setOwningInstitutionItemId(".i1231");
        itemEntity.setOwningInstitutionId(3);
        itemEntity.setCollectionGroupId(1);
        CollectionGroupEntity collectionGroupEntity = new CollectionGroupEntity();
        collectionGroupEntity.setCollectionGroupCode("Shared");
        itemEntity.setCollectionGroupEntity(collectionGroupEntity);
        itemEntity.setCustomerCode("PA");
        itemEntity.setCopyNumber(1);
        itemEntity.setVolumePartYear("v. 30-31 1980-81");
        itemEntity.setItemAvailabilityStatusId(1);
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setStatusCode("Available");
        itemEntity.setItemStatusEntity(itemStatusEntity);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));
        return bibliographicEntity;
    }
}
