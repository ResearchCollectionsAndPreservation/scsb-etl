package org.recap.model.jaxb;

import org.junit.Test;
import org.recap.model.jaxb.marc.CollectionType;
import org.recap.model.jaxb.marc.ContentType;
import org.recap.model.jaxb.marc.DataFieldType;
import org.recap.model.jaxb.marc.RecordType;
import org.recap.model.jaxb.marc.SubfieldatafieldType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by pvsubrah on 6/21/16.
 */
public class JAXBHandlerUT {

    @Test
    public void marshal() throws Exception {
        JAXBHandler jaxbHandler = JAXBHandler.getInstance();
        jaxbHandler.setMarshallerMap(new HashMap<>());
        BibRecord bibRecord = new BibRecord();
        Bib bib = new Bib();
        bib.setOwningInstitutionId("101");
        bib.setOwningInstitutionBibId("111");
        ContentType content = new ContentType();
        CollectionType collection = new CollectionType();
        RecordType recordType = new RecordType();
        DataFieldType dataField = new DataFieldType();
        dataField.setTag("100");
        SubfieldatafieldType subField = new SubfieldatafieldType();
        subField.setCode("a");
        subField.setValue("Title");
        dataField.getSubfield().add(subField);
        recordType.getDatafield().add(dataField);
        collection.getRecord().add(recordType);
        content.setCollection(collection);
        bib.setContent(content);
        bibRecord.setBib(bib);
        Holding holding1 = new Holding();
        holding1.setOwningInstitutionHoldingsId("h-101");
        holding1.setContent(new ContentType());
        Items items = new Items();
        items.setContent(new ContentType());
        holding1.setItems(Arrays.asList(items));
        Holding holding2 = new Holding();
        holding2.setOwningInstitutionHoldingsId("h-102");
        Holdings holdings = new Holdings();
        holdings.setHolding(Arrays.asList(holding1, holding2));
        holding2.setItems(Arrays.asList(items));
        bibRecord.setHoldings(Arrays.asList(holdings));
        String marshalledObject = jaxbHandler.marshal(bibRecord);
        assertNotNull(marshalledObject);
        System.out.println(marshalledObject);
    }

    @Test
    public void unmarshal() throws Exception {
        JAXBHandler jaxbHandler = JAXBHandler.getInstance();
        jaxbHandler.setUnmarshallerMap(new HashMap<>());

        String content = "<bibRecord>\n" +
                "    <bib>\n" +
                "      <owningInstitutionId>NYPL</owningInstitutionId>\n" +
                "      <owningInstitutionBibId>.b153286131</owningInstitutionBibId>\n" +
                "      <content>\n" +
                "        <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
                "          <record>\n" +
                "            <controlfield tag=\"001\">NYPG001000011-B</controlfield>\n" +
                "            <controlfield tag=\"005\">20001116192418.8</controlfield>\n" +
                "            <controlfield tag=\"008\">841106s1976    le       b    000 0 arax </controlfield>\n" +
                "            <datafield ind1=\" \" ind2=\" \" tag=\"010\">\n" +
                "              <subfield code=\"a\">79971032</subfield>\n" +
                "            </datafield>\n" +
                "            <datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
                "              <subfield code=\"a\">NNSZ00100011</subfield>\n" +
                "            </datafield>\n" +
                "            <datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
                "              <subfield code=\"a\">(WaOLN)nyp0200023</subfield>\n" +
                "            </datafield>\n" +
                "            <datafield ind1=\" \" ind2=\" \" tag=\"040\">\n" +
                "              <subfield code=\"c\">NN</subfield>\n" +
                "              <subfield code=\"d\">NN</subfield>\n" +
                "              <subfield code=\"d\">WaOLN</subfield>\n" +
                "            </datafield>\n" +
                "            <datafield ind1=\" \" ind2=\" \" tag=\"043\">\n" +
                "              <subfield code=\"a\">a-ba---</subfield>\n" +
                "            </datafield>\n" +
                "            <datafield ind1=\"0\" ind2=\"0\" tag=\"050\">\n" +
                "              <subfield code=\"a\">DS247.B28</subfield>\n" +
                "              <subfield code=\"b\">R85</subfield>\n" +
                "            </datafield>\n" +
                "            <datafield ind1=\"1\" ind2=\" \" tag=\"100\">\n" +
                "              <subfield code=\"a\">Rumayḥī, Muḥammad Ghānim.</subfield>\n" +
                "            </datafield>\n" +
                "            <datafield ind1=\"1\" ind2=\"3\" tag=\"245\">\n" +
                "              <subfield code=\"a\">al-Baḥrayn :</subfield>\n" +
                "              <subfield code=\"b\">mushkilāt al-taghyīr al-siyāsī wa-al-ijtimāʻī /</subfield>\n" +
                "              <subfield code=\"c\">Muḥammad al-Rumayḥī.</subfield>\n" +
                "            </datafield>\n" +
                "            <datafield ind1=\" \" ind2=\" \" tag=\"250\">\n" +
                "              <subfield code=\"a\">al-Ṭabʻah 1.</subfield>\n" +
                "            </datafield>\n" +
                "            <datafield ind1=\" \" ind2=\" \" tag=\"260\">\n" +
                "              <subfield code=\"a\">[Bayrūt] :</subfield>\n" +
                "              <subfield code=\"b\">Dār Ibn Khaldūn,</subfield>\n" +
                "              <subfield code=\"c\">1976.</subfield>\n" +
                "            </datafield>\n" +
                "            <datafield ind1=\" \" ind2=\" \" tag=\"300\">\n" +
                "              <subfield code=\"a\">264 p. ;</subfield>\n" +
                "              <subfield code=\"c\">24 cm.</subfield>\n" +
                "            </datafield>\n" +
                "            <datafield ind1=\" \" ind2=\" \" tag=\"504\">\n" +
                "              <subfield code=\"a\">Includes bibliographies.</subfield>\n" +
                "            </datafield>\n" +
                "            <datafield ind1=\" \" ind2=\" \" tag=\"546\">\n" +
                "              <subfield code=\"a\">In Arabic.</subfield>\n" +
                "            </datafield>\n" +
                "            <datafield ind1=\" \" ind2=\"0\" tag=\"651\">\n" +
                "              <subfield code=\"a\">Bahrain</subfield>\n" +
                "              <subfield code=\"x\">History</subfield>\n" +
                "              <subfield code=\"y\">20th century.</subfield>\n" +
                "            </datafield>\n" +
                "            <datafield ind1=\" \" ind2=\"0\" tag=\"651\">\n" +
                "              <subfield code=\"a\">Bahrain</subfield>\n" +
                "              <subfield code=\"x\">Economic conditions.</subfield>\n" +
                "            </datafield>\n" +
                "            <datafield ind1=\" \" ind2=\"0\" tag=\"651\">\n" +
                "              <subfield code=\"a\">Bahrain</subfield>\n" +
                "              <subfield code=\"x\">Social conditions.</subfield>\n" +
                "            </datafield>\n" +
                "            <datafield ind1=\" \" ind2=\" \" tag=\"907\">\n" +
                "              <subfield code=\"a\">.b100000241</subfield>\n" +
                "              <subfield code=\"c\">m</subfield>\n" +
                "              <subfield code=\"d\">a</subfield>\n" +
                "              <subfield code=\"e\">-</subfield>\n" +
                "              <subfield code=\"f\">ara</subfield>\n" +
                "              <subfield code=\"g\">le </subfield>\n" +
                "              <subfield code=\"h\">3</subfield>\n" +
                "              <subfield code=\"i\">1</subfield>\n" +
                "            </datafield>\n" +
                "            <datafield ind1=\" \" ind2=\" \" tag=\"952\">\n" +
                "              <subfield code=\"h\">*OFK 84-1944</subfield>\n" +
                "            </datafield>\n" +
                "          </record>\n" +
                "        </collection>\n" +
                "      </content>\n" +
                "    </bib>\n" +
                "    <holdings>\n" +
                "      <holding>\n" +
                "        <owningInstitutionHoldingsId>123456</owningInstitutionHoldingsId>\n" +
                "        <content>\n" +
                "          <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
                "            <record>\n" +
                "              <datafield ind1=\"0\" ind2=\"0\" tag=\"852\">\n" +
                "                <subfield code=\"b\">rc2ma</subfield>\n" +
                "                <subfield code=\"h\">*OFK 84-1944</subfield>\n" +
                "              </datafield>\n" +
                "              <datafield ind1=\"0\" ind2=\"0\" tag=\"866\">\n" +
                "                <subfield code=\"a\"/>\n" +
                "              </datafield>\n" +
                "            </record>\n" +
                "          </collection>\n" +
                "        </content>\n" +
                "        <items>\n" +
                "          <content>\n" +
                "            <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
                "              <record>\n" +
                "                <datafield ind1=\"0\" ind2=\"0\" tag=\"876\">\n" +
                "                  <subfield code=\"h\">Use on site</subfield>\n" +
                "                  <subfield code=\"a\">.i100000058</subfield>\n" +
                "                  <subfield code=\"j\">-</subfield>\n" +
                "                  <subfield code=\"p\">33433005548676</subfield>\n" +
                "                  <subfield code=\"t\">1</subfield>\n" +
                "                  <subfield code=\"3\"/>\n" +
                "                </datafield>\n" +
                "                <datafield ind1=\"0\" ind2=\"0\" tag=\"900\">\n" +
                "                  <subfield code=\"a\">Shared</subfield>\n" +
                "                  <subfield code=\"b\">NA</subfield>\n" +
                "                </datafield>\n" +
                "              </record>\n" +
                "            </collection>\n" +
                "          </content>\n" +
                "        </items>\n" +
                "      </holding>\n" +
                "    </holdings>\n" +
                "  </bibRecord>";


        BibRecord bibRecord = (BibRecord) jaxbHandler.unmarshal(content, BibRecord.class);

        assertNotNull(bibRecord);
        Bib bib = bibRecord.getBib();
        assertNotNull(bib);
        assertEquals(bib.getOwningInstitutionId(), "NYPL");
        assertEquals(bib.getOwningInstitutionBibId(), ".b153286131");
        assertNotNull(bib.getContent());

        assertNotNull(bibRecord.getHoldings());
        assertTrue(bibRecord.getHoldings().size() > 0);
        List<Holding> holdings = bibRecord.getHoldings().get(0).getHolding();
        assertNotNull(holdings);
        Holding holding = holdings.get(0);
        assertNotNull(holding);
        assertEquals(holding.getOwningInstitutionHoldingsId(), "123456");
        assertNotNull(holding.getContent());

        assertNotNull(holding.getItems());
        Items items = holding.getItems().get(0);
        assertNotNull(items);
        assertNotNull(items.getContent());

    }

}