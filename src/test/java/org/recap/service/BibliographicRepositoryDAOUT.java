package org.recap.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
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

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;

public class BibliographicRepositoryDAOUT extends BaseTestCaseUT {

    @InjectMocks
    BibliographicRepositoryDAO bibliographicRepositoryDAO;

    @Mock
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    private HoldingsDetailsRepository holdingsDetailsRepository;

    @Mock
    private ItemDetailsRepository itemDetailsRepository;

    @Mock
    private EntityManager entityManager;

    @Test
    public void save(){
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        HoldingsEntity holdingsEntity = getHoldingsEntity();
        ItemEntity itemEntity = getBibliographicEntity().getItemEntities().get(0);
        Mockito.when(bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(bibliographicEntity.getOwningInstitutionId(), bibliographicEntity.getOwningInstitutionBibId())).thenReturn(null);
        Mockito.when( holdingsDetailsRepository.findByOwningInstitutionHoldingsIdAndOwningInstitutionId(holdingsEntity.getOwningInstitutionHoldingsId(), holdingsEntity.getOwningInstitutionId())).thenReturn(null);
        Mockito.when(itemDetailsRepository.findByOwningInstitutionItemIdAndOwningInstitutionId(itemEntity.getOwningInstitutionItemId(), itemEntity.getOwningInstitutionId())).thenReturn(null);
        Mockito.when(bibliographicDetailsRepository.save(bibliographicEntity)).thenReturn(bibliographicEntity);
        BibliographicEntity entity = bibliographicRepositoryDAO.saveOrUpdate(bibliographicEntity);
        assertNotNull(entity);
    }

    @Test
    public void saveOrUpdateList(){
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        HoldingsEntity holdingsEntity = getHoldingsEntity();
        ItemEntity itemEntity = getBibliographicEntity().getItemEntities().get(0);
        Mockito.when(bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(bibliographicEntity.getOwningInstitutionId(), bibliographicEntity.getOwningInstitutionBibId())).thenReturn(bibliographicEntity);
        Mockito.when(holdingsDetailsRepository.findByOwningInstitutionHoldingsIdAndOwningInstitutionId(holdingsEntity.getOwningInstitutionHoldingsId(), holdingsEntity.getOwningInstitutionId())).thenReturn(holdingsEntity);
        Mockito.when(itemDetailsRepository.findByOwningInstitutionItemIdAndOwningInstitutionId(itemEntity.getOwningInstitutionItemId(), itemEntity.getOwningInstitutionId())).thenReturn(itemEntity);
        List<BibliographicEntity> bibliographicEntities = bibliographicRepositoryDAO.saveOrUpdateList(Arrays.asList(bibliographicEntity));
        assertNotNull(bibliographicEntities);
    }

    @Test
    public void saveHoldingsEntity(){
        HoldingsEntity holdingsEntity = getHoldingsEntity();
        Mockito.when(holdingsDetailsRepository.findByOwningInstitutionHoldingsIdAndOwningInstitutionId(holdingsEntity.getOwningInstitutionHoldingsId(), holdingsEntity.getOwningInstitutionId())).thenReturn(null);
        Mockito.when(holdingsDetailsRepository.save(holdingsEntity)).thenReturn(holdingsEntity);
        HoldingsEntity entity = bibliographicRepositoryDAO.saveOrUpdateHoldingsEntity(holdingsEntity);
        assertNotNull(entity);
    }

    @Test
    public void updateHoldingsEntity(){
        HoldingsEntity holdingsEntity = getHoldingsEntity();
        Mockito.when(holdingsDetailsRepository.findByOwningInstitutionHoldingsIdAndOwningInstitutionId(holdingsEntity.getOwningInstitutionHoldingsId(), holdingsEntity.getOwningInstitutionId())).thenReturn(holdingsEntity);
        Mockito.when(entityManager.merge(any())).thenReturn(holdingsEntity);
        HoldingsEntity entity = bibliographicRepositoryDAO.saveOrUpdateHoldingsEntity(holdingsEntity);
        assertNull(entity);
    }

    @Test
    public void saveItemEntity(){
        ItemEntity itemEntity = getBibliographicEntity().getItemEntities().get(0);
        Mockito.when(itemDetailsRepository.findByOwningInstitutionItemIdAndOwningInstitutionId(itemEntity.getOwningInstitutionItemId(), itemEntity.getOwningInstitutionId())).thenReturn(null);
        Mockito.when(itemDetailsRepository.save(itemEntity)).thenReturn(itemEntity);
        ItemEntity entity = bibliographicRepositoryDAO.saveOrUpdateItemEntity(itemEntity);
        assertNotNull(entity);
    }

    @Test
    public void updateItemEntity(){
        ItemEntity itemEntity = getBibliographicEntity().getItemEntities().get(0);
        Mockito.when(itemDetailsRepository.findByOwningInstitutionItemIdAndOwningInstitutionId(itemEntity.getOwningInstitutionItemId(), itemEntity.getOwningInstitutionId())).thenReturn(itemEntity);
        Mockito.when(entityManager.merge(itemEntity)).thenReturn(itemEntity);
        ItemEntity entity = bibliographicRepositoryDAO.saveOrUpdateItemEntity(itemEntity);
        assertNotNull(entity);
    }


    private BibliographicEntity getBibliographicEntity() {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("test".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("etl");
        bibliographicEntity.setLastUpdatedBy("etl");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("001");
        List<BibliographicEntity> bibliographicEntitylist = new LinkedList(Arrays.asList(bibliographicEntity));

        HoldingsEntity holdingsEntity = getHoldingsEntity();
        List<HoldingsEntity> holdingsEntitylist = new LinkedList(Arrays.asList(holdingsEntity));

        ItemEntity itemEntity = getItemEntity();
        List<ItemEntity> itemEntitylist = new LinkedList(Arrays.asList(itemEntity));


        holdingsEntity.setBibliographicEntities(bibliographicEntitylist);
        holdingsEntity.setItemEntities(itemEntitylist);
        bibliographicEntity.setHoldingsEntities(holdingsEntitylist);
        bibliographicEntity.setItemEntities(itemEntitylist);
        itemEntity.setHoldingsEntities(holdingsEntitylist);
        itemEntity.setBibliographicEntities(bibliographicEntitylist);

        return bibliographicEntity;
    }

    private ItemEntity getItemEntity() {
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
        return itemEntity;
    }

    private HoldingsEntity getHoldingsEntity() {
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionHoldingsId("002");
        holdingsEntity.setItemEntities(Arrays.asList(getItemEntity()));
        return holdingsEntity;
    }


}
