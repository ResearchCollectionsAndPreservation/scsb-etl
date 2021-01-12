package org.recap.controller;

import org.apache.camel.ConsumerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.camel.dynamicrouter.DynamicRouteBuilder;
import org.recap.controller.swagger.DataDumpRestController;
import org.recap.model.search.DataDumpSearchResult;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.service.DataDumpSolrService;
import org.recap.service.email.datadump.DataDumpEmailService;
import org.recap.service.executor.datadump.DataDumpExecutorService;
import org.recap.service.preprocessor.DataDumpExportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Created by premkb on 19/8/16.
 */
public class DataDumpRestControllerUT extends BaseControllerUT {

    private static final Logger logger = LoggerFactory.getLogger(DataDumpRestControllerUT.class);

    @InjectMocks
    DataDumpRestController mockedDataDumpRestController;

    @Mock
    DataDumpSolrService mockedDataDumpSolrService;

    @Value("${scsb.solr.doc.url}")
    String solrClientUrl;

    private ExecutorService executorService;

    @Value("${etl.data.dump.status.file.name}")
    String dataDumpStatusFileName;

    @Mock
    RestTemplate mockedRestTemplate;

    @Mock
    private DataDumpExportService mockedDataDumpExportService;

    @Mock
    private DynamicRouteBuilder mockedDynamicRouteBuilder;

    @Mock
    DataDumpExecutorService mockedDataDumpExecutorService;

    @Mock
    DataDumpEmailService mockedDataDumpEmailService;

    @Mock
    ConsumerTemplate mockedConsumerTemplate;



    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(mockedDataDumpSolrService).build();
    }

    @Test
    public void exportIncrementalMarcXmlFormatForHttp() throws Exception {

        String institutionCodes = "CUL";
        String requestingInstitutionCode="NYPL";
        String fetchType = "0";
        String outputFormat = "0";
        String date = new Date().toString();
        String collectionGroupIds = "1,2";
        String transmissionType = "1";
        String emailToAddress = "test@gmail.com";
        Mockito.when(mockedDataDumpExportService.startDataDumpProcess(Mockito.any())).thenReturn(RecapCommonConstants.SUCCESS);
        String response = mockedDataDumpRestController.exportDataDump(institutionCodes,requestingInstitutionCode,fetchType,outputFormat,date, null, collectionGroupIds,transmissionType,emailToAddress,"ims");
        assertNotNull(response);
        assertEquals(RecapCommonConstants.SUCCESS,response);
    }

    @Test
    public void exportIncrementalSCSBXmlFormatForHttp() throws Exception {
        String institutionCodes = "CUL";
        String requestingInstitutionCode="PUL";
        String fetchType = "0";
        String outputFormat = "1";
        String date = new Date().toString();
        String collectionGroupIds = "1,2";
        String transmissionType = "1";
        String emailToAddress = "test@gmail.com";
        Mockito.when(mockedDataDumpExportService.validateIncomingRequest(Mockito.any())).thenReturn(RecapCommonConstants.SUCCESS);
        String response = mockedDataDumpRestController.exportDataDump(institutionCodes,requestingInstitutionCode,fetchType,outputFormat,date, null, collectionGroupIds,transmissionType,emailToAddress,"ims");
        assertNotNull(response);
        assertEquals(RecapCommonConstants.SUCCESS,response);

    }

    @Test
    public void exportFullDataDumpMarcXmlFormat() throws Exception {
        String institutionCodes = "NYPL";
        String requestingInstitutionCode="PUL";
        String fetchType = "0";
        String outputFormat = "0";
        String date = new Date().toString();
        String collectionGroupIds = "1,2";
        String transmissionType = "0";
        String emailToAddress = "test@gmail.com";
        Mockito.when(mockedDataDumpExportService.startDataDumpProcess(Mockito.any())).thenReturn(RecapConstants.DATADUMP_PROCESS_STARTED);
        String response = mockedDataDumpRestController.exportDataDump(institutionCodes,requestingInstitutionCode,fetchType,outputFormat,date, null, collectionGroupIds,transmissionType,emailToAddress,"ims");
        assertNotNull(response);
        assertEquals(RecapConstants.DATADUMP_PROCESS_STARTED,response);
    }


    @Test
    public void exportFullDataDumpScsbXmlFormat() throws Exception {
        String institutionCodes = "NYPL";
        String requestingInstitutionCode="PUL";
        String fetchType = "0";
        String outputFormat = "1";
        String date = new Date().toString();
        String collectionGroupIds = "1,2";
        String transmissionType = "0";
        String emailToAddress = "test@gmail.com";
        Mockito.when(mockedDataDumpExportService.startDataDumpProcess(Mockito.any())).thenReturn(RecapConstants.DATADUMP_PROCESS_STARTED);
        String response = mockedDataDumpRestController.exportDataDump(institutionCodes,requestingInstitutionCode,fetchType,outputFormat,date, null, collectionGroupIds,transmissionType,emailToAddress,"ims");
        assertNotNull(response);
        assertEquals(RecapConstants.DATADUMP_PROCESS_STARTED,response);

    }

    @Test
    public void exportIncrementalDataDump() throws Exception {
        String institutionCodes = "NYPL,PUL";
        String requestingInstitutionCode="NYPL";
        String fetchType = "1";
        String outputFormat = "1";
        String date = new Date().toString();
        String collectionGroupIds = "1,2";
        String transmissionType = "0";
        String emailToAddress = "test@gmail.com";
        Mockito.when(mockedDataDumpExportService.startDataDumpProcess(Mockito.any())).thenReturn(RecapConstants.DATADUMP_PROCESS_STARTED);
        String response = mockedDataDumpRestController.exportDataDump(institutionCodes,requestingInstitutionCode,fetchType,outputFormat,date, null, collectionGroupIds,transmissionType,emailToAddress,"ims");
        assertNotNull(response);
        assertEquals(RecapConstants.DATADUMP_PROCESS_STARTED,response);

    }

    @Test
    public void exportDeletedRecordsDataDump() throws Exception {
        String institutionCodes = "NYPL,PUL";
        String requestingInstitutionCode="NYPL";
        String fetchType = "2";
        String outputFormat = "2";
        String date = new Date().toString();
        String collectionGroupIds = "1,2";
        String transmissionType = "0";
        String emailToAddress = "test@gmail.com";
        Mockito.when(mockedDataDumpExportService.startDataDumpProcess(Mockito.any())).thenReturn(RecapConstants.DATADUMP_PROCESS_STARTED);
        String response = mockedDataDumpRestController.exportDataDump(institutionCodes,requestingInstitutionCode,fetchType,outputFormat,date, null, collectionGroupIds,transmissionType,emailToAddress,"ims");
        assertNotNull(response);
        assertEquals(RecapConstants.DATADUMP_PROCESS_STARTED,response);
    }

    @Test
    public void invalidFetchTypeParameters()throws Exception{
        String institutionCodes = "NYPL";
        String requestingInstitutionCode="NYPL";
        String fetchType = "3";
        String outputFormat = "1";
        String date = new Date().toString();
        String collectionGroupIds = "1,2";
        String transmissionType = "0";
        String emailToAddress = "test@gmail.com";
        Mockito.when(mockedDataDumpExportService.validateIncomingRequest(Mockito.any())).thenReturn(RecapConstants.DATADUMP_VALID_FETCHTYPE_ERR_MSG);
        String response = mockedDataDumpRestController.exportDataDump(institutionCodes,requestingInstitutionCode,fetchType,outputFormat,date, null, collectionGroupIds,transmissionType,emailToAddress,"ims");
        assertNotNull(response);
        assertEquals(RecapConstants.DATADUMP_VALID_FETCHTYPE_ERR_MSG,response);
    }

    @Test
    public void invalidIncrementalDumpParameters()throws Exception{
        String institutionCodes = "NYPL";
        String requestingInstitutionCode="NYPL";
        String fetchType = "1";
        String outputFormat = "1";
        String date = "";
        String collectionGroupIds = "1,2";
        String transmissionType = "0";
        String emailToAddress = "test@gmail.com";
        Mockito.when(mockedDataDumpExportService.validateIncomingRequest(Mockito.any())).thenReturn(RecapConstants.DATADUMP_DATE_ERR_MSG);
        String response = mockedDataDumpRestController.exportDataDump(institutionCodes,requestingInstitutionCode,fetchType,outputFormat,date, null, collectionGroupIds,transmissionType,emailToAddress,"ims");
        assertNotNull(response);
        assertEquals(RecapConstants.DATADUMP_DATE_ERR_MSG,response);

    }

    @Test
    public void getBibsFromSolr() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setOwningInstitutions(Arrays.asList("PUL","CUL"));
        searchRecordsRequest.setCollectionGroupDesignations(Arrays.asList("Shared"));
        searchRecordsRequest.setPageSize(10);
        searchRecordsRequest.setTotalPageCount(1);
        searchRecordsRequest.setTotalRecordsCount("1");
        RestTemplate restTemplate = new RestTemplate();
        String url = solrClientUrl + "searchService/searchRecords";
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key","recap");
        HttpEntity<SearchRecordsRequest> requestEntity = new HttpEntity<>(searchRecordsRequest,headers);

        Map responseMap = new HashMap();
        responseMap.put("totalPageCount", searchRecordsRequest.getTotalPageCount());
        responseMap.put("totalRecordsCount", searchRecordsRequest.getTotalRecordsCount());
        responseMap.put("dataDumpSearchResults", Arrays.asList(new DataDumpSearchResult()));

        ResponseEntity<Map> mapResponseEntity = new ResponseEntity<Map>(responseMap, HttpStatus.OK);

        Mockito.when(mockedRestTemplate.postForEntity(url, requestEntity, Map.class)).thenReturn(mapResponseEntity);

        ResponseEntity<Map> responseEntity = mockedRestTemplate.postForEntity(url, requestEntity, Map.class);
        assertTrue(responseEntity.getStatusCode().getReasonPhrase().equalsIgnoreCase("OK"));
        Map responseEntityBody = responseEntity.getBody();
        Integer totalPageCount = (Integer) responseEntityBody.get("totalPageCount");
        String totalBibsCount = (String) responseEntityBody.get("totalRecordsCount");
        List dataDumpSearchResults = (List) responseEntityBody.get("dataDumpSearchResults");
        assertNotNull(totalPageCount);
        assertNotNull(totalBibsCount);
        assertNotNull(dataDumpSearchResults);
        System.out.println("Total Pages : " + totalPageCount);
        System.out.println("Total Bibs : " + totalBibsCount);
        stopWatch.stop();
        System.out.println("Total Time Taken : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void concurrentHttpDataExport() throws Exception {
        List<Callable<Map<String, String>>> callables = new ArrayList<>();

        for(int i=0; i<5; i++) {
            ConcurrentDataExportCallable concurrentDataExportCallable = new ConcurrentDataExportCallable(mockMvc, "2", "2", "1");
            callables.add(concurrentDataExportCallable);
        }

        List<Future<Map<String, String>>> futures = getFutures(callables);

        List<MvcResult> results = getResults(futures);
        for(MvcResult mvcResult : results) {
            logger.info(mvcResult.getResponse().getContentAsString());
            assertNotNull(mvcResult.getResponse().getContentAsString());
        }
    }

    @Test
    public void concurrentFullDataExport() throws Exception {
        List<Callable<Map<String, String>>> callables = new ArrayList<>();

        for(int i=0; i<5; i++) {
            ConcurrentDataExportCallable concurrentDataExportCallable = new ConcurrentDataExportCallable(mockMvc, "0", "0", "0");
            callables.add(concurrentDataExportCallable);
        }

        List<Future<Map<String, String>>> futures = getFutures(callables);

        List<MvcResult> results = getResults(futures);
        for(MvcResult result : results) {
            logger.info(result.getResponse().getContentAsString());
            assertNotNull(result.getResponse().getContentAsString());
        }
        File file = new File(dataDumpStatusFileName);
        file.delete();
    }

    @Test
    public void testDataDumpSearchResult(){
        DataDumpSearchResult dataDumpSearchResult = new DataDumpSearchResult();
        List<Integer> itemIdList = new ArrayList<>();
        itemIdList.add(1);
        dataDumpSearchResult.setBibId(1);
        dataDumpSearchResult.setItemIds(itemIdList);
        assertNotNull(dataDumpSearchResult.getBibId());
        assertNotNull(dataDumpSearchResult.getItemIds());
    }

    private List<MvcResult> getResults(List<Future<Map<String, String>>> futures) {
        List<MvcResult> mvcResults = new ArrayList<>();
        for (Iterator<Future<Map<String, String>>> iterator = futures.iterator(); iterator.hasNext(); ) {
            Future future = iterator.next();
            Object object = null;
            try {
                object = future.get();
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            } catch (ExecutionException e) {
                logger.error(e.getMessage());
            }
            MvcResult mvcResult = (MvcResult) object;
            mvcResults.add(mvcResult);
        }
        return mvcResults;
    }

    private List<Future<Map<String, String>>> getFutures(List<Callable<Map<String, String>>> callables) {
        List<Future<Map<String, String>>> futures = null;
        try {
            futures = getExecutorService().invokeAll(callables);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        futures
                .stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                });
        return futures;
    }

    public class ConcurrentDataExportCallable implements Callable {

        protected MockMvc mockMvc;
        private String fetchType;
        private String outputFormat;
        private String transmissionType;

        public ConcurrentDataExportCallable(MockMvc mockMvc, String fetchType, String outputFormat, String transmissionType) {
            this.mockMvc = mockMvc;
            this.fetchType = fetchType;
            this.outputFormat = outputFormat;
            this.transmissionType = transmissionType;
        }

        @Override
        public Object call() throws Exception {

            MvcResult mvcResult = this.mockMvc.perform(get("/dataDump/exportDataDump")
                    .param("fetchType",fetchType)
                    .param("requestingInstitutionCode","NYPL")
                    .param("outputFormat",outputFormat)
                    .param("emailToAddress","peri.subrahmanya@gmail.com")
                    .param("institutionCodes","CUL")
                    .param("transmissionType",transmissionType))
                    .andReturn();
            return mvcResult;
        }
    }

    public ExecutorService getExecutorService() {
        if (null == executorService || executorService.isShutdown()) {
            executorService = Executors.newFixedThreadPool(50);
        }
        return executorService;
    }

    public String getFormattedString(String dateStr){
        String formattedString = dateStr.substring(0,10)+"T"+dateStr.substring(11,16)+":00Z TO NOW";
        return formattedString;
    }

}
