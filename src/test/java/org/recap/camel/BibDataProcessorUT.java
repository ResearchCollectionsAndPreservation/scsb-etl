package org.recap.camel;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.model.etl.BibPersisterCallable;
import org.recap.model.jaxb.BibRecord;

import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.XmlRecordEntity;
import org.recap.model.jparw.ReportEntity;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.repository.ItemDetailsRepository;
import org.recap.repositoryrw.ReportDetailRepository;
import org.recap.util.DBReportUtil;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.PersistenceException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * Created by angelind on 26/7/16.
 */


public class BibDataProcessorUT extends BaseTestCaseUT {

    @InjectMocks
    BibDataProcessor bibDataProcessor;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    ReportDetailRepository reportDetailRepository;

    @Mock
    private Map<String, Integer> institutionMap;

    @Mock
    private Map<String, Integer> collectionGroupMap;

    @Mock
    private Map itemStatusMap;

    @Mock
    private ProducerTemplate producer;

    @Mock
    private DBReportUtil dbReportUtil;

    @Mock
    EtlDataLoadDAOService etlDataLoadDAOService;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    NullPointerException nullPointerException;

    @Mock
    NullPointerException nullPointerException1;

    @Mock
    NullPointerException nullPointerException2;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    String content = "<collection>\n" +
            "        <record>\n" +
            "        <controlfield tag=\"001\">47764496</controlfield>\n" +
            "        <controlfield tag=\"003\">OCoLC</controlfield>\n" +
            "        <controlfield tag=\"005\">20021018083242.7</controlfield>\n" +
            "        <controlfield tag=\"008\">010604s2000 it a bde 000 0cita</controlfield>\n" +
            "        <datafield ind1=\"0\" ind2=\"0\" tag=\"245\">\n" +
            "        <subfield code=\"a\">Dizionario biografico enciclopedico di un secolo del calcio italiano /\n" +
            "        </subfield>\n" +
            "        <subfield code=\"c\">a cura di Marco Sappino.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"1\" ind2=\"4\" tag=\"246\">\n" +
            "        <subfield code=\"a\">Dizionario del calcio italiano</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"260\">\n" +
            "        <subfield code=\"a\">Milano :</subfield>\n" +
            "        <subfield code=\"b\">Baldini &amp; Castoldi,</subfield>\n" +
            "        <subfield code=\"c\">c2000.</subfield>\n" +
            "        </datafield>\n" +
            "        <leader>01184nam a22003494a 4500</leader>\n" +
            "        </record>\n" +
            "        </collection>";

    @Test
    public void persistDataToDB() throws Exception {
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        ETLExchange etlExchange = new ETLExchange();
        etlExchange.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        etlExchange.setInstitutionEntityMap(etlExchange.getInstitutionEntityMap() == null ? new HashMap() : etlExchange.getInstitutionEntityMap());
        etlExchange.setCollectionGroupMap(etlExchange.getCollectionGroupMap() == null ? new HashMap() : etlExchange.getCollectionGroupMap());
        try {
            bibDataProcessor.processETLExchagneAndPersistToDB(etlExchange);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void persistDataToDBPersistenceException() throws Exception {
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        ETLExchange etlExchange = new ETLExchange();
        etlExchange.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        etlExchange.setInstitutionEntityMap(etlExchange.getInstitutionEntityMap() == null ? new HashMap() : etlExchange.getInstitutionEntityMap());
        etlExchange.setCollectionGroupMap(etlExchange.getCollectionGroupMap() == null ? new HashMap() : etlExchange.getCollectionGroupMap());
        Mockito.doThrow(PersistenceException.class).when(etlDataLoadDAOService).saveBibliographicEntityList(Mockito.anyList());
        Mockito.when(itemDetailsRepository.findByBarcode(Mockito.anyString())).thenReturn(bibliographicEntity.getItemEntities());
        try {
            bibDataProcessor.processETLExchagneAndPersistToDB(etlExchange);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void persistDataToDBPersistenceExceptionDuplicate() throws Exception {
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        ETLExchange etlExchange = new ETLExchange();
        etlExchange.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        etlExchange.setInstitutionEntityMap(etlExchange.getInstitutionEntityMap() == null ? new HashMap() : etlExchange.getInstitutionEntityMap());
        etlExchange.setCollectionGroupMap(etlExchange.getCollectionGroupMap() == null ? new HashMap() : etlExchange.getCollectionGroupMap());
        Mockito.doThrow(PersistenceException.class).when(etlDataLoadDAOService).saveBibliographicEntityList(Mockito.anyList());
        Mockito.when(itemDetailsRepository.findByBarcode(Mockito.anyString())).thenReturn(Arrays.asList());
        try {
            bibDataProcessor.processETLExchagneAndPersistToDB(etlExchange);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void persistDataToDBException() throws Exception {
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        ETLExchange etlExchange = new ETLExchange();
        etlExchange.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        etlExchange.setInstitutionEntityMap(etlExchange.getInstitutionEntityMap() == null ? new HashMap() : etlExchange.getInstitutionEntityMap());
        etlExchange.setCollectionGroupMap(etlExchange.getCollectionGroupMap() == null ? new HashMap() : etlExchange.getCollectionGroupMap());
        Mockito.doThrow(NullPointerException.class).when(etlDataLoadDAOService).saveBibliographicEntityList(Mockito.anyList());
        Mockito.when(itemDetailsRepository.findByBarcode(Mockito.anyString())).thenReturn(bibliographicEntity.getItemEntities());
        try {
            bibDataProcessor.processETLExchagneAndPersistToDB(etlExchange);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void persistDataToDBExceptionEx() throws Exception {
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        ETLExchange etlExchange = new ETLExchange();
        etlExchange.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        etlExchange.setInstitutionEntityMap(etlExchange.getInstitutionEntityMap() == null ? new HashMap() : etlExchange.getInstitutionEntityMap());
        etlExchange.setCollectionGroupMap(etlExchange.getCollectionGroupMap() == null ? new HashMap() : etlExchange.getCollectionGroupMap());
        Mockito.doThrow(NullPointerException.class).when(etlDataLoadDAOService).saveBibliographicEntityList(Mockito.anyList());
        Mockito.doThrow(NullPointerException.class).doNothing().when(etlDataLoadDAOService).saveBibliographicEntity(Mockito.any());
        Mockito.when(itemDetailsRepository.findByBarcode(Mockito.anyString())).thenReturn(bibliographicEntity.getItemEntities());
        try {
            bibDataProcessor.processETLExchagneAndPersistToDB(etlExchange);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void persistDataToDBExceptionItemEx() throws Exception {
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        ETLExchange etlExchange = new ETLExchange();
        etlExchange.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        etlExchange.setInstitutionEntityMap(etlExchange.getInstitutionEntityMap() == null ? new HashMap() : etlExchange.getInstitutionEntityMap());
        etlExchange.setCollectionGroupMap(etlExchange.getCollectionGroupMap() == null ? new HashMap() : etlExchange.getCollectionGroupMap());
        Mockito.doThrow(NullPointerException.class).when(etlDataLoadDAOService).saveBibliographicEntityList(Mockito.anyList());
        Mockito.doThrow(NullPointerException.class).doNothing().when(etlDataLoadDAOService).saveBibliographicEntity(Mockito.any());
        Mockito.when(itemDetailsRepository.findByBarcode(Mockito.anyString())).thenReturn(bibliographicEntity.getItemEntities());
        Mockito.when(nullPointerException.getCause()).thenReturn(nullPointerException1);
        Mockito.when(nullPointerException1.getCause()).thenReturn(nullPointerException2);
        Mockito.when(nullPointerException2.getMessage()).thenReturn("error");

        Mockito.when(etlDataLoadDAOService.saveItemEntity(Mockito.any())).thenThrow(nullPointerException);
        Mockito.when(dbReportUtil.generateBibHoldingsFailureReportEntity(Mockito.any(),Mockito.any())).thenReturn(new ArrayList<>());
        try {
            bibDataProcessor.processETLExchagneAndPersistToDB(etlExchange);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void persistDataToDBExceptionHoldingsEx() throws Exception {
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        ETLExchange etlExchange = new ETLExchange();
        etlExchange.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        etlExchange.setInstitutionEntityMap(etlExchange.getInstitutionEntityMap() == null ? new HashMap() : etlExchange.getInstitutionEntityMap());
        etlExchange.setCollectionGroupMap(etlExchange.getCollectionGroupMap() == null ? new HashMap() : etlExchange.getCollectionGroupMap());
        Mockito.doThrow(NullPointerException.class).when(etlDataLoadDAOService).saveBibliographicEntityList(Mockito.anyList());
        Mockito.doThrow(NullPointerException.class).doNothing().when(etlDataLoadDAOService).saveBibliographicEntity(Mockito.any());
        Mockito.when(itemDetailsRepository.findByBarcode(Mockito.anyString())).thenReturn(bibliographicEntity.getItemEntities());
        Mockito.when(nullPointerException.getCause()).thenReturn(nullPointerException1);
        Mockito.when(nullPointerException1.getCause()).thenReturn(nullPointerException2);
        Mockito.when(nullPointerException2.getMessage()).thenReturn("error");
        Mockito.when(etlDataLoadDAOService.savedHoldingsEntity(Mockito.any())).thenThrow(nullPointerException);
        try {
            bibDataProcessor.processETLExchagneAndPersistToDB(etlExchange);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void persistDataToDBExceptionBibEx() throws Exception {
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        ETLExchange etlExchange = new ETLExchange();
        etlExchange.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        etlExchange.setInstitutionEntityMap(etlExchange.getInstitutionEntityMap() == null ? new HashMap() : etlExchange.getInstitutionEntityMap());
        etlExchange.setCollectionGroupMap(etlExchange.getCollectionGroupMap() == null ? new HashMap() : etlExchange.getCollectionGroupMap());
        Mockito.doThrow(NullPointerException.class).when(etlDataLoadDAOService).saveBibliographicEntityList(Mockito.anyList());
        Mockito.doThrow(NullPointerException.class).when(etlDataLoadDAOService).saveBibliographicEntity(Mockito.any());
        Mockito.when(itemDetailsRepository.findByBarcode(Mockito.anyString())).thenReturn(bibliographicEntity.getItemEntities());
        Mockito.when(nullPointerException.getCause()).thenReturn(nullPointerException1);
        Mockito.when(nullPointerException1.getCause()).thenReturn(nullPointerException2);
        Mockito.when(nullPointerException2.getMessage()).thenReturn("error");
        Mockito.when(etlDataLoadDAOService.savedHoldingsEntity(Mockito.any())).thenThrow(nullPointerException);
        try {
            bibDataProcessor.processETLExchagneAndPersistToDB(etlExchange);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void processFailuresForHoldingsAndItems() throws Exception {
        Mockito.when(institutionMap.get("PUL")).thenReturn(1);
        Mockito.when(collectionGroupMap.get("Shared")).thenReturn(1);
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        dbReportUtil.setInstitutionEntitiesMap(institutionMap);
        dbReportUtil.setCollectionGroupMap(collectionGroupMap);
        bibDataProcessor.setXmlFileName("testFailureForItemsAndHoldings.xml");
        ReportEntity reportEntity = bibDataProcessor.processBibHoldingsItems(dbReportUtil, bibliographicEntity);
        assertNotNull(reportEntity);
     }

    @Test
    public void getXmlFileName() throws Exception {
     ReflectionTestUtils.setField(bibDataProcessor,"xmlFileName","file");
     String xmlFileName=bibDataProcessor.getXmlFileName();
     assertEquals("file",xmlFileName);
     ReflectionTestUtils.setField(bibDataProcessor,"institutionName","PUL");
     String institutionName=bibDataProcessor.getInstitutionName();
     assertEquals("PUL",institutionName);
    }

    @Test
    public void checkDataTruncateIssue() throws Exception {
        Mockito.when(institutionMap.get("NYPL")).thenReturn(3);
        Mockito.when(itemStatusMap.get("Available")).thenReturn(1);
        Mockito.when(collectionGroupMap.get("Open")).thenReturn(2);

        Map<String, Integer> institution = new HashMap<>();
        institution.put("NYPL", 3);
        Mockito.when(institutionMap.entrySet()).thenReturn(institution.entrySet());

        Map<String, Integer> collection = new HashMap<>();
        collection.put("Open", 2);
        Mockito.when(collectionGroupMap.entrySet()).thenReturn(collection.entrySet());

        XmlRecordEntity xmlRecordEntity = new XmlRecordEntity();
        xmlRecordEntity.setXmlFileName("BibMultipleHoldingsItems.xml");

        URL resource = getClass().getResource("BibMultipleHoldingsItems.xml");
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

        BibliographicEntity bibliographicEntity = null;

        BibPersisterCallable bibPersisterCallable = new BibPersisterCallable();
        bibPersisterCallable.setItemStatusMap(itemStatusMap);
        bibPersisterCallable.setInstitutionEntitiesMap(institutionMap);
        bibPersisterCallable.setCollectionGroupMap(collectionGroupMap);
        bibPersisterCallable.setXmlRecordEntity(xmlRecordEntity);
        bibPersisterCallable.setBibRecord(bibRecord);
        bibPersisterCallable.setDbReportUtil(dbReportUtil);
        bibPersisterCallable.setInstitutionName("NYPL");
        Map<String, Object> map = (Map<String, Object>) bibPersisterCallable.call();
        if (map != null) {
            Object object = map.get("bibliographicEntity");
            if (object != null) {
                bibliographicEntity = (BibliographicEntity) object;
            }
        }

        assertNotNull(bibliographicEntity);
        assertEquals(bibliographicEntity.getHoldingsEntities().size(), 2);
        assertEquals(bibliographicEntity.getItemEntities().size(), 4);

        assertNotNull(bibliographicDetailsRepository);
        assertNotNull(producer);

        ETLExchange etlExchange = new ETLExchange();
        etlExchange.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        etlExchange.setInstitutionEntityMap(new HashMap());
        etlExchange.setCollectionGroupMap(new HashMap());
        bibDataProcessor.setXmlFileName("BibMultipleHoldingsItems.xml");
        bibDataProcessor.setInstitutionName("NYPL");
        bibDataProcessor.processETLExchagneAndPersistToDB(etlExchange);
    }

    private BibliographicEntity getBibliographicEntity() {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent(content.getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("etl");
        bibliographicEntity.setLastUpdatedBy("etl");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("001");
        List<BibliographicEntity> bibliographicEntitylist = new LinkedList(Arrays.asList(bibliographicEntity));

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionHoldingsId("002");
        List<HoldingsEntity> holdingsEntitylist = new LinkedList(Arrays.asList(holdingsEntity));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setCallNumberType("0");
        itemEntity.setCallNumber("callNum");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("etl");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("etl");
        itemEntity.setBarcode("334330028533193343300285331933433002853319555565");
        itemEntity.setOwningInstitutionItemId(".i1231");
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCustomerCode("PA");
        itemEntity.setItemAvailabilityStatusId(1);
        List<ItemEntity> itemEntitylist = new LinkedList(Arrays.asList(itemEntity));


        holdingsEntity.setBibliographicEntities(bibliographicEntitylist);
        holdingsEntity.setItemEntities(itemEntitylist);
        bibliographicEntity.setHoldingsEntities(holdingsEntitylist);
        bibliographicEntity.setItemEntities(itemEntitylist);
        itemEntity.setHoldingsEntities(holdingsEntitylist);
        itemEntity.setBibliographicEntities(bibliographicEntitylist);

        return bibliographicEntity;
    }
}