package org.recap.repository;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.InstitutionEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by pvsubrah on 6/22/16.
 */
public class InstitutionDetailsRepositoryUT extends BaseTestCase {

    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;

    @Test
    public void saveAndFind() throws Exception {
        assertNotNull(institutionDetailsRepository);

        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("test");
        institutionEntity.setInstitutionName("test");

        InstitutionEntity savedInstitutionEntity = institutionDetailsRepository.save(institutionEntity);
        assertNotNull(savedInstitutionEntity);
        assertNotNull(savedInstitutionEntity.getId());
        assertEquals(savedInstitutionEntity.getInstitutionCode(), "test");
        assertEquals(savedInstitutionEntity.getInstitutionName(), "test");

        InstitutionEntity byInstitutionCode = institutionDetailsRepository.findByInstitutionCode("test");
        assertNotNull(byInstitutionCode);

        InstitutionEntity byInstitutionName = institutionDetailsRepository.findByInstitutionName("test");
        assertNotNull(byInstitutionName);
    }

    @Test
    public void updateEntity() throws Exception {
        assertNotNull(institutionDetailsRepository);

        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("Princetonn");

        institutionDetailsRepository.save(institutionEntity);

        Optional<InstitutionEntity> savedInstitutionEntity = institutionDetailsRepository.findById(1);
        assertEquals(savedInstitutionEntity.get().getInstitutionName(), institutionEntity.getInstitutionName());
    }

}