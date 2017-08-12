package org.recap.service;

import org.recap.model.search.SearchRecordsRequest;
import org.recap.spring.SwaggerAPIProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by peris on 10/26/16.
 */
@Service
public class DataDumpSolrService {
    /**
     * The Solr client url.
     */
    @Value("${solrclient.url}")
    String solrClientUrl;

    /**
     * Gets solr client url.
     *
     * @return the solr client url
     */
    public String getSolrClientUrl() {
        return solrClientUrl;
    }

    /**
     * Get rest template.
     *
     * @return the rest template
     */
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    /**
     * This method hits solrClient microservice and gets the solr search results for data dump process.
     *
     * @param searchRecordsRequest the search records request
     * @return the results
     */
    public Map getResults(SearchRecordsRequest searchRecordsRequest) {
        String url = getSolrClientUrl() + "searchService/searchRecords";
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key", SwaggerAPIProvider.getInstance().getSwaggerApiKey());
        HttpEntity<SearchRecordsRequest> requestEntity = new HttpEntity<>(searchRecordsRequest,headers);
        ResponseEntity<Map> responseEntity = getRestTemplate().postForEntity(url, requestEntity, Map.class);

        return responseEntity.getBody();
    }
}
