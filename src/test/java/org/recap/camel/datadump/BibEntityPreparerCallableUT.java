package org.recap.camel.datadump;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;
import org.recap.camel.datadump.callable.BibEntityPreparerCallable;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.ItemEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BibEntityPreparerCallableUT extends BaseTestCaseUT {

    BibEntityPreparerCallable bibEntityPreparerCallable;

    BibliographicEntity bibliographicEntity;

    @BeforeEach
    public void before() {
        bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setId(100);
        bibliographicEntity.setContent("bib content".getBytes());
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("2");
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setLastUpdatedBy("tst");
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setBarcode("1234");
        itemEntity.setId(1);
        itemEntity.setCustomerCode("1234");
        itemEntity.setCallNumber("1234");
        itemEntity.setCallNumberType("land");
        itemEntity.setItemAvailabilityStatusId(123);
        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(itemEntity);
        bibliographicEntity.setItemEntities(itemEntities);
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        bibEntityPreparerCallable = new BibEntityPreparerCallable(bibliographicEntity, list);
    }

    @Test
    public void tesCall() throws Exception {
        bibliographicEntity = bibEntityPreparerCallable.call();
        assertNotNull(bibliographicEntity);
    }
}
