package org.recap.model.etl;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.TestUtil;
import org.recap.model.csv.FailureReportSCSBCSVRecord;
import org.recap.model.jaxb.Bib;
import org.recap.model.jaxb.BibRecord;
import org.recap.model.jaxb.Holding;
import org.recap.model.jaxb.Holdings;
import org.recap.model.jaxb.Items;
import org.recap.model.jaxb.marc.CollectionType;
import org.recap.model.jaxb.marc.ContentType;
import org.recap.model.jaxb.marc.RecordType;
import org.recap.model.jpa.XmlRecordEntity;
import org.recap.repository.ImsLocationDetailsRepository;
import org.recap.util.DBReportUtil;
import org.recap.util.MarcUtil;
import org.springframework.test.util.ReflectionTestUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by chenchulakshmig on 1/7/16.
 */
public class BibPersisterCallableUT extends BaseTestCaseUT {

    @Mock
    DBReportUtil dbReportUtil;

    @Mock
    private Map<String, Integer> institutionMap;

    @Mock
    private Map<String, Integer> itemStatusMap;

    @Mock
    private Map<String, Integer> collectionGroupMap;

    @Mock
    Map<String, Integer>  imsLocationCodeMap;

    @Mock
    BibRecord bibRecord;

    @Mock
    Bib bib;

    @Mock
    ContentType contentType;

    @Mock
    CollectionType collection;

    @Mock
    RecordType recordType;

    @Mock
    Holdings holdings;

    @Mock
    Holding holding;

    @Mock
    Items items;

    @Mock
    MarcUtil marcUtil;

    @Mock
    ImsLocationDetailsRepository imsLocationDetailsRepository;

    @Test
    public void newInstance() {
        BibRecord bibRecord = new BibRecord();
        BibPersisterCallable bibPersisterCallable = new BibPersisterCallable();
        bibPersisterCallable.setItemStatusMap(itemStatusMap);
        bibPersisterCallable.setInstitutionEntitiesMap(institutionMap);
        bibPersisterCallable.setCollectionGroupMap(collectionGroupMap);
        bibPersisterCallable.setImsLocationCodeMap(imsLocationCodeMap);
        bibPersisterCallable.setBibRecord(bibRecord);
        bibPersisterCallable.setInstitutionName("PUL");
        assertEquals("PUL", bibPersisterCallable.getInstitutionName());
    }

    @Test
    public void checkNullConstraints() throws Exception {
        Mockito.when(institutionMap.get("NYPL")).thenReturn(3);
        Mockito.when(itemStatusMap.get("Available")).thenReturn(1);
        Mockito.when(collectionGroupMap.get("Open")).thenReturn(2);
        Mockito.when(imsLocationCodeMap.get(Mockito.anyString())).thenReturn(1);

        List<FailureReportSCSBCSVRecord> failureReportSCSBCSVRecords = new ArrayList<>();

        Map<String, Integer> institution = new HashMap<>();
        institution.put("NYPL", 3);
        Mockito.when(institutionMap.entrySet()).thenReturn(institution.entrySet());

        Map<String, Integer> collection = new HashMap<>();
        collection.put("Open", 2);
        Mockito.when(collectionGroupMap.entrySet()).thenReturn(collection.entrySet());

        XmlRecordEntity xmlRecordEntity = new XmlRecordEntity();
        xmlRecordEntity.setXmlFileName("BibWithoutItemBarcode.xml");

        URL resource = getClass().getResource("BibWithoutItemBarcode.xml");
        assertNotNull(resource);
        File file = new File(resource.toURI());
        assertNotNull(file);
        assertTrue(file.exists());
        BibRecord bibRecord = null;
        JAXBContext context = JAXBContext.newInstance(BibRecord.class);
        XMLInputFactory xif = XMLInputFactory.newFactory();
        xif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
        InputStream stream = new ByteArrayInputStream(FileUtils.readFileToString(file, "UTF-8").getBytes());
        XMLStreamReader xsr = xif.createXMLStreamReader(stream);
        Unmarshaller um = context.createUnmarshaller();
        bibRecord = (BibRecord) um.unmarshal(xsr);
        assertNotNull(bibRecord);

        BibPersisterCallable bibPersisterCallable = new BibPersisterCallable();
        bibPersisterCallable.setItemStatusMap(itemStatusMap);
        bibPersisterCallable.setInstitutionEntitiesMap(institutionMap);
        bibPersisterCallable.setCollectionGroupMap(collectionGroupMap);
        bibPersisterCallable.setXmlRecordEntity(xmlRecordEntity);
        bibPersisterCallable.setBibRecord(bibRecord);
        bibPersisterCallable.setDbReportUtil(dbReportUtil);
        assertNotNull(bibPersisterCallable.getItemStatusMap());
        assertNotNull(bibPersisterCallable.getInstitutionEntitiesMap());
        assertNotNull(bibPersisterCallable.getCollectionGroupMap());
        assertNotNull(bibPersisterCallable.getXmlRecordEntity());
        assertNotNull(bibPersisterCallable.getBibRecord());
        ReflectionTestUtils.setField(bibPersisterCallable,"marcUtil",marcUtil);
        Mockito.when(marcUtil.getControlFieldValue(recordType, "001")).thenReturn("NYPG001000011-B");
        ReflectionTestUtils.setField(bibPersisterCallable,"imsLocationDetailsRepository",imsLocationDetailsRepository);
        ReflectionTestUtils.setField(bibPersisterCallable,"imsLocationCodeMap",imsLocationCodeMap);
        Mockito.when(imsLocationDetailsRepository.findByImsLocationCode(Mockito.anyString())).thenReturn(TestUtil.getImsLocationEntity(1,"RECAP","RECAP_LAS"));
        Map<String, Object> map = (Map<String, Object>) bibPersisterCallable.call();
        if (map != null) {
            Object object = map.get("reportEntities");
            if (object != null) {
                failureReportSCSBCSVRecords.addAll((List<FailureReportSCSBCSVRecord>) object);
            }
        }
        assertTrue(failureReportSCSBCSVRecords.size() == 2);
    }

    @Ignore
    @Test
    public void checkNullConstraintsError() throws Exception {
        Mockito.when(institutionMap.get("NYPL")).thenReturn(3);
        Mockito.when(itemStatusMap.get("Available")).thenReturn(1);
        Mockito.when(collectionGroupMap.get("Open")).thenReturn(2);
        Mockito.when(bibRecord.getBib()).thenReturn(bib);
        Mockito.when(bib.getOwningInstitutionId()).thenReturn("1");
        Mockito.when(bib.getContent()).thenReturn(contentType);
        Mockito.when(contentType.getCollection()).thenReturn(collection);
        List<RecordType> recordTypes=new ArrayList<>();
        recordTypes.add(recordType);
        Mockito.when(collection.getRecord()).thenReturn(recordTypes);
        List<Holdings> holdingsList=new ArrayList<>();
        holdingsList.add(holdings);
        Mockito.when(bibRecord.getHoldings()).thenReturn(holdingsList);
        List<Holding> holdingList=new ArrayList<>();
        holdingList.add(holding);
        Mockito.when(holdings.getHolding()).thenReturn(holdingList);
        Mockito.when(holding.getContent()).thenReturn(contentType);
        List<Items> itemsList=new ArrayList<>();
        itemsList.add(items);
        Mockito.when(holding.getItems()).thenReturn(itemsList);
        Mockito.when(items.getContent()).thenReturn(contentType);
        List<FailureReportSCSBCSVRecord> failureReportSCSBCSVRecords = new ArrayList<>();

        Map<String, Integer> institution = new HashMap<>();
        institution.put("NYPL", 3);
        Mockito.when(institutionMap.entrySet()).thenReturn(institution.entrySet());

        Map<String, Integer> collection = new HashMap<>();
        collection.put("Open", 2);
        Mockito.when(collectionGroupMap.entrySet()).thenReturn(collection.entrySet());

        XmlRecordEntity xmlRecordEntity = new XmlRecordEntity();
        xmlRecordEntity.setXmlFileName("BibWithoutItemBarcode.xml");

        URL resource = getClass().getResource("BibWithoutItemBarcode.xml");
        assertNotNull(resource);
        File file = new File(resource.toURI());
        assertNotNull(file);
        assertTrue(file.exists());
        BibPersisterCallable bibPersisterCallable = new BibPersisterCallable();
        bibPersisterCallable.setItemStatusMap(itemStatusMap);
        bibPersisterCallable.setInstitutionEntitiesMap(institutionMap);
        bibPersisterCallable.setCollectionGroupMap(collectionGroupMap);
        bibPersisterCallable.setXmlRecordEntity(xmlRecordEntity);
        bibPersisterCallable.setBibRecord(bibRecord);
        bibPersisterCallable.setDbReportUtil(dbReportUtil);
        ReflectionTestUtils.setField(bibPersisterCallable,"marcUtil",marcUtil);
        Mockito.when(marcUtil.getDataFieldValue(recordType, "900", null, null, "a")).thenReturn("Open");
        Mockito.when(marcUtil.getDataFieldValue(recordType, "876", null, null, "p")).thenReturn("123456");
        Mockito.when(marcUtil.getDataFieldValue(recordType, "876", null, null, "l")).thenReturn("RECAP");
        Mockito.when(marcUtil.getDataFieldValue(recordType, "876", null, null, "t")).thenReturn("4745");
        Mockito.when(marcUtil.getDataFieldValue(recordType, "876", null, null, "a")).thenReturn("9");
        Mockito.when(marcUtil.getDataFieldValue(recordType, "876", null, null, "h")).thenReturn("In Library Use");
        Mockito.when(marcUtil.getDataFieldValue(recordType, "900", null, null, "b")).thenReturn("PA");
        Mockito.when(collectionGroupMap.containsKey("Open")).thenReturn(true);
        assertNotNull(bibPersisterCallable.getItemStatusMap());
        assertNotNull(bibPersisterCallable.getInstitutionEntitiesMap());
        assertNotNull(bibPersisterCallable.getCollectionGroupMap());
        assertNotNull(bibPersisterCallable.getXmlRecordEntity());
        assertNotNull(bibPersisterCallable.getBibRecord());
        ReflectionTestUtils.setField(bibPersisterCallable,"imsLocationDetailsRepository",imsLocationDetailsRepository);
        ReflectionTestUtils.setField(bibPersisterCallable,"imsLocationCodeMap",imsLocationCodeMap);
        Mockito.when(imsLocationDetailsRepository.findByImsLocationCode(Mockito.anyString())).thenReturn(TestUtil.getImsLocationEntity(1,"RECAP","RECAP_LAS"));
        if(bibPersisterCallable !=null && bibPersisterCallable.call() != null) {
            Map<String, Object> map = (Map<String, Object>) bibPersisterCallable.call();
            if (map != null) {
                Object object = map.get("reportEntities");
                if (object != null) {
                    failureReportSCSBCSVRecords.addAll((List<FailureReportSCSBCSVRecord>) object);
                }
            }
        }
    }

    @Test
    public void processAndValidateHoldingsEntity() throws Exception {
        Mockito.when(institutionMap.get("NYPL")).thenReturn(3);
        Mockito.when(itemStatusMap.get("Available")).thenReturn(1);
        Mockito.when(collectionGroupMap.get("Open")).thenReturn(2);
        XmlRecordEntity xmlRecordEntity = new XmlRecordEntity();
        xmlRecordEntity.setXmlFileName("BibRecord.xml");
        URL resource = getClass().getResource("BibRecord.xml");
        assertNotNull(resource);
        File file = new File(resource.toURI());
        assertNotNull(file);
        assertTrue(file.exists());
        BibRecord bibRecord = null;
        JAXBContext context = JAXBContext.newInstance(BibRecord.class);
        XMLInputFactory xif = XMLInputFactory.newFactory();
        xif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
        InputStream stream = new ByteArrayInputStream(FileUtils.readFileToString(file, "UTF-8").getBytes());
        XMLStreamReader xsr = xif.createXMLStreamReader(stream);
        Unmarshaller um = context.createUnmarshaller();
        bibRecord = (BibRecord) um.unmarshal(xsr);
        assertNotNull(bibRecord);

        BibPersisterCallable bibPersisterCallable = new BibPersisterCallable();
        bibPersisterCallable.setItemStatusMap(itemStatusMap);
        bibPersisterCallable.setInstitutionEntitiesMap(institutionMap);
        bibPersisterCallable.setCollectionGroupMap(collectionGroupMap);
        bibPersisterCallable.setXmlRecordEntity(xmlRecordEntity);
        bibPersisterCallable.setBibRecord(bibRecord);
        bibPersisterCallable.setDbReportUtil(dbReportUtil);
        assertNotNull(bibPersisterCallable.getItemStatusMap());
        assertNotNull(bibPersisterCallable.getInstitutionEntitiesMap());
        assertNotNull(bibPersisterCallable.getCollectionGroupMap());
        assertNotNull(bibPersisterCallable.getXmlRecordEntity());
        assertNotNull(bibPersisterCallable.getBibRecord());
        ReflectionTestUtils.setField(bibPersisterCallable,"marcUtil",marcUtil);
        ReflectionTestUtils.setField(bibPersisterCallable,"imsLocationDetailsRepository",imsLocationDetailsRepository);
        ReflectionTestUtils.setField(bibPersisterCallable,"imsLocationCodeMap",imsLocationCodeMap);
        Mockito.when(imsLocationDetailsRepository.findByImsLocationCode(Mockito.anyString())).thenReturn(TestUtil.getImsLocationEntity(1,"RECAP","RECAP_LAS"));

        Map<String, Object> map = (Map<String, Object>) bibPersisterCallable.call();
        assertNotNull(map); }
}
