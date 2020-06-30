package org.recap.service.executor.datadump;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.model.export.DataDumpRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertNull;

public class DataDumpExecutorServiceUT extends BaseTestCase {
    @Mock
    DataDumpExecutorService dataDumpExecutorService;

    @Autowired
    IncrementalDataDumpExecutorService incrementalDataDumpExecutorService;

    @Autowired
    FullDataDumpExecutorService fullDataDumpExecutorService;

    @Autowired
    DeletedDataDumpExecutorService deletedDataDumpExecutorService;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testgenerateDataDump() throws InterruptedException, ExecutionException, ParseException {
        List<DataDumpExecutorInterface> dataDumpExecutorInterfaceList=null;
       // DataDumpExecutorService dataDumpExecutorService = new DataDumpExecutorService();
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setFetchType("1");
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        LocalDate date = LocalDate.now();
        dataDumpRequest.setCollectionGroupIds(cgIds);
        String res = dataDumpExecutorService.generateDataDump(dataDumpRequest);
        assertNull(res);
    }
}
