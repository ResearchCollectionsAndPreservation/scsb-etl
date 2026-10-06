package org.recap.service.executor.datadump;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.model.export.DataDumpRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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


    @BeforeEach
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
        Mockito.when(dataDumpExecutorInterface.process(Mockito.any())).thenReturn(ScsbCommonConstants.SUCCESS);
        try {
            res = dataDumpExecutorService.generateDataDump(dataDumpRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(ScsbCommonConstants.SUCCESS,res);
    }
    @Test
    public void getExecutor(){
        List<DataDumpExecutorInterface> dataDumpExecutorInterfaces=  dataDumpExecutorService.getExecutor();
        assertNotNull(dataDumpExecutorInterfaces);
    }



    }
