package org.recap.model.export;

import org.junit.Before;
import org.junit.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.service.DataDumpSolrService;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
public class ImprovedFullDataDumpCallableUT {
    @InjectMocks
    ImprovedFullDataDumpCallable mockImprovedFullDataDumpCallable;
    @Mock
    DataDumpSolrService mockedDataDumpSolrService;
    @Mock
    BibliographicDetailsRepository mockBibliographicDetailsRepository;
    List<BibliographicEntity> bibliographicEntityList;
    @Before
    public void init() {
        bibliographicEntityList = new ArrayList<>();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setBibliographicId(100);
        bibliographicEntity.setContent("bib content".getBytes());
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("2");
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntityList.add(bibliographicEntity);
        MockitoAnnotations.initMocks(this);
        try {
            Mockito.when(mockImprovedFullDataDumpCallable.call()).thenReturn("Test Data");
            Mockito.when(mockBibliographicDetailsRepository.getBibliographicEntityList(Arrays.asList(12))).thenReturn(bibliographicEntityList);
            PowerMockito.whenNew(ImprovedFullDataDumpCallable.class).withArguments(Mockito.any(), Mockito.any()).thenReturn(mockImprovedFullDataDumpCallable);
            ReflectionTestUtils.setField(mockImprovedFullDataDumpCallable,"bibliographicEntityList",bibliographicEntityList);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Test
    public void testCall() {
        List<LinkedHashMap> dataDumpSearchResults = new ArrayList<>();
        List<Integer> data = new ArrayList<>();
        data.add(1);
        data.add(2);
        LinkedHashMap<String,Object> mapData = new LinkedHashMap<>();
        mapData.put("bibId",9572);
        mapData.put("itemIds",data);
        dataDumpSearchResults.add(mapData);
        try {
            Mockito.when(mockBibliographicDetailsRepository.getBibliographicEntityList(Arrays.asList(9572))).thenReturn(bibliographicEntityList);
            assertEquals(new ImprovedFullDataDumpCallable(dataDumpSearchResults,mockBibliographicDetailsRepository).call(), "Test Data");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
