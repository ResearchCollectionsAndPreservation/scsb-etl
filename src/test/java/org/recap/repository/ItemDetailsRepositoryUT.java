package org.recap.repository;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemPK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by chenchulakshmig on 13/7/16.
 */
public class ItemDetailsRepositoryUT extends BaseTestCase {

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    ItemDetailsRepository itemDetailsRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void saveAndFind() throws Exception {
        assertNotNull(bibliographicDetailsRepository);
        assertNotNull(itemDetailsRepository);
        assertNotNull(entityManager);

        Random random = new Random();
        int owningInstitutionId = 2;

        Long count = itemDetailsRepository.countByOwningInstitutionId(owningInstitutionId);

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content".getBytes());
        Date date = new Date();
        bibliographicEntity.setCreatedDate(date);
        bibliographicEntity.setCreatedBy("etl");
        bibliographicEntity.setLastUpdatedBy("etl");
        bibliographicEntity.setLastUpdatedDate(date);
        bibliographicEntity.setOwningInstitutionId(owningInstitutionId);
        String owningInstitutionBibId = String.valueOf(random.nextInt());
        bibliographicEntity.setOwningInstitutionBibId(owningInstitutionBibId);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(date);
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(date);
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionId(owningInstitutionId);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setCallNumberType("0");
        itemEntity.setCallNumber("callNum");
        itemEntity.setCreatedDate(date);
        itemEntity.setCreatedBy("etl");
        itemEntity.setLastUpdatedDate(date);
        itemEntity.setLastUpdatedBy("etl");
        itemEntity.setBarcode("1231");
        String owningInstitutionItemId = String.valueOf(random.nextInt());
        itemEntity.setOwningInstitutionItemId(owningInstitutionItemId);
        itemEntity.setOwningInstitutionId(owningInstitutionId);
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCustomerCode("PA");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        itemEntity.setUseRestrictions("In Library Use");
        itemEntity.setVolumePartYear("X");
        itemEntity.setCopyNumber(1);

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedEntity);

        assertNotNull(savedEntity);
        assertNotNull(savedEntity.getBibliographicId());
        assertNotNull(savedEntity.getHoldingsEntities().get(0).getHoldingsId());
        ItemEntity savedItemEntity = savedEntity.getItemEntities().get(0);
        assertNotNull(savedItemEntity);
        assertNotNull(savedItemEntity.getItemId());

        Long countAfterAdd = itemDetailsRepository.countByOwningInstitutionId(owningInstitutionId);
        assertTrue(countAfterAdd > count);

        List<ItemEntity> byOwningInstitutionId = itemDetailsRepository.findByOwningInstitutionId(owningInstitutionId);
        assertNotNull(byOwningInstitutionId);
        assertTrue(byOwningInstitutionId.size() > 0);

        ItemEntity byOwningInstitutionItemId = itemDetailsRepository.findByOwningInstitutionItemId(owningInstitutionItemId);
        assertNotNull(byOwningInstitutionItemId);

        Page<ItemEntity> pageByOwningInstitutionId = itemDetailsRepository.findByOwningInstitutionId(PageRequest.of(0, 10), owningInstitutionId);
        assertNotNull(pageByOwningInstitutionId);
        assertTrue(countAfterAdd == pageByOwningInstitutionId.getTotalElements());

        ItemPK itemPK1 = new ItemPK(owningInstitutionId, owningInstitutionItemId);
        Optional<ItemEntity> byItemEntityPK = itemDetailsRepository.findById(itemPK1);
        assertNotNull(byItemEntityPK);
        assertEquals(byItemEntityPK.get().getOwningInstitutionId(), itemPK1.getOwningInstitutionId());
        assertEquals(byItemEntityPK.get().getOwningInstitutionItemId(), itemPK1.getOwningInstitutionItemId());

        ItemPK itemPK2 = new ItemPK();
        itemPK2.setOwningInstitutionId(owningInstitutionId);
        itemPK2.setOwningInstitutionItemId(owningInstitutionItemId);
        Optional<ItemEntity> itemEntityPK = itemDetailsRepository.findById(itemPK2);
        assertNotNull(itemEntityPK);

        assertEquals(itemEntityPK.get().getCallNumberType(), "0");
        assertEquals(itemEntityPK.get().getCallNumber(), "callNum");
        assertEquals(itemEntityPK.get().getCreatedBy(), "etl");
        assertEquals(itemEntityPK.get().getLastUpdatedBy(), "etl");
        assertEquals(itemEntityPK.get().getBarcode(), "1231");
        assertEquals(itemEntityPK.get().getOwningInstitutionItemId(), owningInstitutionItemId);
        assertEquals(itemEntityPK.get().getItemAvailabilityStatusId(), itemEntity.getItemAvailabilityStatusId());
        assertEquals(itemEntityPK.get().getCopyNumber(), itemEntity.getCopyNumber());
        assertEquals(itemEntityPK.get().getVolumePartYear(), itemEntity.getVolumePartYear());
        assertEquals(itemEntityPK.get().getUseRestrictions(), itemEntity.getUseRestrictions());
        assertEquals(itemEntityPK.get().getCustomerCode(), "PA");
        assertNotNull(itemEntityPK.get().getHoldingsEntities());
        assertNotNull(itemEntityPK.get().getInstitutionEntity());
        assertNotNull(itemEntityPK.get().getCollectionGroupEntity());
        assertNotNull(itemEntityPK.get().getItemStatusEntity());
        assertTrue(savedItemEntity.getOwningInstitutionId() == owningInstitutionId);
        assertTrue(savedItemEntity.getCollectionGroupId() == 1);
    }

    @Test
    public void findCountOfBibliographicItems() throws Exception {
        Random random = new Random();
        Long beforeSaveCount = itemDetailsRepository.findCountOfBibliographicItems();

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

        Long afterSaveCount = itemDetailsRepository.findCountOfBibliographicItems();
        assertTrue(afterSaveCount > beforeSaveCount);

        Long countByInstId = itemDetailsRepository.findCountOfBibliographicItemsByInstId(1);
        assertNotNull(countByInstId);

    }

}