package org.recap.model.jpa;

import org.junit.Ignore;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.repository.HoldingsDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by chenchulakshmig on 23/6/16.
 */
public class BibliographicEntityUT extends BaseTestCase {

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    HoldingsDetailsRepository holdingsDetailsRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void saveBibSingleHoldings() throws Exception {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("etl");
        bibliographicEntity.setLastUpdatedBy("etl");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setOwningInstitutionId(1);
        String owningInstitutionBibId = String.valueOf(random.nextInt());
        bibliographicEntity.setOwningInstitutionBibId(owningInstitutionBibId);


        HoldingsEntity holdingsEntity = getHoldingsEntity(random, 1);

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

        BibliographicEntity savedEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedEntity);

        BibliographicPK bibliographicPK = new BibliographicPK(1, owningInstitutionBibId);
        Optional<BibliographicEntity> byBibliographicPK = bibliographicDetailsRepository.findById(bibliographicPK);
        assertEquals(bibliographicPK.getOwningInstitutionId(), byBibliographicPK.get().getOwningInstitutionId());
        assertEquals(bibliographicPK.getOwningInstitutionBibId(), byBibliographicPK.get().getOwningInstitutionBibId());

        assertNotNull(byBibliographicPK);
        assertNotNull(byBibliographicPK.get().getBibliographicId());
        assertNotNull(byBibliographicPK.get().getLastUpdatedBy());
        assertNotNull(byBibliographicPK.get().getCreatedBy());
        assertEquals(byBibliographicPK.get().getContent(), savedEntity.getContent());
        assertEquals(byBibliographicPK.get().getCreatedDate(), savedEntity.getCreatedDate());
        assertEquals(byBibliographicPK.get().getLastUpdatedDate(), savedEntity.getLastUpdatedDate());
        assertNotNull(byBibliographicPK.get().getInstitutionEntity());
        assertNotNull(byBibliographicPK.get().getHoldingsEntities().get(0).getHoldingsId());
        List<ItemEntity> itemEntities = byBibliographicPK.get().getItemEntities();
        assertNotNull(itemEntities);
        assertNotNull(itemEntities.get(0).getItemId());
        assertNotNull(itemEntities.get(0).getBibliographicEntities());
    }

    @Test
    public void saveBibMultipleHoldings() throws Exception {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = getBibliographicEntity(1, String.valueOf(random.nextInt()));

        HoldingsEntity holdingsEntity1 = getHoldingsEntity(random, 1);

        HoldingsEntity holdingsEntity2 = new HoldingsEntity();
        holdingsEntity2.setContent("mock holdings".getBytes());
        holdingsEntity2.setCreatedDate(new Date());
        holdingsEntity2.setCreatedBy("etl");
        holdingsEntity2.setLastUpdatedDate(new Date());
        holdingsEntity2.setOwningInstitutionId(1);
        holdingsEntity2.setLastUpdatedBy("etl");
        holdingsEntity2.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity1, holdingsEntity2));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);

        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getBibliographicId());
        assertNotNull(savedBibliographicEntity.getHoldingsEntities().get(0).getHoldingsId());
        assertNotNull(savedBibliographicEntity.getHoldingsEntities().get(1).getHoldingsId());
    }

    @Test
    public void saveMultipleBibSingleHoldings() throws Exception {
        Random random = new Random();
        BibliographicEntity bibliographicEntity1 = getBibliographicEntity(1, String.valueOf(random.nextInt()));

        BibliographicEntity bibliographicEntity2 = getBibliographicEntity(1, String.valueOf(random.nextInt()));

        HoldingsEntity holdingsEntity = getHoldingsEntity(random, 1);

        bibliographicEntity1.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity2.setHoldingsEntities(Arrays.asList(holdingsEntity));

        BibliographicEntity savedEntity1 = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity1);
        entityManager.refresh(savedEntity1);
        BibliographicEntity savedEntity2 = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity2);
        entityManager.refresh(savedEntity2);

        assertNotNull(savedEntity1);
        assertNotNull(savedEntity1.getBibliographicId());

        assertNotNull(savedEntity2);
        assertNotNull(savedEntity2.getBibliographicId());
    }

    @Test
    public void saveBibSingleHoldingsSingleItem() throws Exception {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = getBibliographicEntity(1, String.valueOf(random.nextInt()));

        HoldingsEntity holdingsEntity = getHoldingsEntity(random, 1);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("etl");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("etl");
        itemEntity.setBarcode("77456");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("1");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);

        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getBibliographicId());
        assertNotNull(savedBibliographicEntity.getHoldingsEntities().get(0).getHoldingsId());
        assertNotNull(savedBibliographicEntity.getItemEntities().get(0).getItemId());
    }

    @Test
    public void saveBibSingleHoldingsMultipleItem() throws Exception {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = getBibliographicEntity(1, String.valueOf(random.nextInt()));

        HoldingsEntity holdingsEntity = getHoldingsEntity(random, 1);

        ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setCreatedDate(new Date());
        itemEntity1.setCreatedBy("etl");
        itemEntity1.setLastUpdatedDate(new Date());
        itemEntity1.setLastUpdatedBy("etl");
        itemEntity1.setCustomerCode("1");
        itemEntity1.setItemAvailabilityStatusId(1);
        itemEntity1.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity1.setOwningInstitutionId(1);
        itemEntity1.setBarcode("12369875");
        itemEntity1.setCallNumber("x.12321");
        itemEntity1.setCollectionGroupId(1);
        itemEntity1.setCallNumberType("1");
        itemEntity1.setHoldingsEntities(Arrays.asList(holdingsEntity));

        ItemEntity itemEntity2 = new ItemEntity();
        itemEntity2.setCreatedDate(new Date());
        itemEntity2.setCreatedBy("etl");
        itemEntity2.setLastUpdatedDate(new Date());
        itemEntity2.setLastUpdatedBy("etl");
        itemEntity2.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity2.setOwningInstitutionId(1);
        itemEntity2.setCustomerCode("1");
        itemEntity2.setBarcode("88523");
        itemEntity2.setItemAvailabilityStatusId(1);
        itemEntity2.setCallNumber("x.12321");
        itemEntity2.setCollectionGroupId(1);
        itemEntity2.setCallNumberType("1");
        itemEntity2.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity1, itemEntity2));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity1, itemEntity2));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);

        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getBibliographicId());
        assertNotNull(savedBibliographicEntity.getHoldingsEntities().get(0).getHoldingsId());
        assertNotNull(savedBibliographicEntity.getItemEntities().get(0).getItemId());
        assertNotNull(savedBibliographicEntity.getItemEntities().get(1).getItemId());
    }

    @Test
    public void boundWith() throws Exception {
        Random random = new Random();

        Integer institutionId = 1;
        String owningInstitutionBibId1 = String.valueOf(random.nextInt());
        String owningInstitutionBibId2 = String.valueOf(random.nextInt());

        BibliographicEntity bibliographicEntity1 = getBibliographicEntity(institutionId, owningInstitutionBibId1);
        HoldingsEntity holdingsEntity = getHoldingsEntity(random, institutionId);
        ItemEntity itemEntity1 = getItemEntity(institutionId, holdingsEntity,"33245564");

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity1));
        bibliographicEntity1.setItemEntities(Arrays.asList(itemEntity1));
        bibliographicEntity1.setHoldingsEntities(Arrays.asList(holdingsEntity));

        BibliographicEntity bibliographicEntity2 = getBibliographicEntity(institutionId, owningInstitutionBibId2);
        bibliographicEntity2.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity2.setItemEntities(Arrays.asList(itemEntity1));

        BibliographicEntity savedEntity1 = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity1);
        entityManager.refresh(savedEntity1);
        BibliographicEntity savedEntity2 = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity2);
        entityManager.refresh(savedEntity2);

        assertNotNull(savedEntity1);
        assertNotNull(savedEntity1.getBibliographicId());

        assertNotNull(savedEntity2);
        assertNotNull(savedEntity2.getBibliographicId());

        BibliographicEntity fetchedBibEntity1 = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(institutionId, owningInstitutionBibId1);
        BibliographicEntity fetchedBibEntity2 = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(institutionId, owningInstitutionBibId2);

        assertNotNull(fetchedBibEntity1);
        assertNotNull(fetchedBibEntity2);
        assertEquals(fetchedBibEntity1.getOwningInstitutionBibId(), owningInstitutionBibId1);
        assertEquals(fetchedBibEntity2.getOwningInstitutionBibId(), owningInstitutionBibId2);
        assertNotNull(fetchedBibEntity1.getHoldingsEntities());
        assertNotNull(fetchedBibEntity2.getHoldingsEntities());
        assertTrue(fetchedBibEntity1.getHoldingsEntities().size() == 1);
        assertTrue(fetchedBibEntity2.getHoldingsEntities().size() == 1);
        assertEquals(fetchedBibEntity1.getHoldingsEntities().get(0).getHoldingsId(), fetchedBibEntity2.getHoldingsEntities().get(0).getHoldingsId());
        assertNotNull(fetchedBibEntity1.getHoldingsEntities().get(0).getItemEntities());
        assertNotNull(fetchedBibEntity2.getHoldingsEntities().get(0).getItemEntities());
        assertTrue(fetchedBibEntity1.getHoldingsEntities().get(0).getItemEntities().size() == 1);
        assertTrue(fetchedBibEntity2.getHoldingsEntities().get(0).getItemEntities().size() == 1);
        assertEquals(fetchedBibEntity1.getHoldingsEntities().get(0).getItemEntities().get(0).getItemId(), fetchedBibEntity2.getHoldingsEntities().get(0).getItemEntities().get(0).getItemId());
        assertNotNull(fetchedBibEntity1.getHoldingsEntities().get(0).getBibliographicEntities());
        assertNotNull(fetchedBibEntity2.getHoldingsEntities().get(0).getBibliographicEntities());
        assertTrue(fetchedBibEntity1.getHoldingsEntities().get(0).getBibliographicEntities().size() == 2);
        assertTrue(fetchedBibEntity2.getHoldingsEntities().get(0).getBibliographicEntities().size() == 2);
        assertNotNull(fetchedBibEntity1.getHoldingsEntities().get(0).getItemEntities().get(0).getBibliographicEntities());
        assertNotNull(fetchedBibEntity2.getHoldingsEntities().get(0).getItemEntities().get(0).getBibliographicEntities());
        assertTrue(fetchedBibEntity1.getHoldingsEntities().get(0).getItemEntities().get(0).getBibliographicEntities().size() == 2);
        assertTrue(fetchedBibEntity2.getHoldingsEntities().get(0).getItemEntities().get(0).getBibliographicEntities().size() == 2);
    }

    @Test
    public void saveTwoItemsOneItemWithBadData() throws Exception {
        Random random = new Random();
        BibliographicEntity bibliographicEntity1 = getBibliographicEntity(1, "10001");

        HoldingsEntity holdingsEntity1 = getHoldingsEntity(random, 1);

        bibliographicEntity1.setHoldingsEntities(Arrays.asList(holdingsEntity1));

        ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setCreatedDate(new Date());
        itemEntity1.setCreatedBy("etl");
        itemEntity1.setLastUpdatedDate(new Date());
        itemEntity1.setLastUpdatedBy("etl");
        itemEntity1.setCustomerCode("1");
        itemEntity1.setItemAvailabilityStatusId(1);
        itemEntity1.setOwningInstitutionItemId("101");
        itemEntity1.setOwningInstitutionId(1);
        itemEntity1.setBarcode("00000256");
        itemEntity1.setCallNumber("x.12321");
        itemEntity1.setCollectionGroupId(1);
        itemEntity1.setCallNumberType("1");
        itemEntity1.setCustomerCode("1");
        itemEntity1.setHoldingsEntities(Arrays.asList(holdingsEntity1));

        bibliographicEntity1.setItemEntities(Arrays.asList(itemEntity1));

        BibliographicEntity bibliographicEntity2 = getBibliographicEntity(1, "10002");

        HoldingsEntity holdingsEntity2 = getHoldingsEntity(random, 1);

        bibliographicEntity2.setHoldingsEntities(Arrays.asList(holdingsEntity2));

        ItemEntity itemEntity2 = new ItemEntity();
        itemEntity2.setCreatedDate(new Date());
        itemEntity2.setCreatedBy("etl");
        itemEntity2.setLastUpdatedDate(new Date());
        itemEntity2.setLastUpdatedBy("etl");
        itemEntity2.setItemAvailabilityStatusId(1);
        itemEntity2.setOwningInstitutionItemId("102");
        itemEntity2.setOwningInstitutionId(1);
        itemEntity2.setBarcode("66254");
        itemEntity2.setCallNumber("x.123212");
        itemEntity2.setCollectionGroupId(1);
        itemEntity2.setCallNumberType("1");
        itemEntity2.setCustomerCode("1");
        itemEntity2.setHoldingsEntities(Arrays.asList(holdingsEntity2));

        bibliographicEntity2.setItemEntities(Arrays.asList(itemEntity2));

        BibliographicEntity savedEntity1 = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity1);
        entityManager.refresh(savedEntity1);
        BibliographicEntity savedEntity2 = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity2);
        entityManager.refresh(savedEntity2);

        assertNotNull(savedEntity1);
        assertNotNull(savedEntity1.getBibliographicId());

        assertNotNull(savedEntity2);
        assertNotNull(savedEntity2.getBibliographicId());
    }

    @Test
    public void refreshEntityAfterSave() throws Exception {

        String owningInstitutionBibId = "10001";
        BibliographicEntity bibliographicEntity1 = new BibliographicEntity();
        bibliographicEntity1.setContent("mock Content".getBytes());
        bibliographicEntity1.setCreatedDate(new Date());
        bibliographicEntity1.setLastUpdatedDate(new Date());
        bibliographicEntity1.setOwningInstitutionId(1);
        bibliographicEntity1.setOwningInstitutionBibId(owningInstitutionBibId);
        bibliographicEntity1.setCreatedBy("etl");
        bibliographicEntity1.setLastUpdatedBy("etl");
        BibliographicEntity savedEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity1);
        assertNotNull(savedEntity);
        entityManager.refresh(savedEntity);
        Integer bibliographicId = savedEntity.getBibliographicId();
        assertNotNull(bibliographicId);
        System.out.println("Saved Bib Id : " + bibliographicId);


    }

   // @Ignore
   // @Test
    public void duplicateBibsWithDifferentItems() throws Exception {
        Random random = new Random();
        BibliographicEntity bibliographicEntity1 = new BibliographicEntity();
        bibliographicEntity1.setContent("mock Content".getBytes());
        bibliographicEntity1.setCreatedDate(new Date());
        bibliographicEntity1.setCreatedBy("etl");
        bibliographicEntity1.setLastUpdatedBy("etl");
        bibliographicEntity1.setLastUpdatedDate(new Date());
        bibliographicEntity1.setOwningInstitutionId(1);
        String owningInstitutionBibId = String.valueOf(random.nextInt());
        bibliographicEntity1.setOwningInstitutionBibId(owningInstitutionBibId);

        HoldingsEntity holdingsEntity1 = new HoldingsEntity();
        holdingsEntity1.setContent("mock holdings".getBytes());
        holdingsEntity1.setCreatedDate(new Date());
        holdingsEntity1.setCreatedBy("etl");
        holdingsEntity1.setLastUpdatedDate(new Date());
        holdingsEntity1.setLastUpdatedBy("etl");
        holdingsEntity1.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setLastUpdatedDate(new Date());
        itemEntity1.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity1.setOwningInstitutionId(1);
        itemEntity1.setCreatedDate(new Date());
        itemEntity1.setCreatedBy("etl");
        itemEntity1.setLastUpdatedDate(new Date());
        itemEntity1.setLastUpdatedBy("etl");
        itemEntity1.setBarcode("123");
        itemEntity1.setCallNumber("x.12321");
        itemEntity1.setCollectionGroupId(1);
        itemEntity1.setCallNumberType("1");
        itemEntity1.setCustomerCode("1");
        itemEntity1.setItemAvailabilityStatusId(1);
        itemEntity1.setHoldingsEntities(Arrays.asList(holdingsEntity1));

        bibliographicEntity1.setHoldingsEntities(Arrays.asList(holdingsEntity1));
        bibliographicEntity1.setItemEntities(Arrays.asList(itemEntity1));

        BibliographicEntity savedBibliographicEntity1 = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity1);
        entityManager.refresh(savedBibliographicEntity1);

        BibliographicEntity bibliographicEntity2 = getBibliographicEntity(1, owningInstitutionBibId);

        HoldingsEntity holdingsEntity2 = new HoldingsEntity();
        holdingsEntity2.setContent("mock holdings".getBytes());
        holdingsEntity2.setCreatedDate(new Date());
        holdingsEntity2.setCreatedBy("etl");
        holdingsEntity2.setLastUpdatedDate(new Date());
        holdingsEntity2.setLastUpdatedBy("etl");
        holdingsEntity2.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        ItemEntity itemEntity2 = new ItemEntity();
        itemEntity2.setLastUpdatedDate(new Date());
        itemEntity2.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity2.setOwningInstitutionId(1);
        itemEntity2.setCreatedDate(new Date());
        itemEntity2.setCreatedBy("etl");
        itemEntity2.setLastUpdatedDate(new Date());
        itemEntity2.setLastUpdatedBy("etl");
        itemEntity2.setBarcode("123");
        itemEntity2.setCallNumber("x.12321");
        itemEntity2.setCollectionGroupId(1);
        itemEntity2.setCallNumberType("1");
        itemEntity2.setCustomerCode("1");
        itemEntity2.setItemAvailabilityStatusId(1);
        itemEntity2.setHoldingsEntities(Arrays.asList(holdingsEntity2));

        if (bibliographicEntity2.getHoldingsEntities()==null){
            bibliographicEntity2.setHoldingsEntities(new ArrayList<>());
        }
        bibliographicEntity2.getHoldingsEntities().addAll(Arrays.asList(holdingsEntity2));
        if (bibliographicEntity2.getItemEntities()==null){
            bibliographicEntity2.setItemEntities(new ArrayList<>());
        }
        bibliographicEntity2.getItemEntities().addAll(Arrays.asList(itemEntity2));

        BibliographicEntity savedBibliographicEntity2 = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity2);
        entityManager.refresh(savedBibliographicEntity2);

        BibliographicEntity byOwningInstitutionBibId = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(1, owningInstitutionBibId);
        assertNotNull(byOwningInstitutionBibId);
        assertEquals(byOwningInstitutionBibId.getHoldingsEntities().size(), 2);
        assertEquals(byOwningInstitutionBibId.getItemEntities().size(), 2);

    }

    @Test
    public void duplicateBibWithDifferentHoldingsItemsAndKeepOld() {
        BibliographicEntity bibliographicEntity1 = new BibliographicEntity();
        bibliographicEntity1.setContent("mock Content".getBytes());
        bibliographicEntity1.setCreatedDate(new Date());
        bibliographicEntity1.setCreatedBy("etl");
        bibliographicEntity1.setLastUpdatedBy("etl");
        bibliographicEntity1.setLastUpdatedDate(new Date());
        bibliographicEntity1.setOwningInstitutionId(1);
        String owningInstitutionBibId = "1";
        bibliographicEntity1.setOwningInstitutionBibId(owningInstitutionBibId);

        HoldingsEntity holdingsEntity1 = new HoldingsEntity();
        holdingsEntity1.setContent("mock holdings".getBytes());
        holdingsEntity1.setCreatedDate(new Date());
        holdingsEntity1.setCreatedBy("etl");
        holdingsEntity1.setLastUpdatedDate(new Date());
        holdingsEntity1.setLastUpdatedBy("etl");
        holdingsEntity1.setOwningInstitutionId(1);
        holdingsEntity1.setOwningInstitutionHoldingsId("2");

        ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setLastUpdatedDate(new Date());
        itemEntity1.setOwningInstitutionItemId("3");
        itemEntity1.setOwningInstitutionId(1);
        itemEntity1.setCreatedDate(new Date());
        itemEntity1.setCreatedBy("etl");
        itemEntity1.setLastUpdatedDate(new Date());
        itemEntity1.setLastUpdatedBy("etl");
        itemEntity1.setBarcode("556478");
        itemEntity1.setCallNumber("x.12321");
        itemEntity1.setCollectionGroupId(1);
        itemEntity1.setCallNumberType("1");
        itemEntity1.setCustomerCode("1");
        itemEntity1.setItemAvailabilityStatusId(1);
        itemEntity1.setHoldingsEntities(Arrays.asList(holdingsEntity1));

        bibliographicEntity1.setHoldingsEntities(Arrays.asList(holdingsEntity1));
        bibliographicEntity1.setItemEntities(Arrays.asList(itemEntity1));

        BibliographicEntity savedBibliographicEntity1 = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity1);
        entityManager.refresh(savedBibliographicEntity1);

        BibliographicEntity matchedBib = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(1, owningInstitutionBibId);
        assertNotNull(matchedBib);
        assertEquals(owningInstitutionBibId, matchedBib.getOwningInstitutionBibId());

        HoldingsEntity holdingsEntity2 = new HoldingsEntity();
        holdingsEntity2.setContent("mock holdings".getBytes());
        holdingsEntity2.setCreatedDate(new Date());
        holdingsEntity2.setCreatedBy("etl");
        holdingsEntity2.setLastUpdatedDate(new Date());
        holdingsEntity2.setLastUpdatedBy("etl");
        holdingsEntity2.setOwningInstitutionId(1);
        holdingsEntity2.setOwningInstitutionHoldingsId("4");

        ItemEntity itemEntity2 = new ItemEntity();
        itemEntity2.setLastUpdatedDate(new Date());
        itemEntity2.setOwningInstitutionItemId("5");
        itemEntity2.setOwningInstitutionId(1);
        itemEntity2.setCreatedDate(new Date());
        itemEntity2.setCreatedBy("etl");
        itemEntity2.setLastUpdatedDate(new Date());
        itemEntity2.setLastUpdatedBy("etl");
        itemEntity2.setBarcode("85236");
        itemEntity2.setCallNumber("x.12321");
        itemEntity2.setCollectionGroupId(1);
        itemEntity2.setCallNumberType("1");
        itemEntity2.setCustomerCode("1");
        itemEntity2.setItemAvailabilityStatusId(1);
        itemEntity2.setHoldingsEntities(Arrays.asList(holdingsEntity2));

        matchedBib.getHoldingsEntities().add(holdingsEntity2);
        matchedBib.getItemEntities().add(itemEntity2);

        BibliographicEntity savedBibliographicEntity2 = bibliographicDetailsRepository.saveAndFlush(matchedBib);
        entityManager.refresh(savedBibliographicEntity2);

        BibliographicEntity byOwningInstitutionBibId = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(1, owningInstitutionBibId);
        assertNotNull(byOwningInstitutionBibId);
        assertEquals(byOwningInstitutionBibId.getHoldingsEntities().size(), 2);
        assertEquals(byOwningInstitutionBibId.getItemEntities().size(), 2);
    }

    @Test
    public void boundWithRecordsTest() throws Exception {
        Random random = new Random();

        Integer institutionId = 1;
        String owningInstitutionBibId1 = String.valueOf(random.nextInt());
        String owningInstitutionBibId2 = String.valueOf(random.nextInt());

        BibliographicEntity bibliographicEntity1 = getBibliographicEntity(institutionId, owningInstitutionBibId1);
        HoldingsEntity holdingsEntity = getHoldingsEntity(random, institutionId);
        ItemEntity itemEntity1 = getItemEntity(institutionId, holdingsEntity, "995121");

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity1));
        bibliographicEntity1.setItemEntities(Arrays.asList(itemEntity1));
        bibliographicEntity1.setHoldingsEntities(Arrays.asList(holdingsEntity));

        BibliographicEntity bibliographicEntity2 = getBibliographicEntity(institutionId, owningInstitutionBibId2);
        bibliographicEntity2.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity2.setItemEntities(Arrays.asList(itemEntity1));

        BibliographicEntity savedEntity1 = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity1);
        entityManager.refresh(savedEntity1);
        BibliographicEntity savedEntity2 = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity2);
        entityManager.refresh(savedEntity2);

        assertNotNull(savedEntity1);
        assertNotNull(savedEntity2);

        BibliographicEntity fetchedBibEntity1 = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(institutionId, owningInstitutionBibId1);
        BibliographicEntity fetchedBibEntity2 = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(institutionId, owningInstitutionBibId2);

        assertNotNull(fetchedBibEntity1);
        assertNotNull(fetchedBibEntity2);
        assertEquals(fetchedBibEntity1.getOwningInstitutionBibId(), owningInstitutionBibId1);
        assertEquals(fetchedBibEntity2.getOwningInstitutionBibId(), owningInstitutionBibId2);
        assertNotNull(fetchedBibEntity1.getHoldingsEntities());
        assertNotNull(fetchedBibEntity2.getHoldingsEntities());
        assertTrue(fetchedBibEntity1.getHoldingsEntities().size() == 1);
        assertTrue(fetchedBibEntity2.getHoldingsEntities().size() == 1);
        assertEquals(fetchedBibEntity1.getHoldingsEntities().get(0).getHoldingsId(), fetchedBibEntity2.getHoldingsEntities().get(0).getHoldingsId());
        assertNotNull(fetchedBibEntity1.getHoldingsEntities().get(0).getItemEntities());
        assertNotNull(fetchedBibEntity2.getHoldingsEntities().get(0).getItemEntities());
        assertTrue(fetchedBibEntity1.getHoldingsEntities().get(0).getItemEntities().size() == 1);
        assertTrue(fetchedBibEntity2.getHoldingsEntities().get(0).getItemEntities().size() == 1);
        assertEquals(fetchedBibEntity1.getHoldingsEntities().get(0).getItemEntities().get(0).getItemId(), fetchedBibEntity2.getHoldingsEntities().get(0).getItemEntities().get(0).getItemId());
        assertNotNull(fetchedBibEntity1.getHoldingsEntities().get(0).getBibliographicEntities());
        assertNotNull(fetchedBibEntity2.getHoldingsEntities().get(0).getBibliographicEntities());
        assertTrue(fetchedBibEntity1.getHoldingsEntities().get(0).getBibliographicEntities().size() == 2);
        assertTrue(fetchedBibEntity2.getHoldingsEntities().get(0).getBibliographicEntities().size() == 2);
        assertNotNull(fetchedBibEntity1.getHoldingsEntities().get(0).getItemEntities().get(0).getBibliographicEntities());
        assertNotNull(fetchedBibEntity2.getHoldingsEntities().get(0).getItemEntities().get(0).getBibliographicEntities());
        assertTrue(fetchedBibEntity1.getHoldingsEntities().get(0).getItemEntities().get(0).getBibliographicEntities().size() == 2);
        assertTrue(fetchedBibEntity2.getHoldingsEntities().get(0).getItemEntities().get(0).getBibliographicEntities().size() == 2);
    }

    private ItemEntity getItemEntity(Integer institutionId, HoldingsEntity holdingsEntity, String barcode) {
        ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setCreatedDate(new Date());
        itemEntity1.setCreatedBy("etl");
        itemEntity1.setLastUpdatedDate(new Date());
        itemEntity1.setLastUpdatedBy("etl");
        itemEntity1.setCustomerCode("1");
        itemEntity1.setItemAvailabilityStatusId(1);
        itemEntity1.setOwningInstitutionItemId("101");
        itemEntity1.setOwningInstitutionId(institutionId);
        itemEntity1.setBarcode(barcode);
        itemEntity1.setCallNumber("x.12321");
        itemEntity1.setCollectionGroupId(1);
        itemEntity1.setCallNumberType("1");
        itemEntity1.setHoldingsEntities(Arrays.asList(holdingsEntity));
        return itemEntity1;
    }

    private HoldingsEntity getHoldingsEntity(Random random, Integer institutionId) {
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionId(institutionId);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));
        return holdingsEntity;
    }

    private BibliographicEntity getBibliographicEntity(Integer institutionId, String owningInstitutionBibId1) {
        BibliographicEntity bibliographicEntity1 = new BibliographicEntity();
        bibliographicEntity1.setContent("mock Content".getBytes());
        bibliographicEntity1.setCreatedDate(new Date());
        bibliographicEntity1.setCreatedBy("etl");
        bibliographicEntity1.setLastUpdatedBy("etl");
        bibliographicEntity1.setLastUpdatedDate(new Date());
        bibliographicEntity1.setOwningInstitutionId(institutionId);
        bibliographicEntity1.setOwningInstitutionBibId(owningInstitutionBibId1);
        return bibliographicEntity1;
    }

}