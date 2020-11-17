package org.recap.camel;

import org.apache.camel.ProducerTemplate;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;
import org.recap.model.jpa.XmlRecordEntity;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.repository.HoldingsDetailsRepository;
import org.recap.repository.ItemDetailsRepository;
import org.recap.repository.XmlRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by rajeshbabuk on 18/7/16.
 */
@Component
public class EtlDataLoadProcessor {

    private static final Logger logger = LoggerFactory.getLogger(EtlDataLoadProcessor.class);

    private Integer batchSize;
    private String fileName;
    private String institutionName;

    private RecordProcessor recordProcessor;

    /**
     * The Producer.
     */
    ProducerTemplate producer;

    private XmlRecordRepository xmlRecordRepository;
    private ItemDetailsRepository itemDetailsRepository;
    private HoldingsDetailsRepository holdingsDetailsRepository;
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    /**
     * Starts initial data load process.
     */
    public void startLoadProcess() {
        List distinctFileNames = xmlRecordRepository.findDistinctFileNames();
        long totalStartTime = System.currentTimeMillis();
        for (Iterator iterator = distinctFileNames.iterator(); iterator.hasNext(); ) {
            String distinctFileName = (String) iterator.next();
            if (distinctFileName.contains(fileName)) {
                Integer instIdByFileName = xmlRecordRepository.findInstIdByFileNames(distinctFileName);
                long oldBibsCount = bibliographicDetailsRepository.countByOwningInstitutionId(instIdByFileName);
                long oldHoldingsCount = holdingsDetailsRepository.countByOwningInstitutionId(instIdByFileName);
                long oldItemsCount = itemDetailsRepository.countByOwningInstitutionId(instIdByFileName);
                long oldBibHoldingsCount = bibliographicDetailsRepository.findCountOfBibliographicHoldingsByInstId(instIdByFileName);
                long oldBibItemsCount = itemDetailsRepository.findCountOfBibliographicItemsByInstId(instIdByFileName);
                long totalDocCount;

                totalDocCount = xmlRecordRepository.countByXmlFileName(distinctFileName);

                if (totalDocCount > 0) {
                    int quotient = Integer.valueOf(Long.toString(totalDocCount)) / (batchSize);
                    int remainder = Integer.valueOf(Long.toString(totalDocCount)) % (batchSize);

                    int loopCount = remainder == 0 ? quotient : quotient + 1;

                    Page<XmlRecordEntity> xmlRecordEntities = null;
                    for (int iteration = 0; iteration < loopCount; iteration++) {
                        long startTime = System.currentTimeMillis();
                        xmlRecordEntities = xmlRecordRepository.findByXmlFileName(PageRequest.of(iteration, batchSize), distinctFileName);
                        recordProcessor.setXmlFileName(distinctFileName);
                        recordProcessor.setInstitutionName(institutionName);
                        recordProcessor.process(xmlRecordEntities);
                        long endTime = System.currentTimeMillis();
                        logger.info("File name : {} , Total Docs : {}, Total Loops : {}, Current Iteration : {} , Time taken to save: {} bibs and related data is: {} seconds." ,
                                distinctFileName, totalDocCount, loopCount, iteration, xmlRecordEntities.getNumberOfElements() , (endTime - startTime) / 1000 );
                    }


                    long totalEndTime = System.currentTimeMillis();
                    if(xmlRecordEntities != null) {
                        logger.info("File name : {} , Total time taken to save: {} bibs and related data is: {} seconds.", distinctFileName, xmlRecordEntities.getTotalElements(), (totalEndTime - totalStartTime) / 1000);
                    }
                    else {
                        logger.info("File name : {} , Total time taken to save: zero bibs and related data is: {} seconds.", distinctFileName,  (totalEndTime - totalStartTime) / 1000);
                    }
                    } else {
                    logger.info("No records found to load into DB");
                }

                generateSuccessReport(oldBibsCount, oldHoldingsCount, oldItemsCount, distinctFileName, oldBibHoldingsCount, oldBibItemsCount, instIdByFileName);
            }
        }
        recordProcessor.shutdownExecutorService();
    }

    /**
     * Generates success report for the initial data load.
     * 
     * @param oldBibsCount
     * @param oldHoldingsCount
     * @param oldItemsCount
     * @param fileName
     * @param oldBibHoldingsCount
     * @param oldBibItemsCount
     * @param instIdByFileName
     */
    private void generateSuccessReport(long oldBibsCount, long oldHoldingsCount, long oldItemsCount, String fileName, long oldBibHoldingsCount, long oldBibItemsCount, Integer instIdByFileName) {
        ReportEntity reportEntity = new ReportEntity();
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        long newBibsCount = bibliographicDetailsRepository.countByOwningInstitutionId(instIdByFileName);
        long newHoldingsCount = holdingsDetailsRepository.countByOwningInstitutionId(instIdByFileName);
        long newItemsCount = itemDetailsRepository.countByOwningInstitutionId(instIdByFileName);
        long newBibHoldingsCount = bibliographicDetailsRepository.findCountOfBibliographicHoldingsByInstId(instIdByFileName);
        long newBibItemsCount = itemDetailsRepository.findCountOfBibliographicItemsByInstId(instIdByFileName);
        Integer processedBibsCount = Integer.valueOf(Long.toString(newBibsCount)) - Integer.valueOf(Long.toString(oldBibsCount));
        Integer processedHoldingsCount = Integer.valueOf(Long.toString(newHoldingsCount)) - Integer.valueOf(Long.toString(oldHoldingsCount));
        Integer processedItemsCount = Integer.valueOf(Long.toString(newItemsCount)) - Integer.valueOf(Long.toString(oldItemsCount));
        Integer processedBibHoldingsCount = Integer.valueOf(Long.toString(newBibHoldingsCount)) - Integer.valueOf(Long.toString(oldBibHoldingsCount));
        Integer processedBibItemsCount = Integer.valueOf(Long.toString(newBibItemsCount)) - Integer.valueOf(Long.toString(oldBibItemsCount));
        Integer totalRecordsInfile = Integer.valueOf(Long.toString(xmlRecordRepository.countByXmlFileName(fileName)));

        ReportDataEntity totalRecordsInFileEntity = new ReportDataEntity();
        totalRecordsInFileEntity.setHeaderName(RecapConstants.TOTAL_RECORDS_IN_FILE);
        totalRecordsInFileEntity.setHeaderValue(String.valueOf(totalRecordsInfile));
        reportDataEntities.add(totalRecordsInFileEntity);

        ReportDataEntity totalBibsLoadedEntity = new ReportDataEntity();
        totalBibsLoadedEntity.setHeaderName(RecapConstants.TOTAL_BIBS_LOADED);
        totalBibsLoadedEntity.setHeaderValue(String.valueOf(processedBibsCount));
        reportDataEntities.add(totalBibsLoadedEntity);

        ReportDataEntity totalHoldingsLoadedEntity = new ReportDataEntity();
        totalHoldingsLoadedEntity.setHeaderName(RecapConstants.TOTAL_HOLDINGS_LOADED);
        totalHoldingsLoadedEntity.setHeaderValue(String.valueOf(processedHoldingsCount));
        reportDataEntities.add(totalHoldingsLoadedEntity);

        ReportDataEntity totalItemsLoadedEntity = new ReportDataEntity();
        totalItemsLoadedEntity.setHeaderName(RecapConstants.TOTAL_ITEMS_LOADED);
        totalItemsLoadedEntity.setHeaderValue(String.valueOf(processedItemsCount));
        reportDataEntities.add(totalItemsLoadedEntity);

        ReportDataEntity totalBibHoldingsLoadedEntity = new ReportDataEntity();
        totalBibHoldingsLoadedEntity.setHeaderName(RecapConstants.TOTAL_BIB_HOLDINGS_LOADED);
        totalBibHoldingsLoadedEntity.setHeaderValue(String.valueOf(processedBibHoldingsCount));
        reportDataEntities.add(totalBibHoldingsLoadedEntity);

        ReportDataEntity totalBiBItemsLoadedEntity = new ReportDataEntity();
        totalBiBItemsLoadedEntity.setHeaderName(RecapConstants.TOTAL_BIB_ITEMS_LOADED);
        totalBiBItemsLoadedEntity.setHeaderValue(String.valueOf(processedBibItemsCount));
        reportDataEntities.add(totalBiBItemsLoadedEntity);

        ReportDataEntity fileNameEntity = new ReportDataEntity();
        fileNameEntity.setHeaderName(RecapCommonConstants.FILE_NAME);
        fileNameEntity.setHeaderValue(fileName);
        reportDataEntities.add(fileNameEntity);

        reportEntity.setFileName(fileName);
        reportEntity.setCreatedDate(new Date());
        reportEntity.setType(RecapCommonConstants.SUCCESS);
        reportEntity.setReportDataEntities(reportDataEntities);
        reportEntity.setInstitutionName(institutionName);

        producer.sendBody(RecapCommonConstants.REPORT_Q, reportEntity);
    }

    /**
     * Gets producer.
     *
     * @return the producer
     */
    public ProducerTemplate getProducer() {
        return producer;
    }

    /**
     * Sets producer.
     *
     * @param producer the producer
     */
    public void setProducer(ProducerTemplate producer) {
        this.producer = producer;
    }

    /**
     * Gets item details repository.
     *
     * @return the item details repository
     */
    public ItemDetailsRepository getItemDetailsRepository() {
        return itemDetailsRepository;
    }

    /**
     * Sets item details repository.
     *
     * @param itemDetailsRepository the item details repository
     */
    public void setItemDetailsRepository(ItemDetailsRepository itemDetailsRepository) {
        this.itemDetailsRepository = itemDetailsRepository;
    }

    /**
     * Gets holdings details repository.
     *
     * @return the holdings details repository
     */
    public HoldingsDetailsRepository getHoldingsDetailsRepository() {
        return holdingsDetailsRepository;
    }

    /**
     * Sets holdings details repository.
     *
     * @param holdingsDetailsRepository the holdings details repository
     */
    public void setHoldingsDetailsRepository(HoldingsDetailsRepository holdingsDetailsRepository) {
        this.holdingsDetailsRepository = holdingsDetailsRepository;
    }

    /**
     * Gets bibliographic details repository.
     *
     * @return the bibliographic details repository
     */
    public BibliographicDetailsRepository getBibliographicDetailsRepository() {
        return bibliographicDetailsRepository;
    }

    /**
     * Sets bibliographic details repository.
     *
     * @param bibliographicDetailsRepository the bibliographic details repository
     */
    public void setBibliographicDetailsRepository(BibliographicDetailsRepository bibliographicDetailsRepository) {
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;
    }

    /**
     * Gets batch size.
     *
     * @return the batch size
     */
    public Integer getBatchSize() {
        return batchSize;
    }

    /**
     * Sets batch size.
     *
     * @param batchSize the batch size
     */
    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    /**
     * Gets file name.
     *
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets file name.
     *
     * @param fileName the file name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
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
     * Gets xml record repository.
     *
     * @return the xml record repository
     */
    public XmlRecordRepository getXmlRecordRepository() {
        return xmlRecordRepository;
    }

    /**
     * Sets xml record repository.
     *
     * @param xmlRecordRepository the xml record repository
     */
    public void setXmlRecordRepository(XmlRecordRepository xmlRecordRepository) {
        this.xmlRecordRepository = xmlRecordRepository;
    }

    /**
     * Gets record processor.
     *
     * @return the record processor
     */
    public RecordProcessor getRecordProcessor() {
        return recordProcessor;
    }

    /**
     * Sets record processor.
     *
     * @param recordProcessor the record processor
     */
    public void setRecordProcessor(RecordProcessor recordProcessor) {
        this.recordProcessor = recordProcessor;
    }
}
