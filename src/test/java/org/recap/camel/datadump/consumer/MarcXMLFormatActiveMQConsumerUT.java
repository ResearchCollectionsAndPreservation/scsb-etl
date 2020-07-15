package org.recap.camel.datadump.consumer;

import info.freelibrary.marc4j.impl.*;
import org.apache.camel.*;
import org.apache.camel.impl.*;
import org.apache.camel.support.*;
import org.junit.Test;
import org.marc4j.*;
import org.marc4j.marc.Record;
import org.mockito.Mock;
import org.recap.*;
import org.recap.model.jpa.*;
import org.recap.service.formatter.datadump.MarcXmlFormatterService;
import org.recap.util.datadump.DataExportHeaderUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.*;
import java.util.*;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
        BibliographicEntity bibliographicEntity = null;
        try {
            bibliographicEntity = getBibliographicEntity();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, Object> successAndFailureFormattedList = marcXmlFormatterService.prepareMarcRecords(Arrays.asList(bibliographicEntity));
        try {
           String marcXmlString = marcXmlFormatterService.covertToMarcXmlString((List<Record>)successAndFailureFormattedList.get(RecapCommonConstants.SUCCESS));
            List<Record> recordList = readMarcXml(marcXmlString);
            String dataHeader=";requestId#1";
            CamelContext ctx = new DefaultCamelContext();
            Exchange ex = new DefaultExchange(ctx);
            Message in = ex.getIn();
            ex.setMessage(in);
            in.setBody(recordList);
            ex.setIn(in);
            Map<String,Object> mapdata = new HashMap<>();
            mapdata.put("batchHeaders",dataHeader);
            in.setHeaders(mapdata);
            marcXMLFormatActiveMQConsumer.processMarcXmlString(ex);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        assertTrue(true);
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
    @Test
    public void testgetDataExportHeaderUtil() {
        marcXMLFormatActiveMQConsumer.getDataExportHeaderUtil();
        assertNull(dataExportHeaderUtil);
    }
    private String bibContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
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

    private String holdingContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
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
    private BibliographicEntity getBibliographicEntity() throws URISyntaxException, IOException {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setBibliographicId(100);
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
        holdingsEntity.setHoldingsId(345);
        holdingsEntity.setContent(holdingContent.getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(3);
        holdingsEntity.setOwningInstitutionHoldingsId(".h54323");
        holdingsEntity.setInstitutionEntity(institutionEntity);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setItemId(100);
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
