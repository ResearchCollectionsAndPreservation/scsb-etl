package org.recap.service.executor.datadump;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.model.export.DataDumpRequest;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.repository.CollectionGroupDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.when;

public class AbstractDataDumpExecutorServiceUT {
    @Mock
    CollectionGroupEntity collectionGroupEntity;

    @Mock
    CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    AbstractDataDumpExecutorService abstractDataDumpExecutorService;

    @Before
    public void setUp() {
        abstractDataDumpExecutorService = Mockito.mock(AbstractDataDumpExecutorService.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void testProcess() {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        institutionCodes.add("CUL");
        institutionCodes.add("NYPL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setFetchType("1");
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        dataDumpRequest.setCollectionGroupIds(cgIds);
        Iterable<CollectionGroupEntity>  list = new Iterable<CollectionGroupEntity>() {
            @Override
            public Iterator<CollectionGroupEntity> iterator() {
                return null;
            }
        };
        try {
            //when(collectionGroupDetailsRepository.findAll()).thenReturn((List<CollectionGroupEntity>) list);
            abstractDataDumpExecutorService.process(dataDumpRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
