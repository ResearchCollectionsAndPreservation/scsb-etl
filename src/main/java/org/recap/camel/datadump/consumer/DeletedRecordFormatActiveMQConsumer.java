package org.recap.camel.datadump.consumer;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.impl.engine.DefaultFluentProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.report.CommonReportGenerator;
import org.recap.util.datadump.DataExportHeaderUtil;
import org.recap.camel.datadump.callable.DeletedRecordPreparerCallable;
import org.recap.model.export.DeletedRecord;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.service.formatter.datadump.DeletedJsonFormatterService;


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
public class DeletedRecordFormatActiveMQConsumer extends CommonReportGenerator {

    /**
     * The Deleted json formatter service.
     */
    DeletedJsonFormatterService deletedJsonFormatterService;

    private ExecutorService executorService;
    private DataExportHeaderUtil dataExportHeaderUtil;
    private Integer dataDumpDeletedRecordsThreadSize;
    private Integer dataDumpDeletedRecordsBatchSize;

    /**
     * Instantiates a new Deleted record format active mq consumer.
     *
     * @param deletedJsonFormatterService the deleted json formatter service
     */
    public DeletedRecordFormatActiveMQConsumer(DeletedJsonFormatterService deletedJsonFormatterService) {
        this.deletedJsonFormatterService = deletedJsonFormatterService;
    }

    /**
     * Instantiates a new Deleted record format active mq consumer.
     *
     * @param deletedJsonFormatterService the deleted json formatter service
     * @param dataDumpDeletedRecordsThreadSize the data Dump Deleted Records Thread Size
     * @param dataDumpDeletedRecordsBatchSize the data Dump Deleted Records Batch Size
     */
    public DeletedRecordFormatActiveMQConsumer(DeletedJsonFormatterService deletedJsonFormatterService, Integer dataDumpDeletedRecordsThreadSize, Integer dataDumpDeletedRecordsBatchSize) {
        this.deletedJsonFormatterService = deletedJsonFormatterService;
        this.dataDumpDeletedRecordsThreadSize = dataDumpDeletedRecordsThreadSize;
        this.dataDumpDeletedRecordsBatchSize = dataDumpDeletedRecordsBatchSize;
    }

    /**
     * This method is invoked by the route to prepare deleted record list for data export.
     *
     * @param exchange the exchange
     * @throws Exception the exception
     */
    public void processRecords(Exchange exchange) throws Exception {
        FluentProducerTemplate fluentProducerTemplate = DefaultFluentProducerTemplate.on(exchange.getContext());

        List<DeletedRecord> deletedRecordList = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        List<BibliographicEntity> bibliographicEntities = (List<BibliographicEntity>) exchange.getIn().getBody();

        List<Callable<DeletedRecord>> callables = new ArrayList<>();

        List<List<BibliographicEntity>> partitionList = Lists.partition(bibliographicEntities, dataDumpDeletedRecordsBatchSize);

        for (Iterator<List<BibliographicEntity>> iterator = partitionList.iterator(); iterator.hasNext(); ) {
            List<BibliographicEntity> bibliographicEntityList = iterator.next();

            DeletedRecordPreparerCallable scsbRecordPreparerCallable =
                    new DeletedRecordPreparerCallable(bibliographicEntityList, deletedJsonFormatterService);

            callables.add(scsbRecordPreparerCallable);
        }

        List<Future<DeletedRecord>> futureList = getExecutorService().invokeAll(callables);
        List<Future<DeletedRecord>> collectedFutures = futureList.stream()
                .map(this::getDeletedRecordFuture)
                .collect(Collectors.toCollection(ArrayList::new));
        List<Integer> itemExportedCountList = new ArrayList<>();
        List failures = new ArrayList<>();
        for (Future<DeletedRecord> future : collectedFutures) {
            Map<String, Object> results = (Map<String, Object>) future.get();
            Collection<? extends DeletedRecord> successRecords = (Collection<? extends DeletedRecord>) results.get(ScsbCommonConstants.SUCCESS);
            if (CollectionUtils.isNotEmpty(successRecords)) {
                deletedRecordList.addAll(successRecords);
            }
            Collection failureRecords = (Collection) results.get(ScsbCommonConstants.FAILURE);
            if (CollectionUtils.isNotEmpty(failureRecords)) {
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
        String batchHeaders = (String) exchange.getIn().getHeader(ScsbConstants.BATCH_HEADERS);
        String requestId = getDataExportHeaderUtil().getValueFor(batchHeaders, "requestId");
        if(CollectionUtils.isNotEmpty(failures)) {
            processFailures(exchange, failures, batchHeaders, requestId);
        }

        long endTime = System.currentTimeMillis();

        log.info("Time taken to prepare {} deleted records :  {} seconds " ,bibliographicEntities.size() , (endTime - startTime) / 1000 );

        fluentProducerTemplate
                .to(ScsbConstants.DELETED_JSON_RECORD_FOR_DATA_EXPORT_Q)
                .withBody(deletedRecordList)
                .withHeader(ScsbConstants.BATCH_HEADERS, exchange.getIn().getHeader(ScsbConstants.BATCH_HEADERS))
                .withHeader(ScsbConstants.EXPORT_FORMAT, exchange.getIn().getHeader(ScsbConstants.EXPORT_FORMAT))
                .withHeader(ScsbConstants.TRANSMISSION_TYPE, exchange.getIn().getHeader(ScsbConstants.TRANSMISSION_TYPE))
                .withHeader(ScsbConstants.ITEM_EXPORTED_COUNT,itemExportedCount);
        fluentProducerTemplate.send();
    }

    private Future<DeletedRecord> getDeletedRecordFuture(Future<DeletedRecord> future) {
        try {
             future.get();
             return future;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Process the failure records for deleted records export.
     * @param exchange
     * @param failures
     * @param batchHeaders
     * @param requestId
     */
    private void processFailures(Exchange exchange, List failures, String batchHeaders, String requestId) {
        processRecordFailures(exchange, failures, batchHeaders, requestId, getDataExportHeaderUtil());
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


    /**
     * Gets executor service.
     *
     * @return the executor service
     */
    public ExecutorService getExecutorService() {
        if (null == executorService) {
            log.info("Creating Thread Pool of Size : {}", dataDumpDeletedRecordsThreadSize);
            executorService = Executors.newFixedThreadPool(dataDumpDeletedRecordsThreadSize);
        }
        if (executorService.isShutdown()) {
            log.info("On Shutdown, Creating Thread Pool of Size : {}", dataDumpDeletedRecordsThreadSize);
            executorService = Executors.newFixedThreadPool(dataDumpDeletedRecordsThreadSize);
        }
        return executorService;
    }

}

