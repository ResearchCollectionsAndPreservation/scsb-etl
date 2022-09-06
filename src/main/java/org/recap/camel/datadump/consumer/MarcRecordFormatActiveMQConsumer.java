package org.recap.camel.datadump.consumer;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.impl.engine.DefaultFluentProducerTemplate;
import org.marc4j.marc.Record;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.report.CommonReportGenerator;
import org.recap.util.datadump.DataExportHeaderUtil;
import org.recap.camel.datadump.callable.MarcRecordPreparerCallable;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.service.formatter.datadump.MarcXmlFormatterService;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Created by peris on 11/1/16.
 */
@Slf4j
public class MarcRecordFormatActiveMQConsumer extends CommonReportGenerator {

    /**
     * The Marc xml formatter service.
     */
    MarcXmlFormatterService marcXmlFormatterService;
    private ExecutorService executorService;
    private DataExportHeaderUtil dataExportHeaderUtil;
    private Integer dataDumpMarcFormatThreadSize;
    private Integer dataDumpMarcFormatBatchSize;

    /**
     * Instantiates a new Marc record format active mq consumer.
     *
     * @param marcXmlFormatterService the marc xml formatter service
     */
    public MarcRecordFormatActiveMQConsumer(MarcXmlFormatterService marcXmlFormatterService) {
        this.marcXmlFormatterService = marcXmlFormatterService;
    }

    /**
     * Instantiates a new Marc record format active mq consumer.
     *
     * @param marcXmlFormatterService the marc xml formatter service
     * @param dataDumpMarcFormatThreadSize the data Dump Marc Format Thread Size
     * @param dataDumpMarcFormatBatchSize the data Dump Marc Format Batch Size
     */
    public MarcRecordFormatActiveMQConsumer(MarcXmlFormatterService marcXmlFormatterService, Integer dataDumpMarcFormatThreadSize, Integer dataDumpMarcFormatBatchSize) {
        this.marcXmlFormatterService = marcXmlFormatterService;
        this.dataDumpMarcFormatThreadSize = dataDumpMarcFormatThreadSize;
        this.dataDumpMarcFormatBatchSize = dataDumpMarcFormatBatchSize;
    }

    /**
     * This method is invoked by the route to prepare marc records for data export.
     *
     * @param exchange the exchange
     * @throws Exception the exception
     */
    public void processRecords(Exchange exchange) throws Exception {
        FluentProducerTemplate fluentProducerTemplate = DefaultFluentProducerTemplate.on(exchange.getContext());
        String batchHeaders = (String) exchange.getIn().getHeader(ScsbConstants.BATCH_HEADERS);
        String currentPageCountStr = new DataExportHeaderUtil().getValueFor(batchHeaders, "currentPageCount");
        log.info("Current page in MarcRecordFormatActiveMQConsumer--->{}",currentPageCountStr);

        List<Record> records = new ArrayList<>();

        long startTime = System.currentTimeMillis();


        List<BibliographicEntity> bibliographicEntities = (List<BibliographicEntity>) exchange.getIn().getBody();

        List<Callable<Map<String, Object>>> callables = new ArrayList<>();

        List<List<BibliographicEntity>> partitionList = Lists.partition(bibliographicEntities, dataDumpMarcFormatBatchSize);

        for (Iterator<List<BibliographicEntity>> iterator = partitionList.iterator(); iterator.hasNext(); ) {
            List<BibliographicEntity> bibliographicEntityList = iterator.next();
            MarcRecordPreparerCallable marcRecordPreparerCallable =
                    new MarcRecordPreparerCallable(bibliographicEntityList, marcXmlFormatterService);
            callables.add(marcRecordPreparerCallable);
        }

        List<Future<Map<String, Object>>> futureList = getExecutorService().invokeAll(callables);
        List<Future<Map<String, Object>>> collectedFutures = futureList.stream()
                .map(this::getMapFuture)
                .collect(Collectors.toCollection(ArrayList::new));
        List<Integer> itemExportedCountList = new ArrayList<>();
        List failures = new ArrayList();
        for (Future future : collectedFutures) {
            Map<String, Object> results = (Map<String, Object>) future.get();
            Collection<? extends Record> successRecords = (Collection<? extends Record>) results.get(ScsbCommonConstants.SUCCESS);
            if (!CollectionUtils.isEmpty(successRecords)) {
                records.addAll(successRecords);
            }
            Collection failureRecords = (Collection) results.get(ScsbCommonConstants.FAILURE);
            if (!CollectionUtils.isEmpty(failureRecords)) {
                failures.addAll(failureRecords);
            }
            Integer itemCount = (Integer) results.get(ScsbConstants.ITEM_EXPORTED_COUNT);
            if (itemCount !=0 && itemCount != null){
                itemExportedCountList.add(itemCount);
            }
        }
        Integer itemExportedCount = 0;
        for (Integer itemCount : itemExportedCountList) {
            itemExportedCount = itemExportedCount + itemCount;
        }
        String requestId = getDataExportHeaderUtil().getValueFor(batchHeaders, "requestId");
        processFailures(exchange, failures, batchHeaders, requestId);

        long endTime = System.currentTimeMillis();

        log.info("Time taken to prepare {} marc records : {} seconds " , bibliographicEntities.size() , (endTime - startTime) / 1000);

        fluentProducerTemplate
                .to(ScsbConstants.MARC_RECORD_FOR_DATA_EXPORT_Q)
                .withBody(records)
                .withHeader(ScsbConstants.BATCH_HEADERS, exchange.getIn().getHeader(ScsbConstants.BATCH_HEADERS))
                .withHeader(ScsbConstants.EXPORT_FORMAT, exchange.getIn().getHeader(ScsbConstants.EXPORT_FORMAT))
                .withHeader(ScsbConstants.TRANSMISSION_TYPE, exchange.getIn().getHeader(ScsbConstants.TRANSMISSION_TYPE))
                .withHeader(ScsbConstants.ITEM_EXPORTED_COUNT,itemExportedCount);
        fluentProducerTemplate.send();
    }

    private Future<Map<String, Object>> getMapFuture(Future<Map<String, Object>> future) {
        try {
             future.get();
             return future;
        } catch (InterruptedException  e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Process the failure records for marc records export.
     * @param exchange
     * @param failures
     * @param batchHeaders
     * @param requestId
     */
    private void processFailures(Exchange exchange, List failures, String batchHeaders, String requestId) {
        if (!CollectionUtils.isEmpty(failures)) {
            dataExportHeaderUtil = getDataExportHeaderUtil();
            processRecordFailures(exchange, failures, batchHeaders, requestId, dataExportHeaderUtil);
        }
    }

    /**
     * Gets executor service.
     *
     * @return the executor service
     */
    public ExecutorService getExecutorService() {
        if (null == executorService) {
            log.info("Creating Thread Pool of Size : {}", dataDumpMarcFormatThreadSize);
            executorService = Executors.newFixedThreadPool(dataDumpMarcFormatThreadSize);
        }
        if (executorService.isShutdown()) {
            log.info("On Shutdown, Creating Thread Pool of Size : {}", dataDumpMarcFormatThreadSize);
            executorService = Executors.newFixedThreadPool(dataDumpMarcFormatThreadSize);
        }
        return executorService;
    }

    /**
     * Gets data export header util.
     *
     * @return the data export header util
     */
    public DataExportHeaderUtil getDataExportHeaderUtil() {
        if (null == dataExportHeaderUtil) {
            dataExportHeaderUtil = new DataExportHeaderUtil();
        }
        return dataExportHeaderUtil;
    }

    /**
     * Sets data export header util.
     *
     * @param dataExportHeaderUtil the data export header util
     */
    public void setDataExportHeaderUtil(DataExportHeaderUtil dataExportHeaderUtil) {
        this.dataExportHeaderUtil = dataExportHeaderUtil;
    }

}

