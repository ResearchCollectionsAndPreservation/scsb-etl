package org.recap.model.export;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.repository.BibliographicDetailsRepository;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class DataDumpCallableHelperServiceUT extends BaseTestCaseUT {

    @InjectMocks
    DataDumpCallableHelperService dataDumpCallableHelperService;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    Page<BibliographicEntity> bibliographicEntities;

    @Test
    public void getDeletedRecords() {
        DataDumpRequest dataDumpRequest=new DataDumpRequest();
        dataDumpRequest.setDate(new Date().toString());
        dataDumpRequest.setCollectionGroupIds(Arrays.asList(1,2,3));
        dataDumpRequest.setInstitutionCodes(Arrays.asList("PUL"));
        Mockito.when(bibliographicDetailsRepository.getDeletedRecordsForIncrementalDump(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(bibliographicEntities);
        Mockito.when(bibliographicEntities.getContent()).thenReturn(new ArrayList<>());
        List<BibliographicEntity> bibliographicEntities=dataDumpCallableHelperService.getDeletedRecords(1,1,dataDumpRequest,bibliographicDetailsRepository);
        assertNotNull(bibliographicEntities);
    }

}

