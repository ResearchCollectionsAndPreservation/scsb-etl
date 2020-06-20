package org.recap.util;

import org.junit.Test;
import org.recap.model.jaxb.BibRecord;
import org.recap.model.jaxb.JAXBHandler;
import org.recap.model.jaxb.marc.CollectionType;
import org.recap.model.jaxb.marc.ControlFieldType;
import org.recap.model.jaxb.marc.DataFieldType;
import org.recap.model.jaxb.marc.LeaderFieldType;
import org.recap.model.jaxb.marc.RecordType;
import org.recap.model.jaxb.marc.RecordTypeType;
import org.recap.model.jaxb.marc.SubfieldatafieldType;

import javax.xml.bind.JAXBException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by pvsubrah on 6/22/16.
 */
public class MarcUtilUT {

    @Test
    public void controlField001() throws Exception {
        BibRecord bibRecord = getBibRecord();

        assertNotNull(bibRecord);

        MarcUtil marcUtil = new MarcUtil();
        RecordType marcRecord = bibRecord.getBib().getContent().getCollection().getRecord().get(0);
        String controlFieldValue = marcUtil.getControlFieldValue(marcRecord, "001");
        assertNotNull(controlFieldValue);
    }


    @Test
    public void dataField245() throws Exception {
        MarcUtil marcUtil = new MarcUtil();
        String dataFieldValue = marcUtil.getDataFieldValue(getBibRecord().getBib().getContent().getCollection().getRecord().get(0), "245", null, null, "a");
        assertEquals(dataFieldValue, "al-Baḥrayn :");
    }

    @Test
    public void dataField035() throws Exception {
        MarcUtil marcUtil = new MarcUtil();
        List<String> dataFieldValues = marcUtil.getMultiDataFieldValues(getBibRecord().getBib().getContent().getCollection().getRecord().get(0), "035", null, null, "a");
        assertNotNull(dataFieldValues);
        assertEquals(dataFieldValues.size(), 2);
    }

    @Test
    public void getInd1() throws Exception {
        MarcUtil marcUtil = new MarcUtil();
        RecordType recordType = getBibRecord().getHoldings().get(0).getHolding().get(0).getContent().getCollection().getRecord().get(0);
        String ind1 = marcUtil.getInd1(recordType, "852", "h");
        assertEquals(ind1, "0");
    }

    @Test
    public void getControlFieldValue() throws Exception {
        MarcUtil marcUtil = new MarcUtil();
        RecordType recordType = getBibRecord().getBib().getContent().getCollection().getRecord().get(0);
        String controlFieldValue = marcUtil.getControlFieldValue(recordType, "001");
        assertEquals(controlFieldValue, "NYPG001000011-B");
    }

    private BibRecord getBibRecord() {
        JAXBHandler jaxbHandler = JAXBHandler.getInstance();

        String content = "<bibRecord>\n" +
                "    <bib>\n" +
                "      <owningInstitutionId>NYPL</owningInstitutionId>\n" +
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
                "        <owningInstitutionHoldingsId/>\n" +
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

        BibRecord bibRecord = null;
        try {
            bibRecord = (BibRecord) jaxbHandler.unmarshal(content, BibRecord.class);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return bibRecord;
    }

    @Test
    public void marcRecordsSettersAndGettersTest() throws Exception {
        CollectionType collectionType = new CollectionType();
        collectionType.setId("1");
        assertEquals(collectionType.getId(), "1");
        ControlFieldType controlFieldType = new ControlFieldType();
        controlFieldType.setId("1");
        controlFieldType.setTag("Test Tag");
        controlFieldType.setValue("Test Value");
        assertEquals(controlFieldType.getId(), "1");
        assertEquals(controlFieldType.getTag(), "Test Tag");
        assertEquals(controlFieldType.getValue(), "Test Value");
        DataFieldType dataFieldType = new DataFieldType();
        dataFieldType.setId("1");
        dataFieldType.setTag("Test Tag");
        dataFieldType.setInd1("001");
        dataFieldType.setInd2("002");
        assertEquals(dataFieldType.getId(), "1");
        assertEquals(dataFieldType.getTag(), "Test Tag");
        assertEquals(dataFieldType.getInd1(), "001");
        assertEquals(dataFieldType.getInd2(), "002");
        LeaderFieldType leaderFieldType = new LeaderFieldType();
        leaderFieldType.setId("1");
        leaderFieldType.setValue("Test Leader Field");
        assertEquals(leaderFieldType.getId(), "1");
        assertEquals(leaderFieldType.getValue(), "Test Leader Field");
        RecordType recordType = new RecordType();
        recordType.setId("1");
        recordType.setLeader(leaderFieldType);
        recordType.setType(RecordTypeType.fromValue("Bibliographic"));
        assertEquals(recordType.getId(), "1");
        assertEquals(recordType.getLeader(), leaderFieldType);
        assertEquals(recordType.getType().value(), RecordTypeType.BIBLIOGRAPHIC.value());
        assertNotNull(recordType.getControlfield());
        assertNotNull(recordType.getDatafield());
        SubfieldatafieldType subfieldatafieldType = new SubfieldatafieldType();
        subfieldatafieldType.setId("1");
        subfieldatafieldType.setValue("Test Value");
        subfieldatafieldType.setCode("Test code");
        assertEquals(subfieldatafieldType.getId(), "1");
        assertEquals(subfieldatafieldType.getValue(), "Test Value");
        assertEquals(subfieldatafieldType.getCode(), "Test code");
    }

}