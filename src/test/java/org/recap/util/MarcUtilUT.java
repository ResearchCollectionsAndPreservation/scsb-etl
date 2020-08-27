package org.recap.util;

import org.junit.Test;
import org.recap.model.jaxb.BibRecord;
import org.recap.model.jaxb.JAXBHandler;
import org.recap.model.jaxb.marc.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.*;

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
        assertNotEquals(dataFieldValue, "al-Baḥrayn :");
    }

    @Test
    public void dataField035() throws Exception {
        MarcUtil marcUtil = new MarcUtil();
        List<String> dataFieldValues = marcUtil.getMultiDataFieldValues(getBibRecord().getBib().getContent().getCollection().getRecord().get(0), "035", null, null, "a");
        assertNotNull(dataFieldValues);
        assertEquals(1,dataFieldValues.size());
    }

    @Test
    public void getInd1() throws Exception {
        MarcUtil marcUtil = new MarcUtil();
        RecordType recordType = getBibRecord().getHoldings().get(0).getHolding().get(0).getContent().getCollection().getRecord().get(0);
        String ind1 = marcUtil.getInd1(recordType, "852", "h");
        assertNotEquals(ind1, "0");
    }

    @Test
    public void getControlFieldValue() throws Exception {
        MarcUtil marcUtil = new MarcUtil();
        RecordType recordType = getBibRecord().getBib().getContent().getCollection().getRecord().get(0);
        String controlFieldValue = marcUtil.getControlFieldValue(recordType, "001");
        assertNotEquals(controlFieldValue, "NYPG001000011-B");
    }

    private BibRecord getBibRecord() {
        JAXBHandler jaxbHandler = JAXBHandler.getInstance();

        String content = "<bibRecord><bib><owningInstitutionId>NYPL</owningInstitutionId><owningInstitutionBibId>.b100062349</owningInstitutionBibId><content><collection xmlns=\"http://www.loc.gov/MARC21/slim\"><record><controlfield tag=\"001\">16660149</controlfield><controlfield tag=\"003\">OCoLC</controlfield><controlfield tag=\"005\">20160314023549.0</controlfield><controlfield tag=\"007\">hd afb---buca</controlfield><controlfield tag=\"007\">hd bfb---baaa</controlfield><controlfield tag=\"008\">850618s1981    nyua    a     000 0dspa d</controlfield><datafield ind1=\" \" ind2=\" \" tag=\"040\"><subfield code=\"a\">NYP</subfield><subfield code=\"b\">eng</subfield><subfield code=\"c\">NYP</subfield><subfield code=\"d\">OCLCQ</subfield><subfield code=\"d\">OCLCF</subfield><subfield code=\"d\">OCLCO</subfield><subfield code=\"d\">OCLCQ</subfield><subfield code=\"d\">NYP</subfield></datafield><datafield ind1=\" \" ind2=\" \" tag=\"049\"><subfield code=\"a\">NYPP</subfield></datafield><datafield ind1=\"1\" ind2=\" \" tag=\"100\"><subfield code=\"a\">Guzmán, Carlos.</subfield></datafield><datafield ind1=\"1\" ind2=\"0\" tag=\"245\"><subfield code=\"a\">Santería</subfield><subfield code=\"h\">[microform] :</subfield><subfield code=\"b\">la adivinación por medio de los [sic] los caracoles /</subfield><subfield code=\"c\">Carlos Guzman.</subfield></datafield><datafield ind1=\" \" ind2=\" \" tag=\"260\"><subfield code=\"a\">New York :</subfield><subfield code=\"b\">Latin Press Pub. Co.,</subfield><subfield code=\"c\">©1981.</subfield></datafield><datafield ind1=\" \" ind2=\" \" tag=\"300\"><subfield code=\"a\">52 pages :</subfield><subfield code=\"b\">illustrations ;</subfield><subfield code=\"c\">21 cm</subfield></datafield><datafield ind1=\" \" ind2=\" \" tag=\"336\"><subfield code=\"a\">text</subfield><subfield code=\"b\">txt</subfield><subfield code=\"2\">rdacontent</subfield></datafield><datafield ind1=\" \" ind2=\" \" tag=\"337\"><subfield code=\"a\">microform</subfield><subfield code=\"b\">h</subfield><subfield code=\"2\">rdamedia</subfield></datafield><datafield ind1=\" \" ind2=\" \" tag=\"338\"><subfield code=\"a\">microfilm reel</subfield><subfield code=\"b\">hd</subfield><subfield code=\"2\">rdacarrier</subfield></datafield><datafield ind1=\"0\" ind2=\" \" tag=\"490\"><subfield code=\"a\">Estudios afro-cubanos ;</subfield><subfield code=\"v\">v. 2</subfield></datafield><datafield ind1=\" \" ind2=\" \" tag=\"500\"><subfield code=\"a\">Cover title.</subfield></datafield><datafield ind1=\" \" ind2=\" \" tag=\"500\"><subfield code=\"a\">Includes brief biography of Rómulo Lachatanere, writer on Santería.</subfield></datafield><datafield ind1=\" \" ind2=\" \" tag=\"533\"><subfield code=\"a\">Microfilm.</subfield><subfield code=\"b\">New York:</subfield><subfield code=\"c\">New York Public Library,</subfield><subfield code=\"d\">1984.</subfield><subfield code=\"e\">1 microfilm reel; 35 mm.</subfield><subfield code=\"f\">(MN *ZZ-23118).</subfield></datafield><datafield ind1=\" \" ind2=\"0\" tag=\"650\"><subfield code=\"a\">Santeria.</subfield></datafield><datafield ind1=\"1\" ind2=\"0\" tag=\"600\"><subfield code=\"a\">Lachatañeré, Rómulo</subfield></datafield><datafield ind1=\"1\" ind2=\"7\" tag=\"600\"><subfield code=\"a\">Lachatañeré, R.</subfield><subfield code=\"q\">(Rómulo)</subfield><subfield code=\"2\">fast</subfield><subfield code=\"0\">(OCoLC)fst00227801</subfield></datafield><datafield ind1=\" \" ind2=\"7\" tag=\"650\"><subfield code=\"a\">Santeria.</subfield><subfield code=\"2\">fast</subfield><subfield code=\"0\">(OCoLC)fst01105327</subfield></datafield><datafield ind1=\" \" ind2=\" \" tag=\"907\"><subfield code=\"a\">.b100062349</subfield><subfield code=\"c\">m</subfield><subfield code=\"d\">h</subfield><subfield code=\"e\">-</subfield><subfield code=\"f\">spa</subfield><subfield code=\"g\">nyu</subfield><subfield code=\"h\">0</subfield><subfield code=\"i\">2</subfield></datafield><datafield ind1=\" \" ind2=\" \" tag=\"952\"><subfield code=\"h\">Sc Micro R-4131 no. 5</subfield></datafield><datafield ind1=\" \" ind2=\" \" tag=\"035\"><subfield code=\"a\">(OCoLC)16660149</subfield></datafield><leader>01593cam a2200421Li 4500</leader></record></collection></content></bib><holdings><holding><owningInstitutionHoldingsId/><content><collection xmlns=\"http://www.loc.gov/MARC21/slim\"><record><datafield ind1=\"8\" ind2=\" \" tag=\"852\"><subfield code=\"b\">rcxx2</subfield><subfield code=\"h\">*ZZ-23118</subfield></datafield><datafield ind1=\" \" ind2=\" \" tag=\"866\"><subfield code=\"a\"/></datafield></record></collection></content><items><content><collection xmlns=\"http://www.loc.gov/MARC21/slim\"><record><datafield ind1=\" \" ind2=\" \" tag=\"876\"><subfield code=\"p\">33433002594392</subfield><subfield code=\"h\">In Library Use</subfield><subfield code=\"a\">.i281312722</subfield><subfield code=\"j\">Not Available</subfield><subfield code=\"t\">1</subfield></datafield><datafield ind1=\" \" ind2=\" \" tag=\"900\"><subfield code=\"a\">Private</subfield><subfield code=\"b\">NX</subfield></datafield></record></collection></content></items></holding></holdings></bibRecord>";

        BibRecord bibRecord = null;
        try {
            JAXBContext context = JAXBContext.newInstance(BibRecord.class);
            XMLInputFactory xif = XMLInputFactory.newFactory();
            xif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
            InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
            XMLStreamReader xsr = xif.createXMLStreamReader(stream);
            Unmarshaller um = context.createUnmarshaller();
            bibRecord = (BibRecord) um.unmarshal(xsr);
        } catch (JAXBException | XMLStreamException e) {
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