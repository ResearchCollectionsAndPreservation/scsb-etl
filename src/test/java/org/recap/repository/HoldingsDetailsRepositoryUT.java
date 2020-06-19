package org.recap.repository;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.HoldingsPK;
import org.recap.model.jpa.ItemEntity;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by angelind on 29/7/16.
 */
public class HoldingsDetailsRepositoryUT extends BaseTestCase{

    @Autowired
    HoldingsDetailsRepository holdingsDetailsRepository;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void saveAndFind() throws Exception {
        assertNotNull(bibliographicDetailsRepository);
        assertNotNull(holdingsDetailsRepository);
        assertNotNull(entityManager);

        Random random = new Random();
        int owningInstitutionId = 2;

        Long count = holdingsDetailsRepository.count();

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
        String owningInstitutionHoldingsId = String.valueOf(random.nextInt());
        holdingsEntity.setOwningInstitutionHoldingsId(owningInstitutionHoldingsId);

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

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));
        holdingsEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));

        BibliographicEntity savedEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedEntity);

        assertNotNull(savedEntity);
        assertNotNull(savedEntity.getBibliographicId());
        HoldingsEntity savedHoldingsEntity = savedEntity.getHoldingsEntities().get(0);
        assertNotNull(savedHoldingsEntity);
        assertNotNull(savedHoldingsEntity.getHoldingsId());

        Long countAfterAdd = holdingsDetailsRepository.count();
        assertTrue(countAfterAdd > count);

        HoldingsEntity holdingsEntityByPK = holdingsDetailsRepository.findByHoldingsId(savedHoldingsEntity.getHoldingsId());
        assertNotNull(holdingsEntityByPK.getContent());
        assertNotNull(holdingsEntityByPK.getCreatedDate());
        assertNotNull(holdingsEntityByPK.getLastUpdatedDate());
        assertNotNull(holdingsEntityByPK.getBibliographicEntities());
        assertNotNull(holdingsEntityByPK.getItemEntities());
        assertEquals(holdingsEntityByPK.getCreatedBy(), holdingsEntity.getCreatedBy());
        assertEquals(holdingsEntityByPK.getLastUpdatedBy(), holdingsEntity.getLastUpdatedBy());
    }

    @Test
    public void testHoldingPK(){
        HoldingsPK holdingsPK = new HoldingsPK();
        HoldingsPK holdingsPK1 = new HoldingsPK(1,"45656456456");
        assertNotNull(holdingsPK1.getOwningInstitutionHoldingsId());
        assertNotNull(holdingsPK1.getOwningInstitutionId());
    }

}