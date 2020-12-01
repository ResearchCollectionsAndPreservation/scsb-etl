package org.recap.service.executor.datadump;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.export.DataDumpRequest;
import org.recap.model.export.ImprovedFullDataDumpCallable;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.service.DataDumpSolrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by premkb on 27/9/16.
 */
public class FullDataDumpExecutorServiceUT extends BaseTestCase {

    private static final Logger logger = LoggerFactory.getLogger(FullDataDumpExecutorServiceUT.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    DataDumpSolrService dataDumpSolrService;

    @Mock
    DataDumpSolrService mockedDataDumpSolrService;

    @Mock
    DeletedDataDumpExecutorService mockedDeletedDataDumpExecutorService;

    @Autowired
    private FullDataDumpExecutorService fullDataDumpExecutorService;

    ImprovedFullDataDumpCallable improvedFullDataDumpCallable;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Value("${ftp.server.userName}")
    String ftpUserName;

    @Value("${ftp.server.knownHost}")
    String ftpKnownHost;

    @Value("${ftp.server.privateKey}")
    String ftpPrivateKey;

    @Value("${ftp.data.dump.dir}")
    String ftpDataDumpRemoteServer;

    @Value("${etl.data.dump.directory}")
    private String dumpDirectoryPath;

    @Value("${datadump.batch.size}")
    private int batchSize;

    @Value("${scsb.solr.doc.url}")
    String solrClientUrl;

    @Mock
    RestTemplate mockRestTemplate;

    @Mock
    Future mockedFuture;

    @Autowired
    ProducerTemplate producer;

    private String requestingInstitutionCode = "PUL";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void exportFullDumpForNYPLAndCUL() throws Exception {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setFetchType("0");
        dataDumpRequest.setRequestingInstitutionCode(requestingInstitutionCode);
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        dataDumpRequest.setCollectionGroupIds(cgIds);
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("CUL");
        institutionCodes.add("NYPL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setTransmissionType("2");
        dataDumpRequest.setOutputFileFormat(RecapConstants.XML_FILE_FORMAT);
        dataDumpRequest.setDateTimeString(getDateTimeString());

        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setOwningInstitutions(Arrays.asList("PUL","CUL"));
        searchRecordsRequest.setCollectionGroupDesignations(Arrays.asList("Shared"));
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
        map.put("totalRecordCount",2);

        ResponseEntity<Map> responseEntity = new ResponseEntity<Map>(map, HttpStatus.OK);
        Mockito.when(mockRestTemplate.postForEntity(url, requestEntity, Map.class)).thenReturn(responseEntity);
        Mockito.when(mockedDataDumpSolrService.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(mockedDataDumpSolrService.getSolrClientUrl()).thenReturn(solrClientUrl);
        Mockito.when(mockedDataDumpSolrService.getResults(searchRecordsRequest)).thenCallRealMethod();
        Map results = mockedDataDumpSolrService.getResults(searchRecordsRequest);
        List<LinkedHashMap> dataDumpSearchResults = (List<LinkedHashMap>) results.get("dataDumpSearchResults");

        improvedFullDataDumpCallable = appContext.getBean(ImprovedFullDataDumpCallable.class,dataDumpSearchResults,bibliographicDetailsRepository);
        assertNotNull(improvedFullDataDumpCallable);

        ExecutorService executorService = Executors.newFixedThreadPool(1);

        Future future = executorService.submit(improvedFullDataDumpCallable);
        List<BibliographicEntity> bibliographicEntityList = new ArrayList<>();
        bibliographicEntityList.add(saveBibSingleHoldingsSingleItem());
        Mockito.when((List<BibliographicEntity>)mockedFuture.get()).thenReturn(bibliographicEntityList);
        List<BibliographicEntity> resultFromFuture = (List<BibliographicEntity>) mockedFuture.get();

        assertNotNull(resultFromFuture);

        for (Iterator<BibliographicEntity> iterator = resultFromFuture.iterator(); iterator.hasNext(); ) {
            BibliographicEntity bibliographicEntity = iterator.next();
            assertNotNull(bibliographicEntity);
            assertNotNull(bibliographicEntity.getItemEntities());

        }

        executorService.shutdown();
        Thread.sleep(4000);
    }

    @Test
    public void getFullDumpForMarcXmlFileSystem()throws Exception{
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setFetchType("0");
        dataDumpRequest.setToEmailAddress("peri.subrahmanya@htcinc.com");
        dataDumpRequest.setRequestingInstitutionCode(requestingInstitutionCode);
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        dataDumpRequest.setCollectionGroupIds(cgIds);
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("CUL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setTransmissionType("2");
        dataDumpRequest.setOutputFileFormat(RecapConstants.XML_FILE_FORMAT);
        dataDumpRequest.setDateTimeString(getDateTimeString());
        Mockito.when(mockedDeletedDataDumpExecutorService.process(dataDumpRequest)).thenReturn("Success");
        String response = mockedDeletedDataDumpExecutorService.process(dataDumpRequest);
        assertNotNull(response);
        assertEquals(response,"Success");

    }


    @Test
    public void getFullDumpForScsbXmlFileSystem()throws Exception{
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setFetchType("0");
        dataDumpRequest.setRequestingInstitutionCode(requestingInstitutionCode);
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        dataDumpRequest.setCollectionGroupIds(cgIds);
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("CUL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setTransmissionType("2");
        dataDumpRequest.setOutputFileFormat(RecapConstants.XML_FILE_FORMAT);
        dataDumpRequest.setDateTimeString(getDateTimeString());
        Mockito.when(mockedDeletedDataDumpExecutorService.process(dataDumpRequest)).thenReturn("Success");
        String response = mockedDeletedDataDumpExecutorService.process(dataDumpRequest);
        assertNotNull(response);
        assertEquals(response,"Success");
    }

    @Test
    public void getFullDumpForMarcXmlFtp()throws Exception{
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setFetchType("0");
        dataDumpRequest.setRequestingInstitutionCode(requestingInstitutionCode);
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        dataDumpRequest.setCollectionGroupIds(cgIds);
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("CUL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setTransmissionType("0");
        dataDumpRequest.setOutputFileFormat(RecapConstants.XML_FILE_FORMAT);
        dataDumpRequest.setDateTimeString(getDateTimeString());
        Mockito.when(mockedDeletedDataDumpExecutorService.process(dataDumpRequest)).thenReturn("Success");
        String response = mockedDeletedDataDumpExecutorService.process(dataDumpRequest);
        assertNotNull(response);
        assertEquals(response,"Success");
    }

    private String getDateTimeString(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(RecapConstants.DATE_FORMAT_DDMMMYYYYHHMM);
        return sdf.format(date);
    }

    private int getLoopCount(Long totalRecordCount,int batchSize){
        int quotient = Integer.valueOf(Long.toString(totalRecordCount)) / (batchSize);
        int remainder = Integer.valueOf(Long.toString(totalRecordCount)) % (batchSize);
        int loopCount = remainder == 0 ? quotient : quotient + 1;
        return loopCount;
    }

    public BibliographicEntity saveBibSingleHoldingsSingleItem() throws Exception {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = getBibliographicEntity(1, String.valueOf(random.nextInt()));

        HoldingsEntity holdingsEntity = getHoldingsEntity(random, 1);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("etl");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("etl");
        itemEntity.setBarcode("0800");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("1");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        return savedBibliographicEntity;
    }

    private HoldingsEntity getHoldingsEntity(Random random, Integer institutionId) {
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionId(institutionId);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));
        return holdingsEntity;
    }

    private BibliographicEntity getBibliographicEntity(Integer institutionId, String owningInstitutionBibId1) {
        BibliographicEntity bibliographicEntity1 = new BibliographicEntity();
        bibliographicEntity1.setContent("mock Content".getBytes());
        bibliographicEntity1.setCreatedDate(new Date());
        bibliographicEntity1.setCreatedBy("etl");
        bibliographicEntity1.setLastUpdatedBy("etl");
        bibliographicEntity1.setLastUpdatedDate(new Date());
        bibliographicEntity1.setOwningInstitutionId(institutionId);
        bibliographicEntity1.setOwningInstitutionBibId(owningInstitutionBibId1);
        return bibliographicEntity1;
    }
}
