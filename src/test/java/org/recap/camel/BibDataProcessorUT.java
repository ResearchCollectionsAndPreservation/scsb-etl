package org.recap.camel;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.RecapCommonConstants;
import org.recap.model.etl.BibPersisterCallable;
import org.recap.model.jaxb.JAXBHandler;
import org.recap.model.jaxb.marc.BibRecord;
import org.recap.model.jpa.*;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.repository.ReportDetailRepository;
import org.recap.util.DBReportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by angelind on 26/7/16.
 */
public class BibDataProcessorUT extends BaseTestCase {

    @Autowired
    BibDataProcessor bibDataProcessor;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Value("${etl.report.directory}")
    private String reportDirectoryPath;

    @Mock
    private Map<String, Integer> institutionMap;

    @Mock
    private Map<String, Integer> collectionGroupMap;

    @Mock
    private Map itemStatusMap;

    @Autowired
    private ProducerTemplate producer;

    @Autowired
    private DBReportUtil dbReportUtil;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void persistDataToDB() throws Exception {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("etl");
        bibliographicEntity.setLastUpdatedBy("CUl");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setOwningInstitutionId(1);
        String owningInstitutionBibId = String.valueOf(random.nextInt());
        bibliographicEntity.setOwningInstitutionBibId(null);


        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(null);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setCallNumberType("0");
        itemEntity.setCallNumber("callNum");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("etl");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("etl");
        itemEntity.setBarcode("1231");
        itemEntity.setOwningInstitutionItemId(".i1231");
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCustomerCode("PA");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));

        ETLExchange etlExchange = new ETLExchange();
        ETLExchange etlExchangeNew = new ETLExchange();
        etlExchange.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        etlExchange.setInstitutionEntityMap(etlExchange.getInstitutionEntityMap() == null ? new HashMap() : etlExchange.getInstitutionEntityMap());
        etlExchange.setCollectionGroupMap(etlExchange.getCollectionGroupMap() == null ? new HashMap() : etlExchange.getCollectionGroupMap());
        try {
            bibDataProcessor.processETLExchagneAndPersistToDB(etlExchange);
            bibDataProcessor.processETLExchagneAndPersistToDB(etlExchangeNew);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(1, owningInstitutionBibId);
        assertNull(savedBibliographicEntity);
    }

    @Test
    public void processFailuresForHoldingsAndItems() throws Exception {

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

        Mockito.when(institutionMap.get("PUL")).thenReturn(1);
        Mockito.when(collectionGroupMap.get("Shared")).thenReturn(1);

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent(content.getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("etl");
        bibliographicEntity.setLastUpdatedBy("etl");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setOwningInstitutionId(1);
        String owningInstitutionBibId = "001";
        bibliographicEntity.setOwningInstitutionBibId(owningInstitutionBibId);


        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionHoldingsId("002");

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
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));

        dbReportUtil.setInstitutionEntitiesMap(institutionMap);
        dbReportUtil.setCollectionGroupMap(collectionGroupMap);
        bibDataProcessor.setXmlFileName("testFailureForItemsAndHoldings.xml");

        ReportEntity reportEntity = bibDataProcessor.processBibHoldingsItems(dbReportUtil, bibliographicEntity);
        assertNotNull(reportEntity);
        assertEquals(bibDataProcessor.getXmlFileName(), reportEntity.getFileName());
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
        bibRecord = (BibRecord) JAXBHandler.getInstance().unmarshal(FileUtils.readFileToString(file, "UTF-8"), BibRecord.class);
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

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(bibliographicEntity.getOwningInstitutionId(), bibliographicEntity.getOwningInstitutionBibId());
        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getHoldingsEntities());
        assertEquals(savedBibliographicEntity.getHoldingsEntities().size(), 2);
        assertNotNull(savedBibliographicEntity.getItemEntities());
        java.lang.Thread.sleep(500);

        List<ReportEntity> reportEntities = reportDetailRepository.findByFileNameAndInstitutionNameAndType(bibDataProcessor.getXmlFileName(), bibDataProcessor.getInstitutionName(), RecapCommonConstants.FAILURE);
        assertNotNull(reportEntities);
    }

}