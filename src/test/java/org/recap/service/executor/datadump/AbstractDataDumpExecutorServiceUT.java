package org.recap.service.executor.datadump;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.model.export.DataDumpRequest;
import org.recap.repository.CollectionGroupDetailsRepository;
import org.recap.service.DataDumpSolrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class AbstractDataDumpExecutorServiceUT {
    AbstractDataDumpExecutorService abstractDataDumpExecutorService;
    @Mock
    CollectionGroupDetailsRepository mockCollectionGroupDetailsRepository;
    @Mock
    DataDumpSolrService mockDataDumpSolrService;

    @Before
    public void setUp() {
        abstractDataDumpExecutorService = Mockito.mock(AbstractDataDumpExecutorService.class, Mockito.CALLS_REAL_METHODS);
        MockitoAnnotations.initMocks(this);
        Map results = new HashMap();
        results.put("totalPageCount",2);
        ReflectionTestUtils.setField(abstractDataDumpExecutorService,"collectionGroupDetailsRepository",mockCollectionGroupDetailsRepository);
        ReflectionTestUtils.setField(abstractDataDumpExecutorService,"dataDumpBatchSize","2");
        ReflectionTestUtils.setField(abstractDataDumpExecutorService,"dataDumpSolrService",mockDataDumpSolrService);
        //ReflectionTestUtils.setField(abstractDataDumpExecutorService,"results",results);
    }

    @Test
    public void testProcess() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException{
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        institutionCodes.add("CUL");
        institutionCodes.add("NYPL");

        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setFetchType("1");
        dataDumpRequest.setOutputFileFormat("Json");
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        dataDumpRequest.setCollectionGroupIds(cgIds);
        try {
            abstractDataDumpExecutorService.process(dataDumpRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
