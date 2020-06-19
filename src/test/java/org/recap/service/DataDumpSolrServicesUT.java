package org.recap.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.model.search.SearchRecordsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 19/4/17.
 */
public class DataDumpSolrServicesUT extends BaseTestCase{

    @Mock
    DataDumpSolrService dataDumpSolrService;

    @Mock
    RestTemplate restTemplate;

    @Value("${solrclient.url}")
    String solrClientUrl;

    @Before
    public void setup()throws Exception{
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDataDumpSolrService(){
        HttpHeaders headers = new HttpHeaders();
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        headers.set("api_key","recap");
        HttpEntity<SearchRecordsRequest> requestEntity = new HttpEntity<>(searchRecordsRequest,headers);
        String url = solrClientUrl + "searchService/searchRecords";
        Map map = new HashMap();
        ResponseEntity<Map> responseEntity = new ResponseEntity<Map>(map, HttpStatus.OK);
        Mockito.when(dataDumpSolrService.getSolrClientUrl()).thenReturn(solrClientUrl);
        Mockito.when(dataDumpSolrService.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(dataDumpSolrService.getRestTemplate().postForEntity(url, requestEntity, Map.class)).thenReturn(responseEntity);
        Mockito.when(dataDumpSolrService.getResults(searchRecordsRequest)).thenCallRealMethod();
        Map response = dataDumpSolrService.getResults(searchRecordsRequest);
        assertNotNull(response);

    }

}