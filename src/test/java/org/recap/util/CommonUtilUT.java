package org.recap.util;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.repository.InstitutionDetailsRepository;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by rajeshbabuk on 11/May/2021
 */
public class CommonUtilUT extends BaseTestCaseUT {

    @Value("${scsb.support.institution}")
    private String supportInstitution;

    @InjectMocks
    private CommonUtil commonUtil;

    @Mock
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Test
    public void findAllInstitutionCodesExceptSupportInstitution() {
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(institutionDetailsRepository.findAllInstitutionCodesExceptSupportInstitution(supportInstitution)).thenReturn(Collections.singletonList(institutionEntity.getInstitutionName()));
        List<String> result = commonUtil.findAllInstitutionCodesExceptSupportInstitution();
        assertNotNull(result);
    }

    @Test
    public void findAllInstitutionsExceptSupportInstitution() {
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(institutionDetailsRepository.findAllInstitutionsExceptSupportInstitution(supportInstitution)).thenReturn(Collections.singletonList(institutionEntity));
        List<InstitutionEntity> result = commonUtil.findAllInstitutionsExceptSupportInstitution();
        assertNotNull(result);
    }

    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");
        return institutionEntity;
    }
}
