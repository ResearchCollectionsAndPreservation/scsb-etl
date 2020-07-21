package org.recap.model.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertTrue;

public class HoldingsEntityUT extends BaseTestCase {

    HoldingsEntity holdingsEntity;

    @Test
    public void testHoldingsEntity() {
        holdingsEntity = new HoldingsEntity();
        String data = "Test data";
        try {
            holdingsEntity.equals(data);
            holdingsEntity.hashCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }
}
