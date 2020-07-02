package org.recap.model.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.repository.EtlGfaRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 21/7/16.
 */
public class EtlGfaEntityUT extends BaseTestCase {

    @Autowired
    EtlGfaRepository etlGfaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void testSaveAndFetchEtlGfa(){
        EtlGfaEntity etlGfaEntity = new EtlGfaEntity();
        etlGfaEntity.setItemBarcode("3210457796");
        etlGfaEntity.setCustomer("GP");
        etlGfaEntity.setStatus("Out on Ret WO: 387966 09/29/15 To PA");

        EtlGfaEntity savedEtlGfa = etlGfaRepository.saveAndFlush(etlGfaEntity);
        entityManager.refresh(savedEtlGfa);

        List<EtlGfaEntity> etlGfaEntityList = etlGfaRepository.findBystatus("Out on Ret WO: 387966 09/29/15 To PA");
        List<EtlGfaEntity> etlGfaEntityListByBarcode = etlGfaRepository.findByItemBarcode("3210457796");
        assertNotNull(savedEtlGfa);
        assertNotNull(etlGfaEntityList);
        EtlGfaEntity savedEtlGfaEntity = etlGfaEntityList.get(0);
        assertEquals(savedEtlGfaEntity.getStatus(),"Out on Ret WO: 387966 09/29/15 To PA");
        assertEquals(savedEtlGfaEntity.getCustomer(), etlGfaEntity.getCustomer());
        assertNotNull(etlGfaEntityListByBarcode);
        assertEquals(etlGfaEntityListByBarcode.get(0).getItemBarcode(),"3210457796");

    }

}