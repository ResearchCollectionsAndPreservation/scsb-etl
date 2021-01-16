package org.recap.model.export;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.service.DataDumpSolrService;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertNotNull;


public class ImprovedFullDataDumpCallableUT extends BaseTestCaseUT {
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
        bibliographicEntity.setId(100);
        bibliographicEntity.setContent("bib content".getBytes());
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("2");
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setLastUpdatedBy("tst");
        ItemEntity itemEntity=new ItemEntity();
        itemEntity.setId(1);
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));
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
            ReflectionTestUtils.setField(mockImprovedFullDataDumpCallable,"dataDumpSearchResults",dataDumpSearchResults);
            Mockito.when(mockBibliographicDetailsRepository.getBibliographicEntityList(Arrays.asList(9572))).thenReturn(bibliographicEntityList);
            assertNotNull(new ImprovedFullDataDumpCallable(dataDumpSearchResults,mockBibliographicDetailsRepository).call());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
