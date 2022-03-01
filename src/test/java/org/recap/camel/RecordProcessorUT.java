package org.recap.camel;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.recap.ScsbConstants;
import org.recap.model.jaxb.BibRecord;
import org.recap.model.jaxb.Holding;
import org.recap.model.jaxb.Holdings;
import org.recap.model.jaxb.marc.ContentType;
import org.recap.model.jpa.*;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;
import org.recap.repository.CollectionGroupDetailsRepository;
import org.recap.repository.ImsLocationDetailsRepository;
import org.recap.repository.InstitutionDetailsRepository;
import org.recap.repository.ItemStatusDetailsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JAXBContext.class, XMLInputFactory.class})
public class RecordProcessorUT {

    @InjectMocks
    RecordProcessor recordProcessor;

    @Mock
    BibDataProcessor bibDataProcessor;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    ProducerTemplate producer;

    @Mock
    ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Mock
    CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Mock
    ExecutorService executorService;

    @Mock
    ImsLocationDetailsRepository imsLocationDetailsRepository;

    @Mock
    Future future;

    @Mock
    JAXBContext jaxbContext;

    @Mock
    XMLInputFactory xmlInputFactory;

    @Mock
    XMLStreamReader xmlStreamReader;

    @Mock
    Unmarshaller unmarshaller;

    @Before
    public void setup() throws Exception {
        PowerMockito.mockStatic(JAXBContext.class);
        PowerMockito.mockStatic(XMLInputFactory.class);
    }

    @Test
    public void process() throws Exception {
        XmlRecordEntity xmlRecordEntity = getXmlRecordEntity();
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Page<XmlRecordEntity> xmlRecordEntities = new PageImpl<XmlRecordEntity>(Arrays.asList(xmlRecordEntity));
        Map<String, Object> object = new HashMap<>();
        object.put("bibliographicEntity", getBibliographicEntity());
        List<Future<Object>> futures = new ArrayList<>();
        futures.add(future);
        Mockito.when(future.get()).thenReturn(object);
        Mockito.when(executorService.invokeAll(any())).thenReturn(futures);
        BibRecord bibRecord = new BibRecord();
        bibRecord.setHoldings(Arrays.asList(getHoldings()));
        PowerMockito.when(JAXBContext.newInstance(BibRecord.class)).thenReturn(jaxbContext);
        PowerMockito.when(XMLInputFactory.newFactory()).thenReturn(xmlInputFactory);
        Mockito.when(xmlInputFactory.createXMLStreamReader(any(InputStream.class))).thenReturn(xmlStreamReader);
        Mockito.when(jaxbContext.createUnmarshaller()).thenReturn(unmarshaller);
        Mockito.when(unmarshaller.unmarshal(any(XMLStreamReader.class))).thenReturn(bibRecord);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(Arrays.asList(institutionEntity));
        //recordProcessor.process(xmlRecordEntities);
    }

    @Test
    public void processInterruptedException() throws Exception {
        XmlRecordEntity xmlRecordEntity = getXmlRecordEntity();
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Page<XmlRecordEntity> xmlRecordEntities = new PageImpl<XmlRecordEntity>(Arrays.asList(xmlRecordEntity));
        Map<String, Object> object = new HashMap<>();
        object.put("bibliographicEntity", getBibliographicEntity());
        List<Future<Object>> futures = new ArrayList<>();
        futures.add(future);
        Mockito.when(future.get()).thenThrow(new InterruptedException());
        Mockito.when(executorService.invokeAll(any())).thenReturn(futures);
        BibRecord bibRecord = new BibRecord();
        bibRecord.setHoldings(Arrays.asList(getHoldings()));
        PowerMockito.when(JAXBContext.newInstance(BibRecord.class)).thenReturn(jaxbContext);
        PowerMockito.when(XMLInputFactory.newFactory()).thenReturn(xmlInputFactory);
        Mockito.when(xmlInputFactory.createXMLStreamReader(any(InputStream.class))).thenReturn(xmlStreamReader);
        Mockito.when(jaxbContext.createUnmarshaller()).thenReturn(unmarshaller);
        Mockito.when(unmarshaller.unmarshal(any(XMLStreamReader.class))).thenReturn(bibRecord);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(Arrays.asList(institutionEntity));
        //recordProcessor.process(xmlRecordEntities);
    }

    @Test
    public void processExecutionException() throws Exception {
        XmlRecordEntity xmlRecordEntity = getXmlRecordEntity();
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Page<XmlRecordEntity> xmlRecordEntities = new PageImpl<XmlRecordEntity>(Arrays.asList(xmlRecordEntity));
        Map<String, Object> object = new HashMap<>();
        object.put("bibliographicEntity", getBibliographicEntity());
        List<Future<Object>> futures = new ArrayList<>();
        futures.add(future);
        Mockito.when(future.get()).thenThrow(new ExecutionException(new Throwable()));
        Mockito.when(executorService.invokeAll(any())).thenReturn(futures);
        BibRecord bibRecord = new BibRecord();
        PowerMockito.when(JAXBContext.newInstance(BibRecord.class)).thenReturn(jaxbContext);
        PowerMockito.when(XMLInputFactory.newFactory()).thenReturn(xmlInputFactory);
        Mockito.when(xmlInputFactory.createXMLStreamReader(any(InputStream.class))).thenReturn(xmlStreamReader);
        Mockito.when(jaxbContext.createUnmarshaller()).thenReturn(unmarshaller);
        Mockito.when(unmarshaller.unmarshal(any(XMLStreamReader.class))).thenReturn(bibRecord);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(Arrays.asList(institutionEntity));
        //recordProcessor.process(xmlRecordEntities);
    }

    @Test
    public void processInnerException() throws Exception {
        XmlRecordEntity xmlRecordEntity = getXmlRecordEntity();
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Page<XmlRecordEntity> xmlRecordEntities = new PageImpl<XmlRecordEntity>(Arrays.asList(xmlRecordEntity));
        Map<String, Object> object = new HashMap<>();
        object.put("bibliographicEntity", getBibliographicEntity());
        List<Future<Object>> futures = new ArrayList<>();
        futures.add(future);
        Mockito.when(executorService.invokeAll(any())).thenThrow(new InterruptedException());
        BibRecord bibRecord = new BibRecord();
        PowerMockito.when(JAXBContext.newInstance(BibRecord.class)).thenReturn(jaxbContext);
        PowerMockito.when(XMLInputFactory.newFactory()).thenReturn(xmlInputFactory);
        Mockito.when(xmlInputFactory.createXMLStreamReader(any(InputStream.class))).thenReturn(xmlStreamReader);
        Mockito.when(jaxbContext.createUnmarshaller()).thenReturn(unmarshaller);
        Mockito.when(unmarshaller.unmarshal(any(XMLStreamReader.class))).thenReturn(bibRecord);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(Arrays.asList(institutionEntity));
        try {
            recordProcessor.process(xmlRecordEntities);
        } catch (Exception e) {
        }
    }

    @Test
    public void processWithEmptyFuture() {
        XmlRecordEntity xmlRecordEntity = getXmlRecordEntity();
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Page<XmlRecordEntity> xmlRecordEntities = new PageImpl<XmlRecordEntity>(Arrays.asList(xmlRecordEntity));
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(Arrays.asList(institutionEntity));
        //recordProcessor.process(xmlRecordEntities);
    }

    @Test
    public void processFutureResults() {
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        ReportEntity reportEntity = getReportEntity();
        Map<String, Object> map = new HashMap<>();
        map.put("bibliographicEntity", bibliographicEntity);
        map.put("reportEntities", Arrays.asList(reportEntity));
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        List<ReportEntity> reportEntities = new ArrayList<>();
        //ReflectionTestUtils.invokeMethod(recordProcessor, "processFutureResults", map, bibliographicEntities, reportEntities);
    }

    @Test
    public void getReportEntityForFailure() {
        XmlRecordEntity xmlRecordEntity = getXmlRecordEntity();
        String message = "Holding content missing";
        ReflectionTestUtils.invokeMethod(recordProcessor, "getReportEntityForFailure", xmlRecordEntity, message);
    }

    @Test
    public void getJaxbHandler() {
        recordProcessor.getJaxbHandler();
    }

    @Test
    public void getItemStatusMap() {
        ItemStatusEntity itemStatusEntity = getItemStatusEntity();
        Mockito.when(itemStatusDetailsRepository.findAll()).thenReturn(Arrays.asList(itemStatusEntity));
        Map<String, Integer> result = recordProcessor.getItemStatusMap();
        assertNotNull(result);
    }

    @Test
    public void getCollectionGroupMap() {
        CollectionGroupEntity collectionGroupEntity = getCollectionGroupEntity();
        Mockito.when(collectionGroupDetailsRepository.findAll()).thenReturn(Arrays.asList(collectionGroupEntity));
        Map<String, Integer> result = recordProcessor.getCollectionGroupMap();
        assertNotNull(result);
    }

    @Test
    public void getImsLocationCodeMap() {
        ImsLocationEntity imsLocationEntity = getImsLocationEntity();
        Mockito.when(imsLocationDetailsRepository.findAll()).thenReturn(Arrays.asList(imsLocationEntity));
        Map<String, Integer> result = recordProcessor.getImsLocationCodeMap();
        assertNotNull(result);
    }

    @Test
    public void getExecutorService() {
        Mockito.when(executorService.isShutdown()).thenReturn(true);
        ReflectionTestUtils.setField(recordProcessor,"dataLoadThreadSize",10);
        recordProcessor.getExecutorService();
    }

    @Test
    public void validateHoldingsContent() {
        List<Holdings> holdingsList = new ArrayList<>();
        holdingsList.add(getHoldings());
        ReflectionTestUtils.invokeMethod(recordProcessor, "validateHoldingsContent", holdingsList);
    }

    @Test
    public void validateHoldingsContentEmptyList() {
        ReflectionTestUtils.invokeMethod(recordProcessor, "validateHoldingsContent", Collections.EMPTY_LIST);
    }

    @Test
    public void validateHoldingsContentWithoutHoldings() {
        List<Holdings> holdingsList = new ArrayList<>();
        holdingsList.add(null);
        ReflectionTestUtils.invokeMethod(recordProcessor, "validateHoldingsContent", holdingsList);
    }

    @Test
    public void checkGetters() {
        recordProcessor.setXmlFileName("test.xml");
        assertNotNull(recordProcessor.getXmlFileName());
        recordProcessor.setExecutorService(executorService);
        recordProcessor.shutdownExecutorService();
        recordProcessor.setInstitutionName("Pul");
        assertNotNull(recordProcessor.getInstitutionName());

    }

    @Test
    public void validateHoldingsContentWithoutHolding() {
        List<Holdings> holdingsList = new ArrayList<>();
        Holdings holdings = getHoldings();
        holdings.setHolding(null);
        holdingsList.add(holdings);
        ReflectionTestUtils.invokeMethod(recordProcessor, "validateHoldingsContent", holdingsList);
    }

    private Holdings getHoldings() {
        Holdings holdings = new Holdings();
        Holding holding = new Holding();
        holding.setContent(new ContentType());
        holding.setOwningInstitutionHoldingsId("1");
        holdings.setHolding(Arrays.asList(holding));
        return holdings;
    }

    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(3);
        institutionEntity.setInstitutionName("NYPL");
        institutionEntity.setInstitutionCode("NYPL");
        return institutionEntity;
    }

    private XmlRecordEntity getXmlRecordEntity() {
        XmlRecordEntity xmlRecordEntity = new XmlRecordEntity();
        xmlRecordEntity.setId(1);
        xmlRecordEntity.setXml(new byte[1]);
        xmlRecordEntity.setDataLoaded(new Date());
        xmlRecordEntity.setXmlFileName("XML");
        xmlRecordEntity.setOwningInstBibId("1");
        xmlRecordEntity.setOwningInst("1");
        return xmlRecordEntity;
    }

    private BibliographicEntity getBibliographicEntity() {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("etl");
        bibliographicEntity.setLastUpdatedBy("etl");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("001");
        List<BibliographicEntity> bibliographicEntitylist = new LinkedList(Arrays.asList(bibliographicEntity));

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionHoldingsId("002");
        List<HoldingsEntity> holdingsEntitylist = new LinkedList(Arrays.asList(holdingsEntity));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setCallNumberType("0");
        itemEntity.setCallNumber("callNum");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("etl");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("etl");
        itemEntity.setBarcode("334330028533193343300285331933433002853319555565");
        itemEntity.setOwningInstitutionItemId(".i1231");
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCustomerCode("PA");
        itemEntity.setItemAvailabilityStatusId(1);
        List<ItemEntity> itemEntitylist = new LinkedList(Arrays.asList(itemEntity));


        holdingsEntity.setBibliographicEntities(bibliographicEntitylist);
        holdingsEntity.setItemEntities(itemEntitylist);
        bibliographicEntity.setHoldingsEntities(holdingsEntitylist);
        bibliographicEntity.setItemEntities(itemEntitylist);
        itemEntity.setHoldingsEntities(holdingsEntitylist);
        itemEntity.setBibliographicEntities(bibliographicEntitylist);

        return bibliographicEntity;
    }

    private ReportEntity getReportEntity() {
        ReportEntity reportEntity = new ReportEntity();
        org.recap.model.jparw.ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setHeaderName(ScsbConstants.HEADER_FETCH_TYPE);
        reportDataEntity.setHeaderValue(ScsbConstants.DATADUMP_FETCHTYPE_INCREMENTAL);
        reportEntity.setReportDataEntities(Arrays.asList(reportDataEntity));
        reportEntity.setCreatedDate(new Date());
        return reportEntity;
    }

    public ItemStatusEntity getItemStatusEntity() {
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(1);
        itemStatusEntity.setStatusCode("statusCode");
        itemStatusEntity.setStatusDescription("statusCode");
        return itemStatusEntity;
    }

    private CollectionGroupEntity getCollectionGroupEntity() {
        CollectionGroupEntity collectionGroupEntity = new CollectionGroupEntity();
        collectionGroupEntity.setId(1);
        collectionGroupEntity.setCollectionGroupCode("Complete");
        collectionGroupEntity.setCollectionGroupDescription("Complete");
        return collectionGroupEntity;
    }

    private ImsLocationEntity getImsLocationEntity() {
        ImsLocationEntity imsLocationEntity = new ImsLocationEntity();
        imsLocationEntity.setId(1);
        imsLocationEntity.setImsLocationCode("HD");
        imsLocationEntity.setImsLocationName("HD");
        return imsLocationEntity;
    }
}
