package org.recap.service.formatter.datadump;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapCommonConstants;
import org.recap.model.export.DeletedRecord;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.BibliographicDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * Created by premkb on 29/9/16.
 */
public class DeletedJsonFormatterServiceUT extends BaseTestCase{

    @Autowired
    private DeletedJsonFormatterService deletedJsonFormatterService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    @Test
    public void getFormattedOutput() throws Exception {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setBarcode("1234");
        itemEntity.setItemId(1);
        itemEntity.setCustomerCode("1234");
        itemEntity.setCallNumber("1234");
        itemEntity.setCallNumberType("land");
        itemEntity.setItemAvailabilityStatusId(123);
        Map<String,Object> successAndFailureFormattedList = deletedJsonFormatterService.prepareDeletedRecords(getBibliographicEntityList());
        List<DeletedRecord> deletedRecordList = (List<DeletedRecord>)successAndFailureFormattedList.get(RecapCommonConstants.SUCCESS);
        String outputString = (String) deletedJsonFormatterService.getJsonForDeletedRecords(deletedRecordList);
        ReflectionTestUtils.invokeMethod(deletedJsonFormatterService,"isChangedToPrivateCGD",itemEntity);
        assertNotNull(outputString);
    }

    private List<BibliographicEntity> getBibliographicEntityList() throws URISyntaxException, IOException {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setBibliographicId(100);
        bibliographicEntity.setContent("bib content".getBytes());
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("2");
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setLastUpdatedBy("tst");

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("holding content".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId("3");

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId("5");
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("test");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("test");
        itemEntity.setBarcode("330320145");
        itemEntity.setCallNumber("x.123421");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("1");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setCopyNumber(1234);
        itemEntity.setDeleted(true);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setLastUpdatedDate(new Date());
        itemEntity1.setOwningInstitutionItemId("4");
        itemEntity1.setOwningInstitutionId(1);
        itemEntity1.setCreatedDate(new Date());
        itemEntity1.setCreatedBy("tst");
        itemEntity1.setLastUpdatedDate(new Date());
        itemEntity1.setLastUpdatedBy("tst");
        itemEntity1.setBarcode("3456");
        itemEntity1.setCallNumber("x.12321");
        itemEntity1.setCollectionGroupId(1);
        itemEntity1.setCallNumberType("1");
        itemEntity1.setCustomerCode("1");
        itemEntity1.setItemAvailabilityStatusId(1);
        itemEntity1.setCopyNumber(123);
        itemEntity1.setDeleted(true);
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity1,itemEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        BibliographicEntity savedEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedEntity);

        return Arrays.asList(savedEntity);
    }

}
