package org.recap.model.jpa;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by hemalathas on 21/7/16.
 */

public class EtlGfaEntityUT extends BaseTestCaseUT {

    @Test
    public void testSaveAndFetchEtlGfa() {
        EtlGfaEntity etlGfaEntity = new EtlGfaEntity();
        etlGfaEntity.setItemBarcode("3210457796");
        etlGfaEntity.setCustomer("GP");
        etlGfaEntity.setStatus("Out on Ret WO: 387966 09/29/15 To PA");

        assertNotNull(etlGfaEntity.getCustomer());
        assertNotNull(etlGfaEntity.getStatus());
        assertNotNull(etlGfaEntity.getItemBarcode());
    }

}
