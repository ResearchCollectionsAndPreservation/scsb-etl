package org.recap.model.jpa;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HoldingsEntityUT extends BaseTestCaseUT {

    @Test
    public void testHoldingsEntity() {
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setBibliographicEntities(Arrays.asList(new BibliographicEntity()));
        holdingsEntity.setItemEntities(Arrays.asList(new ItemEntity()));
        holdingsEntity.setInstitutionEntity(new InstitutionEntity());
        holdingsEntity.equals(holdingsEntity);
        String data = "Test data";
        try {
            holdingsEntity.equals(data);
            holdingsEntity.hashCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(holdingsEntity.getItemEntities());
        assertNotNull(holdingsEntity.getBibliographicEntities());
        assertNotNull(holdingsEntity.getInstitutionEntity());

    }
}
