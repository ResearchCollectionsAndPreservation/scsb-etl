package org.recap.repository;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.BibliographicPK;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by pvsubrah on 6/21/16.
 */
public class BibliographicDetailsRepositoryUT extends BaseTestCase {

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    HoldingsDetailsRepository holdingsDetailsRepository;

    @Autowired
    ItemDetailsRepository itemDetailsRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void saveAndFindBibHoldingsItemEntity() throws Exception {

        assertNotNull(bibliographicDetailsRepository);
        assertNotNull(holdingsDetailsRepository);
        assertNotNull(entityManager);

        Random random = new Random();

        String owningInstitutionBibId = String.valueOf(random.nextInt());
        int owningInstitutionId = 1;

        Page<BibliographicEntity> byOwningInstitutionId = bibliographicDetailsRepository.findByOwningInstitutionId(PageRequest.of(0, 10), owningInstitutionId);

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("Mock Bib Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("etl");
        bibliographicEntity.setLastUpdatedBy("etl");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setOwningInstitutionBibId(owningInstitutionBibId);
        bibliographicEntity.setOwningInstitutionId(owningInstitutionId);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionId(owningInstitutionId);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

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

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);

        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getBibliographicId());

        Long countByOwningInstitutionIdAfterAdd = bibliographicDetailsRepository.countByOwningInstitutionId(owningInstitutionId);
        assertTrue(countByOwningInstitutionIdAfterAdd > byOwningInstitutionId.getTotalElements());

        List<BibliographicEntity> byOwningInstitutionBibId = bibliographicDetailsRepository.findByOwningInstitutionBibId(owningInstitutionBibId);
        assertNotNull(byOwningInstitutionBibId);
        assertTrue(byOwningInstitutionBibId.size() > 0);

        BibliographicEntity byOwningInstitutionIdAndOwningInstitutionBibId = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(owningInstitutionId, owningInstitutionBibId);
        assertNotNull(byOwningInstitutionIdAndOwningInstitutionBibId);

        BibliographicPK bibliographicPK = new BibliographicPK();
        bibliographicPK.setOwningInstitutionId(owningInstitutionId);
        bibliographicPK.setOwningInstitutionBibId(owningInstitutionBibId);
        BibliographicEntity entity = bibliographicDetailsRepository.getOne(bibliographicPK);
        assertNotNull(entity);

        assertNotNull(holdingsDetailsRepository);
        HoldingsEntity savedHoldingsEntity = savedBibliographicEntity.getHoldingsEntities().get(0);
        assertNotNull(savedHoldingsEntity);
        assertNotNull(savedHoldingsEntity.getHoldingsId());

        HoldingsEntity byHoldingsId = holdingsDetailsRepository.findByHoldingsId(savedHoldingsEntity.getHoldingsId());
        assertNotNull(byHoldingsId);

        assertNotNull(itemDetailsRepository);
        ItemEntity savedItemEntity = savedBibliographicEntity.getItemEntities().get(0);
        assertNotNull(savedItemEntity);
        assertNotNull(savedItemEntity.getItemId());

        ItemEntity byItemId = itemDetailsRepository.findByItemId(savedItemEntity.getItemId());
        assertNotNull(byItemId);
    }

    @Test
    public void findCountOfBibliographicHoldings() throws Exception {
        Random random = new Random();
        Long beforeSaveCount = bibliographicDetailsRepository.findCountOfBibliographicHoldings();

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("Mock Bib Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("etl");
        bibliographicEntity.setLastUpdatedBy("etl");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        bibliographicEntity.setOwningInstitutionId(1);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

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

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);

        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getBibliographicId());

        Long afterSaveCount = bibliographicDetailsRepository.findCountOfBibliographicHoldings();
        assertTrue(afterSaveCount > beforeSaveCount);
    }

    @Test
    public void countRecordsForFullDump() throws Exception {
        Random random = new Random();

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("Mock Bib Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("etl");
        bibliographicEntity.setLastUpdatedBy("etl");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        bibliographicEntity.setOwningInstitutionId(3);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionId(3);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setCallNumberType("0");
        itemEntity.setCallNumber("callNum");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("etl");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("etl");
        itemEntity.setBarcode("1231");
        itemEntity.setOwningInstitutionItemId(".i1231");
        itemEntity.setOwningInstitutionId(3);
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCustomerCode("PA");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);

        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getBibliographicId());

        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("NYPL");
        Long count = bibliographicDetailsRepository.countRecordsForFullDump(cgIds,institutionCodes);
        assertEquals(new Long(1),count);
    }

    @Test
    public void countByInstitutionCodesWithSameOwnInstBibForAll3Inst() throws Exception {
        BibliographicEntity bibliographicEntityPUL = new BibliographicEntity();
        bibliographicEntityPUL.setContent("Mock Bib Content".getBytes());
        bibliographicEntityPUL.setCreatedDate(new Date());
        bibliographicEntityPUL.setCreatedBy("etl");
        bibliographicEntityPUL.setLastUpdatedBy("etl");
        bibliographicEntityPUL.setLastUpdatedDate(new Date());
        bibliographicEntityPUL.setOwningInstitutionBibId(String.valueOf(100));
        bibliographicEntityPUL.setOwningInstitutionId(1);

        HoldingsEntity holdingsEntityPUL = new HoldingsEntity();
        holdingsEntityPUL.setContent("mock holdings".getBytes());
        holdingsEntityPUL.setCreatedDate(new Date());
        holdingsEntityPUL.setCreatedBy("etl");
        holdingsEntityPUL.setLastUpdatedDate(new Date());
        holdingsEntityPUL.setLastUpdatedBy("etl");
        holdingsEntityPUL.setOwningInstitutionId(1);
        holdingsEntityPUL.setOwningInstitutionHoldingsId(String.valueOf(10));

        ItemEntity itemEntityPUL = new ItemEntity();
        itemEntityPUL.setCallNumberType("0");
        itemEntityPUL.setCallNumber("callNum");
        itemEntityPUL.setCreatedDate(new Date());
        itemEntityPUL.setCreatedBy("etl");
        itemEntityPUL.setLastUpdatedDate(new Date());
        itemEntityPUL.setLastUpdatedBy("etl");
        itemEntityPUL.setBarcode("1231");
        itemEntityPUL.setOwningInstitutionItemId(".i1231");
        itemEntityPUL.setOwningInstitutionId(1);
        itemEntityPUL.setCollectionGroupId(1);
        itemEntityPUL.setCustomerCode("PA");
        itemEntityPUL.setItemAvailabilityStatusId(1);
        itemEntityPUL.setHoldingsEntities(Arrays.asList(holdingsEntityPUL));
        holdingsEntityPUL.setItemEntities(Arrays.asList(itemEntityPUL));

        bibliographicEntityPUL.setHoldingsEntities(Arrays.asList(holdingsEntityPUL));
        bibliographicEntityPUL.setItemEntities(Arrays.asList(itemEntityPUL));

        BibliographicEntity savedBibliographicEntityPUL = bibliographicDetailsRepository.saveAndFlush(bibliographicEntityPUL);
        entityManager.refresh(savedBibliographicEntityPUL);

        BibliographicEntity bibliographicEntityCUL = new BibliographicEntity();
        bibliographicEntityCUL.setContent("Mock Bib Content".getBytes());
        bibliographicEntityCUL.setCreatedDate(new Date());
        bibliographicEntityCUL.setCreatedBy("etl");
        bibliographicEntityCUL.setLastUpdatedBy("etl");
        bibliographicEntityCUL.setLastUpdatedDate(new Date());
        bibliographicEntityCUL.setOwningInstitutionBibId(String.valueOf(100));
        bibliographicEntityCUL.setOwningInstitutionId(2);

        HoldingsEntity holdingsEntityCUL = new HoldingsEntity();
        holdingsEntityCUL.setContent("mock holdings".getBytes());
        holdingsEntityCUL.setCreatedDate(new Date());
        holdingsEntityCUL.setCreatedBy("etl");
        holdingsEntityCUL.setLastUpdatedDate(new Date());
        holdingsEntityCUL.setLastUpdatedBy("etl");
        holdingsEntityCUL.setOwningInstitutionId(2);
        holdingsEntityCUL.setOwningInstitutionHoldingsId(String.valueOf(10));

        ItemEntity itemEntityCUL = new ItemEntity();
        itemEntityCUL.setCallNumberType("0");
        itemEntityCUL.setCallNumber("callNum");
        itemEntityCUL.setCreatedDate(new Date());
        itemEntityCUL.setCreatedBy("etl");
        itemEntityCUL.setLastUpdatedDate(new Date());
        itemEntityCUL.setLastUpdatedBy("etl");
        itemEntityCUL.setBarcode("1231");
        itemEntityCUL.setOwningInstitutionItemId(".i1231");
        itemEntityCUL.setOwningInstitutionId(2);
        itemEntityCUL.setCollectionGroupId(1);
        itemEntityCUL.setCustomerCode("PA");
        itemEntityCUL.setItemAvailabilityStatusId(1);
        itemEntityCUL.setHoldingsEntities(Arrays.asList(holdingsEntityCUL));
        holdingsEntityCUL.setItemEntities(Arrays.asList(itemEntityCUL));

        bibliographicEntityCUL.setHoldingsEntities(Arrays.asList(holdingsEntityCUL));
        bibliographicEntityCUL.setItemEntities(Arrays.asList(itemEntityCUL));

        BibliographicEntity savedBibliographicEntityCUL = bibliographicDetailsRepository.saveAndFlush(bibliographicEntityCUL);
        entityManager.refresh(savedBibliographicEntityCUL);

        BibliographicEntity bibliographicEntityNYPL = new BibliographicEntity();
        bibliographicEntityNYPL.setContent("Mock Bib Content".getBytes());
        bibliographicEntityNYPL.setCreatedDate(new Date());
        bibliographicEntityNYPL.setCreatedBy("etl");
        bibliographicEntityNYPL.setLastUpdatedBy("etl");
        bibliographicEntityNYPL.setLastUpdatedDate(new Date());
        bibliographicEntityNYPL.setOwningInstitutionBibId(String.valueOf(100));
        bibliographicEntityNYPL.setOwningInstitutionId(3);

        HoldingsEntity holdingsEntityNYPL = new HoldingsEntity();
        holdingsEntityNYPL.setContent("mock holdings".getBytes());
        holdingsEntityNYPL.setCreatedDate(new Date());
        holdingsEntityNYPL.setCreatedBy("etl");
        holdingsEntityNYPL.setLastUpdatedDate(new Date());
        holdingsEntityNYPL.setLastUpdatedBy("etl");
        holdingsEntityNYPL.setOwningInstitutionId(3);
        holdingsEntityNYPL.setOwningInstitutionHoldingsId(String.valueOf(10));

        ItemEntity itemEntityNYPL = new ItemEntity();
        itemEntityNYPL.setCallNumberType("0");
        itemEntityNYPL.setCallNumber("callNum");
        itemEntityNYPL.setCreatedDate(new Date());
        itemEntityNYPL.setCreatedBy("etl");
        itemEntityNYPL.setLastUpdatedDate(new Date());
        itemEntityNYPL.setLastUpdatedBy("etl");
        itemEntityNYPL.setBarcode("1231");
        itemEntityNYPL.setOwningInstitutionItemId(".i1231");
        itemEntityNYPL.setOwningInstitutionId(3);
        itemEntityNYPL.setCollectionGroupId(1);
        itemEntityNYPL.setCustomerCode("PA");
        itemEntityNYPL.setItemAvailabilityStatusId(1);
        itemEntityNYPL.setHoldingsEntities(Arrays.asList(holdingsEntityNYPL));
        holdingsEntityNYPL.setItemEntities(Arrays.asList(itemEntityNYPL));

        bibliographicEntityNYPL.setHoldingsEntities(Arrays.asList(holdingsEntityNYPL));
        bibliographicEntityNYPL.setItemEntities(Arrays.asList(itemEntityNYPL));

        BibliographicEntity savedBibliographicEntityNYPL = bibliographicDetailsRepository.saveAndFlush(bibliographicEntityNYPL);
        entityManager.refresh(savedBibliographicEntityNYPL);

        assertNotNull(savedBibliographicEntityNYPL);
        assertNotNull(savedBibliographicEntityNYPL.getBibliographicId());

        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        List<String> institutionCodesPUL = new ArrayList<>();
        institutionCodesPUL.add("PUL");
        Long countPUL = bibliographicDetailsRepository.countRecordsForFullDump(cgIds,institutionCodesPUL);
        assertEquals(new Long(1),countPUL);

        List<String> institutionCodesCUL = new ArrayList<>();
        institutionCodesCUL.add("CUL");
        Long countCUL = bibliographicDetailsRepository.countRecordsForFullDump(cgIds,institutionCodesCUL);
        assertEquals(new Long(1),countCUL);

        List<String> institutionCodesNYPL = new ArrayList<>();
        institutionCodesNYPL.add("NYPL");
        Long countNYPL = bibliographicDetailsRepository.countRecordsForFullDump(cgIds,institutionCodesNYPL);
        assertEquals(new Long(1),countNYPL);
    }

    @Test
    public void countRecordsForIncrementalDump() throws Exception {
        Random random = new Random();

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("Mock Bib Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("etl");
        bibliographicEntity.setLastUpdatedBy("etl");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        bibliographicEntity.setOwningInstitutionId(3);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setOwningInstitutionId(3);
        holdingsEntity.setLastUpdatedBy("etl");

        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setCallNumberType("0");
        itemEntity.setCallNumber("callNum");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("etl");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("etl");
        itemEntity.setBarcode("1231");
        itemEntity.setOwningInstitutionItemId(".i1231");
        itemEntity.setOwningInstitutionId(3);
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCustomerCode("PA");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);

        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getBibliographicId());

        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("NYPL");
        Date inputDate = DateUtil.getDateFromString("2016-08-30 11:20", RecapConstants.DATE_FORMAT_YYYYMMDDHHMM);
        Long count = bibliographicDetailsRepository.countRecordsForIncrementalDump(cgIds,institutionCodes,inputDate);
        assertEquals(new Long(1),count);
    }

    @Test
    public void getRecordsForIncrementalDump() throws Exception {
        Random random = new Random();

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("Mock Bib Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("etl");
        bibliographicEntity.setLastUpdatedBy("etl");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        bibliographicEntity.setOwningInstitutionId(1);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

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

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);

        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getBibliographicId());

        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        Date inputDate = DateUtil.getDateFromString("2016-09-02 12:00", RecapConstants.DATE_FORMAT_YYYYMMDDHHMM);
        Page<BibliographicEntity> bibliographicEntities = bibliographicDetailsRepository.getRecordsForIncrementalDump(PageRequest.of(0, 10),cgIds,institutionCodes,inputDate);
        List<BibliographicEntity> bibliographicEntityList = bibliographicEntities.getContent();
        assertNotNull(bibliographicEntityList);
        assertEquals(1,bibliographicEntityList.size());
        assertEquals(new Integer(1),bibliographicEntityList.get(0).getOwningInstitutionId());
        assertEquals("callNum",bibliographicEntityList.get(0).getItemEntities().get(0).getCallNumber());
        assertEquals("0",bibliographicEntityList.get(0).getItemEntities().get(0).getCallNumberType());
        assertEquals(new Integer(1),bibliographicEntityList.get(0).getItemEntities().get(0).getCollectionGroupId());

    }

    @Test
    public void getRecordsForFullDump() throws Exception {
        Random random = new Random();

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("Mock Bib Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("etl");
        bibliographicEntity.setLastUpdatedBy("etl");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        bibliographicEntity.setOwningInstitutionId(1);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

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

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);

        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getBibliographicId());

        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        Page<BibliographicEntity> bibliographicEntities = bibliographicDetailsRepository.getRecordsForFullDump(PageRequest.of(0, 10),cgIds,institutionCodes);
        List<BibliographicEntity> bibliographicEntityList = bibliographicEntities.getContent();
        assertNotNull(bibliographicEntityList);
        assertEquals(1,bibliographicEntityList.size());
        assertEquals(new Integer(1),bibliographicEntityList.get(0).getOwningInstitutionId());
        assertEquals("callNum",bibliographicEntityList.get(0).getItemEntities().get(0).getCallNumber());
        assertEquals("0",bibliographicEntityList.get(0).getItemEntities().get(0).getCallNumberType());
        assertEquals(new Integer(1),bibliographicEntityList.get(0).getItemEntities().get(0).getCollectionGroupId());
    }

    @Test
    public void getDeletedRecordsForFullDump() throws Exception {
        Random random = new Random();

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("Mock Bib Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("etl");
        bibliographicEntity.setLastUpdatedBy("etl");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        bibliographicEntity.setOwningInstitutionId(1);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

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
        itemEntity.setDeleted(true);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);

        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getBibliographicId());

        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        Page<BibliographicEntity> bibliographicEntities = bibliographicDetailsRepository.getDeletedRecordsForFullDump(PageRequest.of(0, 10),cgIds,institutionCodes);
        List<BibliographicEntity> bibliographicEntityList = bibliographicEntities.getContent();
        assertNotNull(bibliographicEntityList);
        assertEquals(1,bibliographicEntityList.size());
        assertEquals(new Integer(1),bibliographicEntityList.get(0).getOwningInstitutionId());
        assertEquals("callNum",bibliographicEntityList.get(0).getItemEntities().get(0).getCallNumber());
        assertEquals("0",bibliographicEntityList.get(0).getItemEntities().get(0).getCallNumberType());
        assertEquals(new Integer(1),bibliographicEntityList.get(0).getItemEntities().get(0).getCollectionGroupId());
    }

    @Test
    public void getDeletedRecordsForIncrementalDump() throws Exception {
        Random random = new Random();

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("Mock Bib Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("etl");
        bibliographicEntity.setLastUpdatedBy("etl");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        bibliographicEntity.setOwningInstitutionId(1);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

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
        itemEntity.setDeleted(true);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);

        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getBibliographicId());

        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        Date inputDate = DateUtil.getDateFromString("2016-09-02 12:00", RecapConstants.DATE_FORMAT_YYYYMMDDHHMM);
        Page<BibliographicEntity> bibliographicEntities = bibliographicDetailsRepository.getDeletedRecordsForIncrementalDump(PageRequest.of(0, 10),cgIds,institutionCodes,inputDate);
        List<BibliographicEntity> bibliographicEntityList = bibliographicEntities.getContent();
        assertNotNull(bibliographicEntityList);
        assertEquals(1,bibliographicEntityList.size());
        assertEquals(new Integer(1),bibliographicEntityList.get(0).getOwningInstitutionId());
        assertEquals("callNum",bibliographicEntityList.get(0).getItemEntities().get(0).getCallNumber());
        assertEquals("0",bibliographicEntityList.get(0).getItemEntities().get(0).getCallNumberType());
        assertEquals(new Integer(1),bibliographicEntityList.get(0).getItemEntities().get(0).getCollectionGroupId());
    }

    @Test
    public void countDeletedRecordsForFullDump() throws Exception {
        Random random = new Random();

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("Mock Bib Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("etl");
        bibliographicEntity.setLastUpdatedBy("etl");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        bibliographicEntity.setOwningInstitutionId(3);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionId(3);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setCallNumberType("0");
        itemEntity.setCallNumber("callNum");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("etl");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("etl");
        itemEntity.setBarcode("1231");
        itemEntity.setOwningInstitutionItemId(".i1231");
        itemEntity.setOwningInstitutionId(3);
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCustomerCode("PA");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setDeleted(true);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);

        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getBibliographicId());

        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("NYPL");
        Long count = bibliographicDetailsRepository.countDeletedRecordsForFullDump(cgIds,institutionCodes);
        assertEquals(new Long(1),count);
    }

    @Test
    public void countDeletedRecordsForIncremental() throws Exception {
        Random random = new Random();

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("Mock Bib Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("etl");
        bibliographicEntity.setLastUpdatedBy("etl");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        bibliographicEntity.setOwningInstitutionId(3);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setOwningInstitutionId(3);
        holdingsEntity.setLastUpdatedBy("etl");

        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setCallNumberType("0");
        itemEntity.setCallNumber("callNum");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("etl");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("etl");
        itemEntity.setBarcode("1231");
        itemEntity.setOwningInstitutionItemId(".i1231");
        itemEntity.setOwningInstitutionId(3);
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCustomerCode("PA");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setDeleted(true);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);

        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getBibliographicId());

        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("NYPL");
        Date inputDate = DateUtil.getDateFromString("2016-08-30 11:20", RecapConstants.DATE_FORMAT_YYYYMMDDHHMM);
        Long count = bibliographicDetailsRepository.countDeletedRecordsForIncremental(cgIds,institutionCodes,inputDate);
        assertEquals(new Long(1),count);
    }
}