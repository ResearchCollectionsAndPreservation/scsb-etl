package org.recap.model.jpa;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImsLocationEntityUT extends BaseTestCaseUT {

    @Test
    public void testImsLocationEntity() {

        ImsLocationEntity imsLocationEntity = new ImsLocationEntity();
        ImsLocationEntity imsLocationEntity1 = new ImsLocationEntity();
        imsLocationEntity.setId(1);
        imsLocationEntity.setImsLocationCode("HD");
        imsLocationEntity.setCreatedBy("test");
        imsLocationEntity.setUpdatedBy("admin");
        imsLocationEntity.setImsLocationName("HD");
        imsLocationEntity.setDescription("Desc");
        imsLocationEntity.setCreatedDate(new Date());
        imsLocationEntity.setUpdatedDate(new Date());
        imsLocationEntity.setActive(true);
        imsLocationEntity.equals(null);
        imsLocationEntity.equals(imsLocationEntity);
        imsLocationEntity.equals(imsLocationEntity1);
        imsLocationEntity.hashCode();
        imsLocationEntity1.hashCode();

        assertNotNull(imsLocationEntity.getId());
        assertNotNull(imsLocationEntity.getImsLocationCode());
        assertNotNull(imsLocationEntity.getImsLocationName());
        assertNotNull(imsLocationEntity.getUpdatedDate());
        assertNotNull(imsLocationEntity.getCreatedDate());
        assertNotNull(imsLocationEntity.getDescription());
        assertNotNull(imsLocationEntity.getUpdatedBy());
        assertNotNull(imsLocationEntity.getCreatedBy());
        assertTrue(imsLocationEntity.isActive());
    }
}
