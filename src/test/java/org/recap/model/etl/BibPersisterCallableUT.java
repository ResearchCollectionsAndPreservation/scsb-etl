package org.recap.model.etl;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.camel.BibDataProcessor;
import org.recap.model.csv.FailureReportReCAPCSVRecord;
import org.recap.model.jaxb.JAXBHandler;
import org.recap.model.jaxb.BibRecord;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.XmlRecordEntity;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.util.DBReportUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by chenchulakshmig on 1/7/16.
 */
public class BibPersisterCallableUT extends BaseTestCase {

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    BibDataProcessor bibDataProcessor;

    @Autowired
    DBReportUtil dbReportUtil;

    @Mock
    private Map<String, Integer> institutionMap;

    @Mock
    private Map itemStatusMap;

    @Mock
    private Map<String, Integer> collectionGroupMap;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void newInstance() {
        BibRecord bibRecord = new BibRecord();
        BibPersisterCallable bibPersisterCallable = new BibPersisterCallable();
        bibPersisterCallable.setItemStatusMap(itemStatusMap);
        bibPersisterCallable.setInstitutionEntitiesMap(institutionMap);
        bibPersisterCallable.setCollectionGroupMap(collectionGroupMap);
        bibPersisterCallable.setBibRecord(bibRecord);
        assertNotNull(bibPersisterCallable);
    }

    @Test
    public void checkNullConstraints() throws Exception {
        Mockito.when(institutionMap.get("NYPL")).thenReturn(3);
        Mockito.when(itemStatusMap.get("Available")).thenReturn(1);
        Mockito.when(collectionGroupMap.get("Open")).thenReturn(2);

        List<FailureReportReCAPCSVRecord> failureReportReCAPCSVRecords = new ArrayList<>();

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
        bibRecord = (BibRecord) JAXBHandler.getInstance().unmarshal(FileUtils.readFileToString(file, "UTF-8"), BibRecord.class);
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
        Map<String, Object> map = (Map<String, Object>) bibPersisterCallable.call();
        if (map != null) {
            Object object = map.get("reportEntities");
            if (object != null) {
                failureReportReCAPCSVRecords.addAll((List<FailureReportReCAPCSVRecord>) object);
            }
        }
        assertTrue(failureReportReCAPCSVRecords.size() == 2);
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
        bibRecord = (BibRecord) JAXBHandler.getInstance().unmarshal(FileUtils.readFileToString(file, "UTF-8"), BibRecord.class);
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
        Map<String, Object> map = (Map<String, Object>) bibPersisterCallable.call();
        BibliographicEntity bibliographicEntity = (BibliographicEntity) map.get("bibliographicEntity");
        assertNotNull(bibliographicEntity);
        assertTrue(bibliographicEntity.getHoldingsEntities().size() == 1);
        assertNotNull(bibliographicEntity.getHoldingsEntities().get(0));
        assertNotNull(bibliographicEntity.getHoldingsEntities().get(0).getOwningInstitutionHoldingsId());
        assertNotEquals(bibliographicEntity.getHoldingsEntities().get(0).getOwningInstitutionHoldingsId(), ".c11316020.c11333133.c11349165.c11365225.c11304777.c10638106c11349165.c11365225.c11304777.c10638106c11349165.c11365225.c11304777.c10638106c11349165.c11365225.c11304777.c10638106");
    }
}