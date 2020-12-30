package org.recap.camel;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.etl.BibPersisterCallable;
import org.recap.model.jaxb.BibRecord;
import org.recap.model.jaxb.JAXBHandler;
import org.recap.model.jaxb.Holding;
import org.recap.model.jaxb.Holdings;
import org.recap.model.jpa.*;
import org.recap.model.jparw.ReportEntity;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.repository.CollectionGroupDetailsRepository;
import org.recap.repository.InstitutionDetailsRepository;
import org.recap.repository.ItemStatusDetailsRepository;
import org.recap.util.DBReportUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by pvsubrah on 6/21/16.
 */
@Component
public class RecordProcessor {
    private static final Logger logger = LoggerFactory.getLogger(RecordProcessor.class);

    private Map<String, Integer> institutionEntityMap;
    private Map<String, Integer>  itemStatusMap;
    private Map<String, Integer>  collectionGroupMap;
    private JAXBHandler jaxbHandler;
    private String xmlFileName;
    private String institutionName;

    @Autowired
    private ProducerTemplate producer;

    @Autowired
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    private ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Autowired
    private CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    /**
     * The Bib data processor.
     */
    @Autowired
    BibDataProcessor bibDataProcessor;

    /**
     * The Db report util.
     */
    @Autowired
    DBReportUtil dbReportUtil;

    private ExecutorService executorService;


    /**
     * This method processes the xml record entities in scsb format and builds bibliographic entities to persist.
     *
     * @param xmlRecordEntities the xml record entities
     */
    public void process(Page<XmlRecordEntity> xmlRecordEntities) {
        logger.info("Processor: {}" , Thread.currentThread().getName());

        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        List<ReportEntity> reportEntities = new ArrayList<>();

        List<Future<Map<String, String>>> futures = prepareFutureTasks(xmlRecordEntities, reportEntities);

        for (Iterator<Future<Map<String, String>>> iterator = futures.iterator(); iterator.hasNext(); ) {
            Future<Map<String, String>> future = iterator.next();
            Object object = null;
            try {
                object = future.get();
            } catch (InterruptedException  e) {
                Thread.currentThread().interrupt();
                logger.error(RecapConstants.ERROR,e);
            } catch  (ExecutionException e) {
                logger.error(RecapConstants.ERROR,e);
            }

            processFutureResults(object, bibliographicEntities, reportEntities);
        }

        if (!CollectionUtils.isEmpty(bibliographicEntities)) {
            ETLExchange etlExchange = new ETLExchange();
            etlExchange.setBibliographicEntities(bibliographicEntities);
            etlExchange.setInstitutionEntityMap(getInstitutionEntityMap());
            etlExchange.setCollectionGroupMap(getCollectionGroupMap());
            bibDataProcessor.setXmlFileName(xmlFileName);
            bibDataProcessor.setInstitutionName(institutionName);
            bibDataProcessor.processETLExchagneAndPersistToDB(etlExchange);
        }


        if (!CollectionUtils.isEmpty(reportEntities)) {
            for(ReportEntity reportEntity : reportEntities) {
                producer.sendBody(RecapCommonConstants.REPORT_Q, reportEntity);
            }
        }

    }

    /**
     * Bibliographic entities and failure report entities are built from the map.
     *
     * @param object
     * @param bibliographicEntities
     * @param reportEntities
     */
    private void processFutureResults(Object object, List<BibliographicEntity> bibliographicEntities, List<ReportEntity> reportEntities) {
        Map<String, Object> resultMap = (Map<String, Object>) object;

        if (object != null) {
            Object bibliographicEntity = resultMap.get("bibliographicEntity");
            List<ReportEntity> reportEntityList = (List<ReportEntity>) resultMap.get("reportEntities");
            if (bibliographicEntity != null) {
                bibliographicEntities.add((BibliographicEntity) bibliographicEntity);
            }
            if (!CollectionUtils.isEmpty(reportEntityList)) {
                reportEntities.addAll(reportEntityList);
            }
        }
    }

    /**
     * This method takes xml records and processes them in multithreading to build bibliographic entities and failure report enitites.
     *
     * @param xmlRecordEntities
     * @param reportEntities
     * @return
     */
    private List<Future<Map<String, String>>> prepareFutureTasks(Page<XmlRecordEntity> xmlRecordEntities, List<ReportEntity> reportEntities) {
        BibRecord bibRecord;

        List<Callable<Map<String, String>>> callables = new ArrayList<>();

        for (Iterator<XmlRecordEntity> iterator = xmlRecordEntities.iterator(); iterator.hasNext(); ) {
            XmlRecordEntity xmlRecordEntity = iterator.next();
            String xml = new String(xmlRecordEntity.getXml());
            try {
                JAXBContext context = JAXBContext.newInstance(BibRecord.class);
                XMLInputFactory xif = XMLInputFactory.newFactory();
                xif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
                InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
                XMLStreamReader xsr = xif.createXMLStreamReader(stream);
                Unmarshaller um = context.createUnmarshaller();
                bibRecord = (BibRecord) um.unmarshal(xsr);
                boolean valid = validateHoldingsContent(bibRecord.getHoldings());
                if (valid) {
                    BibPersisterCallable bibPersisterCallable = new BibPersisterCallable();
                    bibPersisterCallable.setDbReportUtil(dbReportUtil);
                    bibPersisterCallable.setBibRecord(bibRecord);
                    bibPersisterCallable.setCollectionGroupMap(getCollectionGroupMap());
                    bibPersisterCallable.setInstitutionEntitiesMap(getInstitutionEntityMap());
                    bibPersisterCallable.setItemStatusMap(getItemStatusMap());
                    bibPersisterCallable.setXmlRecordEntity(xmlRecordEntity);
                    bibPersisterCallable.setInstitutionName(institutionName);

                    callables.add(bibPersisterCallable);
                } else {
                    ReportEntity reportEntityForFailure = getReportEntityForFailure(xmlRecordEntity, "Holding content missing");
                    reportEntities.add(reportEntityForFailure);
                }

            } catch (Exception e) {
                logger.error(RecapConstants.ERROR,e);
                ReportEntity reportEntity = new ReportEntity();
                List<ReportDataEntity> reportDataEntities = new ArrayList<>();
                String owningInst = xmlRecordEntity.getOwningInst();
                reportEntity.setCreatedDate(new Date());
                reportEntity.setType(RecapCommonConstants.FAILURE);
                reportEntity.setFileName(xmlRecordEntity.getXmlFileName());
                reportEntity.setInstitutionName(owningInst);

                if(StringUtils.isNotBlank(owningInst)) {
                    ReportDataEntity reportDataEntity = new ReportDataEntity();
                    reportDataEntity.setHeaderName(RecapCommonConstants.OWNING_INSTITUTION);
                    reportDataEntity.setHeaderValue(String.valueOf(getInstitutionEntityMap().get(owningInst)));
                    reportDataEntities.add(reportDataEntity);
                }

                if(StringUtils.isNotBlank(xmlRecordEntity.getOwningInstBibId())) {
                    ReportDataEntity reportDataEntity = new ReportDataEntity();
                    reportDataEntity.setHeaderName(RecapCommonConstants.OWNING_INSTITUTION_BIB_ID);
                    reportDataEntity.setHeaderValue(xmlRecordEntity.getOwningInstBibId());
                    reportDataEntities.add(reportDataEntity);
                }

                if(e.getCause() != null) {
                    ReportDataEntity reportDataEntity = new ReportDataEntity();
                    reportDataEntity.setHeaderName(RecapConstants.EXCEPTION_MESSAGE);
                    reportDataEntity.setHeaderValue(e.getCause().getMessage());
                    reportDataEntities.add(reportDataEntity);
                }

                reportEntity.setReportDataEntities(reportDataEntities);
                reportEntities.add(reportEntity);
            }
        }

        List<Future<Map<String, String>>> futures = null;
        try {
            futures = getExecutorService().invokeAll(callables);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error(RecapConstants.ERROR,e);
        }
        if(futures !=null) {
            futures
                    .stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                    });
        }
        return futures;
    }

    /**
     * Prepares a failure report entity from xml record and with the given message.
     *
     * @param xmlRecordEntity
     * @param message
     * @return
     */
    private ReportEntity getReportEntityForFailure(XmlRecordEntity xmlRecordEntity, String message) {
        ReportEntity reportEntity = new ReportEntity();
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        String owningInst = xmlRecordEntity.getOwningInst();
        reportEntity.setCreatedDate(new Date());
        reportEntity.setType(RecapCommonConstants.FAILURE);
        reportEntity.setFileName(xmlRecordEntity.getXmlFileName());
        reportEntity.setInstitutionName(owningInst);

        if(StringUtils.isNotBlank(owningInst)) {
            ReportDataEntity reportDataEntity = new ReportDataEntity();
            reportDataEntity.setHeaderName(RecapCommonConstants.OWNING_INSTITUTION);
            reportDataEntity.setHeaderValue(String.valueOf(getInstitutionEntityMap().get(owningInst)));
            reportDataEntities.add(reportDataEntity);
        }

        if(StringUtils.isNotBlank(xmlRecordEntity.getOwningInstBibId())) {
            ReportDataEntity reportDataEntity = new ReportDataEntity();
            reportDataEntity.setHeaderName(RecapCommonConstants.OWNING_INSTITUTION_BIB_ID);
            reportDataEntity.setHeaderValue(xmlRecordEntity.getOwningInstBibId());
            reportDataEntities.add(reportDataEntity);
        }

        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setHeaderName(RecapConstants.EXCEPTION_MESSAGE);
        reportDataEntity.setHeaderValue(message);
        reportDataEntities.add(reportDataEntity);

        reportEntity.setReportDataEntities(reportDataEntities);

        return reportEntity;

    }

    /**
     * Validates holdings content.
     * @param holdings
     * @return
     */
    private boolean validateHoldingsContent(List<Holdings> holdings) {
        if(!CollectionUtils.isEmpty(holdings)) {
            for (Iterator<Holdings> iterator = holdings.iterator(); iterator.hasNext(); ) {
                Holdings holding = iterator.next();
                if(null != holding) {
                    List<Holding> holdingList = holding.getHolding();
                    if(!CollectionUtils.isEmpty(holdingList)) {
                        for (Iterator<Holding> holdingIterator = holdingList.iterator(); holdingIterator.hasNext(); ) {
                            Holding holdingNew = holdingIterator.next();
                            if(null == holdingNew) {
                                return false;
                            }
                        }
                    }else {
                        return false;
                    }
                } else
                    return false;
            }
            return true;
        } else  {
            return false;
        }
    }


    /**
     * Gets jaxb handler.
     *
     * @return the jaxb handler
     */
    public JAXBHandler getJaxbHandler() {
        if (null == jaxbHandler) {
            jaxbHandler = JAXBHandler.getInstance();
        }
        return jaxbHandler;
    }

    /**
     * Gets institution entity map.
     *
     * @return the institution entity map
     */
    public Map<String, Integer> getInstitutionEntityMap() {
        if (null == institutionEntityMap) {
            institutionEntityMap = new HashMap<>();
            Iterable<InstitutionEntity> institutionEntities = institutionDetailsRepository.findAll();
            for (Iterator<InstitutionEntity> iterator = institutionEntities.iterator(); iterator.hasNext(); ) {
                InstitutionEntity institutionEntity = iterator.next();
                institutionEntityMap.put(institutionEntity.getInstitutionCode(), institutionEntity.getId());
            }
        }
        return institutionEntityMap;
    }

    /**
     * Gets item status map.
     *
     * @return the item status map
     */
    public Map<String, Integer> getItemStatusMap() {
        if (null == itemStatusMap) {
            itemStatusMap = new HashMap<>();
            Iterable<ItemStatusEntity> itemStatusEntities = itemStatusDetailsRepository.findAll();
            for (Iterator<ItemStatusEntity> iterator = itemStatusEntities.iterator(); iterator.hasNext(); ) {
                ItemStatusEntity itemStatusEntity = iterator.next();
                itemStatusMap.put(itemStatusEntity.getStatusCode(), itemStatusEntity.getId());
            }
        }
        return itemStatusMap;
    }

    /**
     * Gets collection group map.
     *
     * @return the collection group map
     */
    public Map<String, Integer> getCollectionGroupMap() {
        if (null == collectionGroupMap) {
            collectionGroupMap = new HashMap<>();
            Iterable<CollectionGroupEntity> collectionGroupEntities = collectionGroupDetailsRepository.findAll();
            for (Iterator<CollectionGroupEntity> iterator = collectionGroupEntities.iterator(); iterator.hasNext(); ) {
                CollectionGroupEntity collectionGroupEntity = iterator.next();
                collectionGroupMap.put(collectionGroupEntity.getCollectionGroupCode(), collectionGroupEntity.getId());
            }
        }
        return collectionGroupMap;
    }

    /**
     * Gets executor service.
     *
     * @return the executor service
     */
    public ExecutorService getExecutorService() {
        if (null == executorService || executorService.isShutdown()) {
            executorService = Executors.newFixedThreadPool(50);
        }
        return executorService;
    }

    /**
     * Gets xml file name.
     *
     * @return the xml file name
     */
    public String getXmlFileName() {
        return xmlFileName;
    }

    /**
     * Sets xml file name.
     *
     * @param xmlFileName the xml file name
     */
    public void setXmlFileName(String xmlFileName) {
        this.xmlFileName = xmlFileName;
    }

    /**
     * Gets institution name.
     *
     * @return the institution name
     */
    public String getInstitutionName() {
        return institutionName;
    }

    /**
     * Sets institution name.
     *
     * @param institutionName the institution name
     */
    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    /**
     * Sets executor service.
     *
     * @param executorService the executor service
     */
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * Shutdown executor service.
     */
    public void shutdownExecutorService() {
        getExecutorService().shutdown();
    }
}
