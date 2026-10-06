package org.recap.camel;

import jakarta.persistence.PersistenceException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.repository.ItemDetailsRepository;
import org.recap.repositoryrw.ReportDetailRepository;
import org.recap.service.BibliographicRepositoryDAO;
import org.recap.util.DBReportUtil;
import org.springframework.test.util.ReflectionTestUtils;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


/**
 * Created by angelind on 26/7/16.
 */


public class BibDataProcessorUT extends BaseTestCaseUT {

    @InjectMocks
    BibDataProcessor bibDataProcessor;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    BibliographicRepositoryDAO bibliographicRepositoryDAO;

    @Mock
    ReportDetailRepository reportDetailRepository;
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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

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
    public void persistDataToDBPersistenceExceptionDifferentOwingInstitutionItemId() throws Exception {
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        bibliographicEntity.getItemEntities().get(0).setOwningInstitutionItemId("1");
        ETLExchange etlExchange = new ETLExchange();
        etlExchange.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        etlExchange.setInstitutionEntityMap(etlExchange.getInstitutionEntityMap() == null ? new HashMap() : etlExchange.getInstitutionEntityMap());
        etlExchange.setCollectionGroupMap(etlExchange.getCollectionGroupMap() == null ? new HashMap() : etlExchange.getCollectionGroupMap());
        Mockito.doThrow(PersistenceException.class).when(bibliographicRepositoryDAO).saveOrUpdateList(Mockito.anyList());
        Mockito.when(itemDetailsRepository.findByBarcode(Mockito.anyString())).thenReturn(getBibliographicEntity().getItemEntities());
        bibDataProcessor.processETLExchagneAndPersistToDB(etlExchange);
    }

    @Test
    public void persistDataToDBPersistenceExceptionAndNullPoiterException() throws Exception {
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        bibliographicEntity.getItemEntities().get(0).setOwningInstitutionItemId("1");
        ETLExchange etlExchange = new ETLExchange();
        etlExchange.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        etlExchange.setInstitutionEntityMap(etlExchange.getInstitutionEntityMap() == null ? new HashMap() : etlExchange.getInstitutionEntityMap());
        etlExchange.setCollectionGroupMap(etlExchange.getCollectionGroupMap() == null ? new HashMap() : etlExchange.getCollectionGroupMap());
        Mockito.doThrow(PersistenceException.class).when(bibliographicRepositoryDAO).saveOrUpdateList(Mockito.anyList());
        Mockito.doThrow(new NullPointerException()).when(itemDetailsRepository).findByBarcode(Mockito.anyString());
        bibDataProcessor.processETLExchagneAndPersistToDB(etlExchange);
    }

    @Test
    public void persistDataToDBPersistenceExceptionDuplicate() throws Exception {
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        ETLExchange etlExchange = new ETLExchange();
        etlExchange.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        etlExchange.setInstitutionEntityMap(etlExchange.getInstitutionEntityMap() == null ? new HashMap() : etlExchange.getInstitutionEntityMap());
        etlExchange.setCollectionGroupMap(etlExchange.getCollectionGroupMap() == null ? new HashMap() : etlExchange.getCollectionGroupMap());
        Mockito.doThrow(PersistenceException.class).when(bibliographicRepositoryDAO).saveOrUpdateList(Mockito.anyList());
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
        Mockito.doThrow(NullPointerException.class).when(bibliographicRepositoryDAO).saveOrUpdateList(Mockito.anyList());
        Mockito.when(itemDetailsRepository.findByBarcode(Mockito.anyString())).thenReturn(bibliographicEntity.getItemEntities());
        try {
            bibDataProcessor.processETLExchagneAndPersistToDB(etlExchange);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void processBibHoldingsItemsHoldingsException(){
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        Mockito.when(nullPointerException.getCause()).thenReturn(nullPointerException1);
        Mockito.when(nullPointerException1.getCause()).thenReturn(nullPointerException2);
        Mockito.when(nullPointerException2.getMessage()).thenReturn("error");
        Mockito.doThrow(nullPointerException).when(etlDataLoadDAOService).saveBibliographicEntity(bibliographicEntity);
        Mockito.when(dbReportUtil.generateBibHoldingsFailureReportEntity(any(), any())).thenReturn(reportDataEntities);
        ReportEntity reportEntity = bibDataProcessor.processBibHoldingsItems(dbReportUtil,bibliographicEntity);
        assertNotNull(reportEntity);
    }

    @Test
    public void processBibHoldingsItemsException(){
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        Mockito.when(nullPointerException.getCause()).thenReturn(nullPointerException1);
        Mockito.when(nullPointerException1.getCause()).thenReturn(nullPointerException2);
        Mockito.when(nullPointerException2.getMessage()).thenReturn("error");
        Mockito.when( bibliographicRepositoryDAO.saveOrUpdateItemEntity(any())).thenThrow(nullPointerException);
        ReportEntity reportEntity = bibDataProcessor.processBibHoldingsItems(dbReportUtil,bibliographicEntity);
        assertNotNull(reportEntity);
    }
    @Test
    public void processBibHoldingsItemsNullException(){
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        Mockito.when(nullPointerException.getCause()).thenReturn(nullPointerException1);
        Mockito.when(nullPointerException1.getCause()).thenReturn(nullPointerException2);
        Mockito.when(nullPointerException2.getMessage()).thenReturn("error");
        Mockito.when( bibliographicRepositoryDAO.saveOrUpdateItemEntity(any())).thenThrow(nullPointerException);
        Mockito.when(dbReportUtil.generateBibHoldingsFailureReportEntity(any(), any())).thenReturn(reportDataEntities);
        ReportEntity reportEntity = bibDataProcessor.processBibHoldingsItems(dbReportUtil,bibliographicEntity);
        assertNotNull(reportEntity);
    }

    @Test
    public void processBibHoldingsItemsNullExceptionWithoutMessage(){
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        Mockito.when( bibliographicRepositoryDAO.saveOrUpdateItemEntity(any())).thenThrow(new NullPointerException());
        Mockito.when(dbReportUtil.generateBibHoldingsFailureReportEntity(any(), any())).thenReturn(reportDataEntities);
        ReportEntity reportEntity = bibDataProcessor.processBibHoldingsItems(dbReportUtil,bibliographicEntity);
        assertNotNull(reportEntity);
    }

    @Test
    public void isDuplicateItem(){
        List<ItemEntity> existingItemEntityList = new ArrayList<>();
        ItemEntity itemEntity = getBibliographicEntity().getItemEntities().get(0);
        existingItemEntityList.add(itemEntity);
        ReflectionTestUtils.invokeMethod(bibDataProcessor,"isDuplicateItem",existingItemEntityList,itemEntity);
    }

    @Test
    public void setDuplicateBarcodeReportInfoForItemsinSameBib() {
        String barcode = "24366";
        ItemEntity existingItemEntity = getBibliographicEntity().getItemEntities().get(0);
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        HoldingsEntity holdingsEntity = getBibliographicEntity().getHoldingsEntities().get(0);
        ItemEntity itemEntity = getBibliographicEntity().getItemEntities().get(0);
        ReflectionTestUtils.invokeMethod(bibDataProcessor, "setDuplicateBarcodeReportInfoForItemsinSameBib", barcode, existingItemEntity, dbReportUtil, bibliographicEntity, holdingsEntity, itemEntity);
    }

    @Test
    public void getExistingBarcodeItemWithinSameBib() {
        List<ItemEntity> existingItemList = getBibliographicEntity().getItemEntities();
        ItemEntity itemEntity = getBibliographicEntity().getItemEntities().get(0);
        ReflectionTestUtils.invokeMethod(bibDataProcessor, "getExistingBarcodeItemWithinSameBib", existingItemList, itemEntity);
    }

    @Test
    public void getExistingBarcodeItemWithinSameBibWithNullValue() {
        List<ItemEntity> existingItemList = getBibliographicEntity().getItemEntities();
        ItemEntity itemEntity = getBibliographicEntity().getItemEntities().get(0);
        itemEntity.setBarcode("456631");
        ReflectionTestUtils.invokeMethod(bibDataProcessor, "getExistingBarcodeItemWithinSameBib", existingItemList, itemEntity);
    }

    @Test
    public void persistDataToDBExceptionEx() throws Exception {
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        ETLExchange etlExchange = new ETLExchange();
        etlExchange.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        etlExchange.setInstitutionEntityMap(etlExchange.getInstitutionEntityMap() == null ? new HashMap() : etlExchange.getInstitutionEntityMap());
        etlExchange.setCollectionGroupMap(etlExchange.getCollectionGroupMap() == null ? new HashMap() : etlExchange.getCollectionGroupMap());
        Mockito.doThrow(NullPointerException.class).when(bibliographicRepositoryDAO).saveOrUpdateList(Mockito.anyList());
        Mockito.doThrow(NullPointerException.class).doNothing().when(etlDataLoadDAOService).saveBibliographicEntity(any());
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
        Mockito.doThrow(NullPointerException.class).when(bibliographicRepositoryDAO).saveOrUpdateList(Mockito.anyList());
        Mockito.doThrow(NullPointerException.class).doNothing().when(etlDataLoadDAOService).saveBibliographicEntity(any());
        Mockito.when(itemDetailsRepository.findByBarcode(Mockito.anyString())).thenReturn(bibliographicEntity.getItemEntities());
        Mockito.when(nullPointerException.getCause()).thenReturn(nullPointerException1);
        Mockito.when(nullPointerException1.getCause()).thenReturn(nullPointerException2);
        Mockito.when(nullPointerException2.getMessage()).thenReturn("error");

        Mockito.when(etlDataLoadDAOService.saveItemEntity(any())).thenThrow(nullPointerException);
        Mockito.when(dbReportUtil.generateBibHoldingsFailureReportEntity(any(), any())).thenReturn(new ArrayList<>());
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
        Mockito.doThrow(NullPointerException.class).when(bibliographicRepositoryDAO).saveOrUpdateList(Mockito.anyList());
        Mockito.doThrow(NullPointerException.class).doNothing().when(etlDataLoadDAOService).saveBibliographicEntity(any());
        Mockito.when(itemDetailsRepository.findByBarcode(Mockito.anyString())).thenReturn(bibliographicEntity.getItemEntities());
        Mockito.when(nullPointerException.getCause()).thenReturn(nullPointerException1);
        Mockito.when(nullPointerException1.getCause()).thenReturn(nullPointerException2);
        Mockito.when(nullPointerException2.getMessage()).thenReturn("error");
        Mockito.when(etlDataLoadDAOService.savedHoldingsEntity(any())).thenThrow(nullPointerException);
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
        Mockito.doThrow(NullPointerException.class).when(bibliographicRepositoryDAO).saveOrUpdateList(Mockito.anyList());
        Mockito.doThrow(NullPointerException.class).when(bibliographicRepositoryDAO).saveOrUpdate(any());
        Mockito.when(itemDetailsRepository.findByBarcode(Mockito.anyString())).thenReturn(bibliographicEntity.getItemEntities());
        Mockito.when(nullPointerException.getCause()).thenReturn(nullPointerException1);
        Mockito.when(nullPointerException1.getCause()).thenReturn(nullPointerException2);
        Mockito.when(nullPointerException2.getMessage()).thenReturn("error");
        Mockito.when(etlDataLoadDAOService.savedHoldingsEntity(any())).thenThrow(nullPointerException);
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
        ReflectionTestUtils.setField(bibDataProcessor, "xmlFileName", "file");
        String xmlFileName = bibDataProcessor.getXmlFileName();
        assertEquals("file", xmlFileName);
        ReflectionTestUtils.setField(bibDataProcessor, "institutionName", "PUL");
        String institutionName = bibDataProcessor.getInstitutionName();
        assertEquals("PUL", institutionName);
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
        itemEntity.setOwningInstitutionItemId("1231");
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
