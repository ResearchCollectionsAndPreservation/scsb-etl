package org.recap.service.executor.datadump;

import org.apache.camel.CamelContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.export.DataDumpRequest;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.repository.CollectionGroupDetailsRepository;
import org.recap.service.DataDumpSolrService;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static org.junit.Assert.assertNotNull;


public class AbstractDataDumpExecutorServiceUT extends BaseTestCaseUT {

    private static int httpResonseRecordLimit;
    AbstractDataDumpExecutorService abstractDataDumpExecutorService;
    @Mock
    CollectionGroupDetailsRepository mockCollectionGroupDetailsRepository;
    @Mock
    DataDumpSolrService mockDataDumpSolrService;
    @Mock
    CamelContext mockCamelContext;
    @Mock
    DataDumpSolrService dataDumpSolrService;


    @Before
    public void setUp() {
        SearchRecordsRequest searchRecordsRequest;searchRecordsRequest =  new SearchRecordsRequest();
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        institutionCodes.add("CUL");
        institutionCodes.add("NYPL");
        searchRecordsRequest.setOwningInstitutions(institutionCodes);
        searchRecordsRequest.setCollectionGroupDesignations(null);
        searchRecordsRequest.setPageSize(10000);
        abstractDataDumpExecutorService = Mockito.mock(AbstractDataDumpExecutorService.class, Mockito.CALLS_REAL_METHODS);
        MockitoAnnotations.initMocks(this);
        Map results = new HashMap();
        results.put("totalPageCount", 2);
        results.put("totalRecordsCount",1);
        ReflectionTestUtils.setField(abstractDataDumpExecutorService, "collectionGroupDetailsRepository", mockCollectionGroupDetailsRepository);
        ReflectionTestUtils.setField(abstractDataDumpExecutorService, "dataDumpBatchSize", "2");
        ReflectionTestUtils.setField(abstractDataDumpExecutorService, "dataDumpSolrService", mockDataDumpSolrService);
        ReflectionTestUtils.setField(abstractDataDumpExecutorService, "camelContext", mockCamelContext);
    }

    @Test
    public void testProcess() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        institutionCodes.add("CUL");
        institutionCodes.add("NYPL");

        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setFetchType(RecapConstants.DATADUMP_FETCHTYPE_DELETED);
        dataDumpRequest.setOutputFileFormat("2");
        dataDumpRequest.setDateTimeString(getDateTimeString());
        dataDumpRequest.setTransmissionType("1");
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        ReflectionTestUtils.setField(abstractDataDumpExecutorService, "httpResonseRecordLimit", "1");
        ReflectionTestUtils.invokeMethod(abstractDataDumpExecutorService, "canProcessRecords", 3, "1");

        ReflectionTestUtils.invokeMethod(abstractDataDumpExecutorService, "getFullOrIncrementalDirectory","1");ReflectionTestUtils.invokeMethod(abstractDataDumpExecutorService, "getFullOrIncrementalDirectory","3");
        ReflectionTestUtils.invokeMethod(abstractDataDumpExecutorService, "getFolderName",dataDumpRequest);
        ReflectionTestUtils.invokeMethod(abstractDataDumpExecutorService, "getOutputFormat",dataDumpRequest);
        dataDumpRequest.setOutputFileFormat("0");
        ReflectionTestUtils.invokeMethod(abstractDataDumpExecutorService, "getOutputFormat",dataDumpRequest);
        dataDumpRequest.setOutputFileFormat("1");
        ReflectionTestUtils.invokeMethod(abstractDataDumpExecutorService, "getOutputFormat",dataDumpRequest);
        dataDumpRequest.setOutputFileFormat("4");
        ReflectionTestUtils.invokeMethod(abstractDataDumpExecutorService, "getOutputFormat",dataDumpRequest);
        ReflectionTestUtils.invokeMethod(abstractDataDumpExecutorService, "getFileName",dataDumpRequest,1);
        Map results = new HashMap();
        results.put("totalPageCount", 2);
        results.put("totalRecordsCount", "1");
        List<HashMap> dataDumpSearchResults=new ArrayList<>();
        HashMap hashMap=new HashMap();
        hashMap.put("bibId",1);
        hashMap.put("itemIds", Arrays.asList(1));
        dataDumpSearchResults.add(hashMap);
        results.put("dataDumpSearchResults", dataDumpSearchResults);
        Mockito.when(mockDataDumpSolrService.getResults(Mockito.any())).thenReturn(results);
        List<CollectionGroupEntity> collectionGroupEntityList=new ArrayList<>();
        CollectionGroupEntity collectionGroupEntity=new CollectionGroupEntity();
        collectionGroupEntity.setId(1);
        collectionGroupEntity.setCollectionGroupCode("Shared");
        collectionGroupEntityList.add(collectionGroupEntity);
        Mockito.when(mockCollectionGroupDetailsRepository.findAll()).thenReturn(collectionGroupEntityList);
        HashMap map = new HashMap();
        Map resultsNew = new HashMap();
        resultsNew.put("dataDumpSearchResults", Arrays.asList(map));
        ReflectionTestUtils.invokeMethod(abstractDataDumpExecutorService, "bibHasItems",resultsNew);

        dataDumpRequest.setCollectionGroupIds(cgIds);
        try {
            abstractDataDumpExecutorService.process(dataDumpRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void getFormattedDateString() {
        Calendar cal = Calendar.getInstance();
        System.out.print(cal);
        Date inputDate = cal.getTime();
        DateFormat df = new SimpleDateFormat(RecapCommonConstants.DATE_FORMAT_YYYYMMDDHHMM);
        String inputDateString = df.format(inputDate);
        String dateString=abstractDataDumpExecutorService.getFormattedDateString(inputDateString,inputDateString);
        assertNotNull(dateString);
    }


        private String getDateTimeString() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(RecapConstants.DATE_FORMAT_DDMMMYYYYHHMM);
        return sdf.format(date);
    }
}
