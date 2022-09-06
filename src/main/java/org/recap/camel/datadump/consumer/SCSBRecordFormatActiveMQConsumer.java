package org.recap.camel.datadump.consumer;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.impl.engine.DefaultFluentProducerTemplate;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.camel.datadump.callable.BibRecordPreparerCallable;
import org.recap.model.jaxb.BibRecord;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.report.CommonReportGenerator;
import org.recap.service.formatter.datadump.SCSBXmlFormatterService;
import org.recap.util.datadump.DataExportHeaderUtil;

import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by peris on 11/1/16.
 */
@Slf4j
public class SCSBRecordFormatActiveMQConsumer extends CommonReportGenerator {


    /**
     * The Scsb xml formatter service.
     */
    SCSBXmlFormatterService scsbXmlFormatterService;
    private ExecutorService executorService;
    private DataExportHeaderUtil dataExportHeaderUtil;
    private Integer dataDumpScsbFormatThreadSize;
    private Integer dataDumpScsbFormatBatchSize;

    /**
     * Instantiates a new Scsb record format active mq consumer.
     *
     * @param scsbXmlFormatterService the scsb xml formatter service
     */
    public SCSBRecordFormatActiveMQConsumer(SCSBXmlFormatterService scsbXmlFormatterService) {
        this.scsbXmlFormatterService = scsbXmlFormatterService;
    }

    /**
     * Instantiates a new Scsb record format active mq consumer.
     *
     * @param scsbXmlFormatterService      the scsb xml formatter service
     * @param dataDumpScsbFormatThreadSize the data Dump Scsb Format Thread Size
     * @param dataDumpScsbFormatBatchSize the data Dump Scsb Format Batch Size
     */
    public SCSBRecordFormatActiveMQConsumer(SCSBXmlFormatterService scsbXmlFormatterService, Integer dataDumpScsbFormatThreadSize, Integer dataDumpScsbFormatBatchSize) {
        this.scsbXmlFormatterService = scsbXmlFormatterService;
        this.dataDumpScsbFormatThreadSize = dataDumpScsbFormatThreadSize;
        this.dataDumpScsbFormatBatchSize = dataDumpScsbFormatBatchSize;
    }

    /**
     * This method is invoked by the route to prepare bib records for data export.
     *
     * @param exchange the exchange
     * @throws Exception the exception
     */
    public void processRecords(Exchange exchange) throws Exception {


        List<BibRecord> records = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        List<BibliographicEntity> bibliographicEntities = (List<BibliographicEntity>) exchange.getIn().getBody();

        List<Callable<Map<String, Object>>> callables = new ArrayList<>();

        List<List<BibliographicEntity>> partitionList = Lists.partition(bibliographicEntities, dataDumpScsbFormatBatchSize);

        for (Iterator<List<BibliographicEntity>> iterator = partitionList.iterator(); iterator.hasNext(); ) {
            List<BibliographicEntity> bibliographicEntityList = iterator.next();
            BibRecordPreparerCallable scsbRecordPreparerCallable =
                    new BibRecordPreparerCallable(bibliographicEntityList, scsbXmlFormatterService);
            callables.add(scsbRecordPreparerCallable);
        }

        List<Future<Map<String, Object>>> futureList = getExecutorService().invokeAll(callables);
        List<Future<Map<String, Object>>> collectedFutures = futureList.stream()
                .map(this::getMapFuture)
                .collect(Collectors.toCollection(ArrayList::new));
        List<Integer> itemExportedCountList = new ArrayList<>();
        List failures = new ArrayList();
        for (Future future : collectedFutures) {
            Map<String, Object> results = (Map<String, Object>) future.get();
            Collection<? extends BibRecord> successRecords = (Collection<? extends BibRecord>) results.get(ScsbCommonConstants.SUCCESS);
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
        String batchHeaders = (String) exchange.getIn().getHeader(ScsbConstants.BATCH_HEADERS);
        String requestId = getDataExportHeaderUtil().getValueFor(batchHeaders, "requestId");
        FluentProducerTemplate fluentProducerTemplate = DefaultFluentProducerTemplate.on(exchange.getContext());
        processFailures(failures, batchHeaders, requestId, fluentProducerTemplate);

        long endTime = System.currentTimeMillis();

        log.info("Time taken to prepare {} scsb records : {} seconds " , bibliographicEntities.size() , (endTime - startTime) / 1000 );

        if(!records.isEmpty()) {
            fluentProducerTemplate
                    .to(ScsbConstants.SCSB_RECORD_FOR_DATA_EXPORT_Q)
                    .withBody(records)
                    .withHeader(ScsbConstants.BATCH_HEADERS, exchange.getIn().getHeader(ScsbConstants.BATCH_HEADERS))
                    .withHeader("exportFormat", exchange.getIn().getHeader(ScsbConstants.EXPORT_FORMAT))
                    .withHeader("transmissionType", exchange.getIn().getHeader(ScsbConstants.TRANSMISSION_TYPE))
                    .withHeader(ScsbConstants.ITEM_EXPORTED_COUNT, itemExportedCount);
            fluentProducerTemplate.send();
        }
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
     * Process the failure records for bib records export.
     * @param failures
     * @param batchHeaders
     * @param requestId
     * @param fluentProducerTemplate
     */
    private void processFailures(List failures, String batchHeaders, String requestId, FluentProducerTemplate fluentProducerTemplate) {
        if (!CollectionUtils.isEmpty(failures)) {
            dataExportHeaderUtil = getDataExportHeaderUtil();
            Map values = processReport(batchHeaders, requestId, dataExportHeaderUtil);
            values.put(ScsbConstants.NUM_RECORDS, String.valueOf(failures.size()));
            values.put(ScsbConstants.FAILURE_CAUSE,failures.get(0));
            values.put(ScsbConstants.FAILURE_LIST,failures);
            fluentProducerTemplate
                    .to(ScsbConstants.DATADUMP_FAILURE_REPORT_Q)
                    .withBody(values);

            fluentProducerTemplate.send();
        }
    }

    /**
     * Gets executor service.
     *
     * @return the executor service
     */
    public ExecutorService getExecutorService() {
        if (null == executorService) {
            log.info("Creating Thread Pool of Size : {}", dataDumpScsbFormatThreadSize);
            executorService = Executors.newFixedThreadPool(dataDumpScsbFormatThreadSize);
        }
        if (executorService.isShutdown()) {
            log.info("On Shutdown, Creating Thread Pool of Size : {}", dataDumpScsbFormatThreadSize);
            executorService = Executors.newFixedThreadPool(dataDumpScsbFormatThreadSize);
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

