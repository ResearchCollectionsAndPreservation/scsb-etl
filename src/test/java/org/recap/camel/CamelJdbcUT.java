package org.recap.camel;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.FileEndpoint;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileFilter;
import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.util.datadump.DataExportHeaderUtil;
import org.recap.camel.dynamicrouter.DynamicRouteBuilder;
import org.recap.model.export.DataDumpRequest;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.repository.XmlRecordRepository;
import org.recap.service.DataDumpSolrService;
import org.recap.service.formatter.datadump.MarcXmlFormatterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by peris on 7/17/16.
 */

@Ignore
public class CamelJdbcUT extends BaseTestCase {

    @Value("${etl.split.xml.tag.name}")
    String xmlTagName;

    @Value("${etl.pool.size}")
    Integer etlPoolSize;

    @Value("${etl.pool.size}")
    Integer etlMaxPoolSize;

    @Value("${etl.max.pool.size}")
    String inputDirectoryPath;

    @Value("${activemq.broker.url}")
    String brokerUrl;

    @Autowired
    DataExportHeaderUtil dataExportHeaderUtil;

    @Autowired
    XmlRecordRepository xmlRecordRepository;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    DataDumpSolrService dataDumpSolrService;

    @Autowired
    MarcXmlFormatterService marcXmlFormatterService;

    @Autowired
    private ProducerTemplate producer;

    @Value("${datadump.batch.size}")
    String dataDumpBatchSize;

    @Autowired
    DynamicRouteBuilder dynamicRouteBuilder;

    @Mock
    DataDumpSolrService mockedDataDumpSolrService;

    @Value("${scsb.solr.doc.url}")
    String solrClientUrl;

    @Mock
    RestTemplate mockRestTemplate;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    //@Ignore
   // @Test
    public void parseXmlAndInsertIntoDb() throws Exception {


        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                FileEndpoint fileEndpoint = endpoint("file:" + inputDirectoryPath, FileEndpoint.class);
                fileEndpoint.setFilter(new XmlFileFilter());

                from(fileEndpoint)
                        .split()
                        .tokenizeXML(xmlTagName)
                        .streaming()
                        .threads(etlPoolSize, etlMaxPoolSize, "xmlProcessingThread")
                        .process(new XmlProcessor(xmlRecordRepository))
                        .to("jdbc:dataSource");
            }
        });

        java.lang.Thread.sleep(10000);
    }

    class XmlFileFilter implements GenericFileFilter {
        @Override
        public boolean accept(GenericFile file) {
            return FilenameUtils.getExtension(file.getAbsoluteFilePath()).equalsIgnoreCase("xml");
        }
    }


    @Test
    public void exportDataDumpForMarcXML() throws Exception {
        dynamicRouteBuilder.addDataDumpExportRoutes();
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setOwningInstitutions(Arrays.asList("CUL"));
        searchRecordsRequest.setCollectionGroupDesignations(Arrays.asList("Shared"));
        searchRecordsRequest.setPageSize(Integer.valueOf(dataDumpBatchSize));

        long startTime = System.currentTimeMillis();
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

        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setToEmailAddress("peri.subrahmanya@gmail.com");
        String dateTimeString = getDateTimeString();
        dataDumpRequest.setDateTimeString(dateTimeString);
        dataDumpRequest.setRequestingInstitutionCode("PUL");
        dataDumpRequest.setTransmissionType(RecapConstants.DATADUMP_TRANSMISSION_TYPE_FTP);
        dataDumpRequest.setInstitutionCodes(Arrays.asList("NYPL", "CUL"));
        dataDumpRequest.setCollectionGroupIds(Arrays.asList(1,2));
        dataDumpRequest.setOutputFileFormat(RecapConstants.DATADUMP_XML_FORMAT_MARC);
        dataDumpRequest.setRequestId(new SimpleDateFormat("yyyy-MM-dd HH").format(new Date()));

        long endTime = System.currentTimeMillis();
        System.out.println("Time taken to fetch 10K results for page 1 is : " + (endTime - startTime) / 1000 + " seconds ");
        String fileName = "PUL" + File.separator + dateTimeString + File.separator + RecapConstants.DATA_DUMP_FILE_NAME + "PUL" + 0;
        String folderName = "PUL" + File.separator + dateTimeString;

//        Integer totalPageCount = (Integer) results.get("totalPageCount");
        Integer totalPageCount = 4;

        String headerString = dataExportHeaderUtil.getBatchHeaderString(totalPageCount, 1, folderName, fileName, dataDumpRequest);

        producer.sendBodyAndHeader(RecapConstants.SOLR_INPUT_FOR_DATA_EXPORT_Q, results, "batchHeaders", headerString.toString());

        for (int pageNum = 1; pageNum < totalPageCount; pageNum++) {
            searchRecordsRequest.setPageNumber(pageNum);
            startTime = System.currentTimeMillis();
            Map results1 = mockedDataDumpSolrService.getResults(searchRecordsRequest);
            endTime = System.currentTimeMillis();
            System.out.println("Time taken to fetch 10K results for page  : " + pageNum + " is " + (endTime - startTime) / 1000 + " seconds ");
            fileName = "PUL" + File.separator + dateTimeString + File.separator + RecapConstants.DATA_DUMP_FILE_NAME + "PUL" + pageNum + 1;
            headerString = dataExportHeaderUtil.getBatchHeaderString(totalPageCount, pageNum + 1, folderName, fileName, dataDumpRequest);
            producer.sendBodyAndHeader(RecapConstants.SOLR_INPUT_FOR_DATA_EXPORT_Q, results1, "batchHeaders", headerString.toString());
        }

       /* while (true) {

        }*/
    }

    @Test
    public void exportDataDumpForSCSBXML() throws Exception {
        dynamicRouteBuilder.addDataDumpExportRoutes();
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setOwningInstitutions(Arrays.asList("CUL"));
        searchRecordsRequest.setCollectionGroupDesignations(Arrays.asList("Shared"));
        searchRecordsRequest.setPageSize(Integer.valueOf(2));

        long startTime = System.currentTimeMillis();
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

        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setToEmailAddress("peri.subrahmanya@gmail.com");
        String dateTimeString = getDateTimeString();
        dataDumpRequest.setDateTimeString(dateTimeString);
        dataDumpRequest.setRequestingInstitutionCode("PUL");
        dataDumpRequest.setTransmissionType(RecapConstants.DATADUMP_TRANSMISSION_TYPE_FTP);
        dataDumpRequest.setInstitutionCodes(Arrays.asList("NYPL", "CUL"));
        dataDumpRequest.setCollectionGroupIds(Arrays.asList(1,2));
        dataDumpRequest.setOutputFileFormat(RecapConstants.DATADUMP_XML_FORMAT_SCSB);
        dataDumpRequest.setRequestId(new SimpleDateFormat("yyyy-MM-dd HH").format(new Date()));

        long endTime = System.currentTimeMillis();
        System.out.println("Time taken to fetch 10K results for page 1 is : " + (endTime - startTime) / 1000 + " seconds ");
        String fileName = "PUL" + File.separator + dateTimeString + File.separator + RecapConstants.DATA_DUMP_FILE_NAME + "PUL" + 0;
        String folderName = "PUL" + File.separator + dateTimeString;

//        Integer totalPageCount = (Integer) results.get("totalPageCount");
        Integer totalPageCount = 3;

        String headerString = dataExportHeaderUtil.getBatchHeaderString(totalPageCount, 1, folderName, fileName, dataDumpRequest);

        producer.sendBodyAndHeader(RecapConstants.SOLR_INPUT_FOR_DATA_EXPORT_Q, results, "batchHeaders", headerString.toString());

        for (int pageNum = 1; pageNum < totalPageCount; pageNum++) {
            searchRecordsRequest.setPageNumber(pageNum);
            startTime = System.currentTimeMillis();
            Map results1 = mockedDataDumpSolrService.getResults(searchRecordsRequest);
            endTime = System.currentTimeMillis();
            System.out.println("Time taken to fetch 10K results for page  : " + pageNum + " is " + (endTime - startTime) / 1000 + " seconds ");
            fileName = "PUL" + File.separator + dateTimeString + File.separator + RecapConstants.DATA_DUMP_FILE_NAME + "PUL" + pageNum + 1;
            headerString = dataExportHeaderUtil.getBatchHeaderString(totalPageCount, pageNum + 1, folderName, fileName, dataDumpRequest);
            producer.sendBodyAndHeader(RecapConstants.SOLR_INPUT_FOR_DATA_EXPORT_Q, results1, "batchHeaders", headerString.toString());
        }

        /*while (true) {

        }*/
    }

    @Test
    public void exportDataDumpForDeletedJson() throws Exception {
        dynamicRouteBuilder.addDataDumpExportRoutes();
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setOwningInstitutions(Arrays.asList("PUL"));
        searchRecordsRequest.setCollectionGroupDesignations(Arrays.asList("Shared"));
        searchRecordsRequest.setDeleted(true);
        searchRecordsRequest.setPageSize(Integer.valueOf(dataDumpBatchSize));

        long startTime = System.currentTimeMillis();
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

        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setToEmailAddress("peri.subrahmanya@gmail.com");
        String dateTimeString = getDateTimeString();
        dataDumpRequest.setDateTimeString(dateTimeString);
        dataDumpRequest.setRequestingInstitutionCode("PUL");
        dataDumpRequest.setTransmissionType(RecapConstants.DATADUMP_TRANSMISSION_TYPE_FTP);
        dataDumpRequest.setInstitutionCodes(Arrays.asList("NYPL", "CUL"));
        dataDumpRequest.setCollectionGroupIds(Arrays.asList(1,2));
        dataDumpRequest.setOutputFileFormat(RecapConstants.DATADUMP_DELETED_JSON_FORMAT);
        dataDumpRequest.setRequestId(new SimpleDateFormat("yyyy-MM-dd HH").format(new Date()));

        long endTime = System.currentTimeMillis();
        System.out.println("Time taken to fetch 10K results for page 1 is : " + (endTime - startTime) / 1000 + " seconds ");
        String fileName = "PUL" + File.separator + dateTimeString + File.separator + RecapConstants.DATA_DUMP_FILE_NAME + "PUL" + 0;
        String folderName = "PUL" + File.separator + dateTimeString;

        Integer totalPageCount = (Integer) results.get("totalPageCount");

        String headerString = dataExportHeaderUtil.getBatchHeaderString(totalPageCount, 1, folderName, fileName, dataDumpRequest);
        producer.sendBodyAndHeader(RecapConstants.SOLR_INPUT_FOR_DATA_EXPORT_Q, results, "batchHeaders", headerString);

        for (int pageNum = 1; pageNum < totalPageCount; pageNum++) {
            searchRecordsRequest.setPageNumber(pageNum);
            startTime = System.currentTimeMillis();
            Map results1 = mockedDataDumpSolrService.getResults(searchRecordsRequest);
            endTime = System.currentTimeMillis();
            System.out.println("Time taken to fetch 10K results for page  : " + pageNum + " is " + (endTime - startTime) / 1000 + " seconds ");
            fileName = "PUL" + File.separator + dateTimeString + File.separator + RecapConstants.DATA_DUMP_FILE_NAME + "PUL" + pageNum + 1;
            headerString = dataExportHeaderUtil.getBatchHeaderString(totalPageCount, pageNum + 1, folderName, fileName, dataDumpRequest);
            producer.sendBodyAndHeader(RecapConstants.SOLR_INPUT_FOR_DATA_EXPORT_Q, results1, "batchHeaders", headerString.toString());
        }

       /* while (true) {

        }*/
    }

    private String getDateTimeString() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(RecapConstants.DATE_FORMAT_MMDDYYY);
        return sdf.format(date);
    }
}
