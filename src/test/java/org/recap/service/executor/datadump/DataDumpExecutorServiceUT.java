package org.recap.service.executor.datadump;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.model.export.DataDumpRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class DataDumpExecutorServiceUT extends BaseTestCase {
    @Autowired
    DataDumpExecutorService dataDumpExecutorService;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testgenerateDataDump() {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setFetchType("1");
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        dataDumpRequest.setCollectionGroupIds(cgIds);
        String res = "test data";
        dataDumpRequest.setDate(new Date().toString());
        dataDumpRequest.setToDate(new Date().toString());
        try {
            res = dataDumpExecutorService.generateDataDump(dataDumpRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(res);
    }
}
