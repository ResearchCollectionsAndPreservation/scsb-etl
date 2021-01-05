package org.recap.service.executor.datadump;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.model.export.DataDumpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DataDumpExecutorServiceUT extends BaseTestCaseUT {

    @InjectMocks
    DataDumpExecutorService dataDumpExecutorService;

    @Mock
    DataDumpExecutorInterface dataDumpExecutorInterface;

    @Mock
    IncrementalDataDumpExecutorService incrementalDataDumpExecutorService;

    @Mock
    FullDataDumpExecutorService fullDataDumpExecutorService;

    @Mock
    DeletedDataDumpExecutorService deletedDataDumpExecutorService;


    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testgenerateDataDump() throws InterruptedException, ExecutionException, ParseException {
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
        List<DataDumpExecutorInterface> dataDumpExecutorInterfaceList=new ArrayList<>();
        dataDumpExecutorInterfaceList.add(dataDumpExecutorInterface);
        ReflectionTestUtils.setField(dataDumpExecutorService,"dataDumpExecutorInterfaceList",dataDumpExecutorInterfaceList);
        Mockito.when(dataDumpExecutorInterface.isInterested(Mockito.any())).thenReturn(true);
        Mockito.when(dataDumpExecutorInterface.process(Mockito.any())).thenReturn(RecapCommonConstants.SUCCESS);
        try {
            res = dataDumpExecutorService.generateDataDump(dataDumpRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(RecapCommonConstants.SUCCESS,res);
    }
    @Test
    public void getExecutor(){
        List<DataDumpExecutorInterface> dataDumpExecutorInterfaces=  dataDumpExecutorService.getExecutor();
        assertNotNull(dataDumpExecutorInterfaces);
    }



    }
