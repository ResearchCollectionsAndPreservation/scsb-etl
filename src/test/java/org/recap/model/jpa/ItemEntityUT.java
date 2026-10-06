package org.recap.model.jpa;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemEntityUT extends BaseTestCaseUT {

    @Test
    public void getItemEntity() {

        ItemEntity itemEntity = new ItemEntity();

        itemEntity.setOwningInstitutionItemId("1");
        itemEntity.setImsLocationEntity(new ImsLocationEntity());
        itemEntity.setInstitutionEntity(new InstitutionEntity());
        itemEntity.setItemStatusEntity(new ItemStatusEntity());
        itemEntity.setBibliographicEntities(Arrays.asList(new BibliographicEntity()));
        itemEntity.setCgdProtection(true);
        itemEntity.setCreatedBy("test");
        itemEntity.setCgdChangeLog("cgd");
        itemEntity.setCollectionGroupEntity(new CollectionGroupEntity());
        itemEntity.setInitialMatchingDate(new Date());

        assertNotNull(itemEntity.getItemStatusEntity());
        assertNotNull(itemEntity.getBibliographicEntities());
        assertNotNull(itemEntity.getInstitutionEntity());
        assertNotNull(itemEntity.getImsLocationEntity());
        assertNotNull(itemEntity.getCollectionGroupEntity());
        assertNotNull(itemEntity.getCgdChangeLog());
        assertTrue(itemEntity.isCgdProtection());
        assertNotNull(itemEntity.getInitialMatchingDate());
    }
}
