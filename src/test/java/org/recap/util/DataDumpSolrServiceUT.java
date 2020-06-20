package org.recap.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.service.DataDumpSolrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * Created by peris on 10/26/16.
 */
public class DataDumpSolrServiceUT extends BaseTestCase {

    @Autowired
    DataDumpSolrService dataDumpSolrService;

    @Mock
    DataDumpSolrService mockedDataDumpSolrService;

    @Value("${solrclient.url}")
    String solrClientUrl;

    @Mock
    RestTemplate mockRestTemplate;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void fetchResultsFromSolrForDataDump() throws Exception {

        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setOwningInstitutions(Arrays.asList("PUL","CUL"));
        searchRecordsRequest.setCollectionGroupDesignations(Arrays.asList("Shared"));
        searchRecordsRequest.setTotalRecordsCount("1");
        searchRecordsRequest.setTotalPageCount(1);
        searchRecordsRequest.setPageSize(10);

        String url = solrClientUrl + "searchService/searchRecords";
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key","recap");
        HttpEntity<SearchRecordsRequest> requestEntity = new HttpEntity<>(searchRecordsRequest,headers);
        List<Integer> itemIds = new ArrayList<>();
        itemIds.add(311);
        List<LinkedHashMap<String, Object>> mapList = new ArrayList<>();
        LinkedHashMap<String,Object> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("bibId",95);
        linkedHashMap.put("itemIds",itemIds);
        mapList.add(linkedHashMap);
        Map<String,Object> map = new HashMap<>();
        map.put("totalPageCount",1);
        map.put("dataDumpSearchResults",mapList);
        map.put("totalRecordsCount","2");

        ResponseEntity<Map> responseEntity = new ResponseEntity<Map>(map, HttpStatus.OK);
        Mockito.when(mockRestTemplate.postForEntity(url, requestEntity, Map.class)).thenReturn(responseEntity);
        Mockito.when(mockedDataDumpSolrService.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(mockedDataDumpSolrService.getSolrClientUrl()).thenReturn(solrClientUrl);
        Mockito.when(mockedDataDumpSolrService.getResults(searchRecordsRequest)).thenCallRealMethod();
        Map results = mockedDataDumpSolrService.getResults(searchRecordsRequest);


        Integer totalPageCount = (Integer) results.get("totalPageCount");
        String totalBibsCount = (String)results.get("totalRecordsCount");
        List dataDumpSearchResults = (List) results.get("dataDumpSearchResults");
        assertNotNull(totalPageCount);
        assertNotNull(totalBibsCount);
        assertNotNull(dataDumpSearchResults);
        System.out.println("Total Pages : " + totalPageCount);
        System.out.println("Total Bibs : " + totalBibsCount);
    }

    @Test
    public void fetchResultsFromSolrForIncrementalDataDump() throws Exception{
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setOwningInstitutions(Arrays.asList("PUL","CUL"));
        searchRecordsRequest.setCollectionGroupDesignations(Arrays.asList("Shared"));
        searchRecordsRequest.setFieldName("BibLastUpdatedDate");
        searchRecordsRequest.setTotalRecordsCount("1");
        searchRecordsRequest.setTotalPageCount(1);
        searchRecordsRequest.setFieldValue(getFormattedString("2016-10-21 10:30"));
        searchRecordsRequest.setPageSize(10);

        String url = solrClientUrl + "searchService/searchRecords";
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key","recap");
        HttpEntity<SearchRecordsRequest> requestEntity = new HttpEntity<>(searchRecordsRequest,headers);
        List<Integer> itemIds = new ArrayList<>();
        itemIds.add(311);
        List<LinkedHashMap<String, Object>> mapList = new ArrayList<>();
        LinkedHashMap<String,Object> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("bibId",95);
        linkedHashMap.put("itemIds",itemIds);
        mapList.add(linkedHashMap);
        Map<String,Object> map = new HashMap<>();
        map.put("totalPageCount",1);
        map.put("dataDumpSearchResults",mapList);
        map.put("totalRecordsCount","2");

        ResponseEntity<Map> responseEntity = new ResponseEntity<Map>(map, HttpStatus.OK);
        Mockito.when(mockRestTemplate.postForEntity(url, requestEntity, Map.class)).thenReturn(responseEntity);
        Mockito.when(mockedDataDumpSolrService.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(mockedDataDumpSolrService.getSolrClientUrl()).thenReturn(solrClientUrl);
        Mockito.when(mockedDataDumpSolrService.getResults(searchRecordsRequest)).thenCallRealMethod();
        Map results = mockedDataDumpSolrService.getResults(searchRecordsRequest);

        Integer totalPageCount = (Integer) results.get("totalPageCount");
        String totalBibsCount = (String) results.get("totalRecordsCount");
        List dataDumpSearchResults = (List) results.get("dataDumpSearchResults");
        assertNotNull(totalPageCount);
        assertNotNull(totalBibsCount);
        assertNotNull(dataDumpSearchResults);
        System.out.println("Total Pages : " + totalPageCount);
        System.out.println("Total Bibs : " + totalBibsCount);
    }

    public String getFormattedString(String dateStr){
        String formattedString = dateStr.substring(0,10)+"T"+dateStr.substring(11,16)+":00Z TO NOW";
        return formattedString;
    }
}