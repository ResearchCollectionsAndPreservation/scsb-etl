package org.recap.camel;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.repository.HoldingsDetailsRepository;
import org.recap.repository.ItemDetailsRepository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by hemalathas on 25/7/17.
 */
public class EtlDataLoadDAOServiceUT extends BaseTestCaseUT {

    @InjectMocks
    EtlDataLoadDAOService etlDataLoadDAOService;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    HoldingsDetailsRepository holdingsDetailsRepository;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    EntityManager entityManager;

    @Test
    public void testSavedHoldingsEntity(){
        Random random = new Random();
        Mockito.when(holdingsDetailsRepository.save(Mockito.any())).thenReturn(getHoldingsEntity(random, 1));
        HoldingsEntity holdingsEntity = etlDataLoadDAOService.savedHoldingsEntity(getHoldingsEntity(random, 1));
        assertNotNull(holdingsEntity);
    }

    @Test
    public void testItemEntity(){
        Mockito.when(itemDetailsRepository.save(Mockito.any())).thenReturn(getItemEntity());
        ItemEntity savedItemEntity = etlDataLoadDAOService.saveItemEntity(getItemEntity());
        assertNotNull(savedItemEntity);
    }

    @Test
    public void saveBibliographicEntity(){
        BibliographicEntity bibliographicEntity=new BibliographicEntity();
        Mockito.when(bibliographicDetailsRepository.save(Mockito.any())).thenReturn(bibliographicEntity);
        etlDataLoadDAOService.saveBibliographicEntity(bibliographicEntity);
        List<BibliographicEntity> bibliographicEntityList=new ArrayList<>();
        bibliographicEntityList.add(bibliographicEntity);
        Mockito.when(bibliographicDetailsRepository.saveAll(Mockito.any())).thenReturn(bibliographicEntityList);
        etlDataLoadDAOService.saveBibliographicEntityList(bibliographicEntityList);
        etlDataLoadDAOService.clearSession();
        assertTrue(true);
    }

    private ItemEntity getItemEntity() {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setCallNumberType("0");
        itemEntity.setCallNumber("callNum");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("etl");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("etl");
        itemEntity.setBarcode("33201457127");
        itemEntity.setOwningInstitutionItemId(".i1231");
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCustomerCode("PA");
        itemEntity.setItemAvailabilityStatusId(1);
        return itemEntity;
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

}