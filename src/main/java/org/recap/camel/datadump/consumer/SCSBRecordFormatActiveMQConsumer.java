package org.recap.camel.datadump.consumer;

import com.google.common.collect.Lists;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.impl.engine.DefaultFluentProducerTemplate;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.camel.datadump.callable.BibRecordPreparerCallable;
import org.recap.model.jaxb.BibRecord;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.report.CommonReportGenerator;
import org.recap.service.formatter.datadump.SCSBXmlFormatterService;
import org.recap.util.datadump.DataExportHeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by peris on 11/1/16.
 */
public class SCSBRecordFormatActiveMQConsumer extends CommonReportGenerator {

    private static final Logger logger = LoggerFactory.getLogger(SCSBRecordFormatActiveMQConsumer.class);

    /**
     * The Scsb xml formatter service.
     */
    SCSBXmlFormatterService scsbXmlFormatterService;
    private ExecutorService executorService;
    private DataExportHeaderUtil dataExportHeaderUtil;

    /**
     * Instantiates a new Scsb record format active mq consumer.
     *
     * @param scsbXmlFormatterService the scsb xml formatter service
     */
    public SCSBRecordFormatActiveMQConsumer(SCSBXmlFormatterService scsbXmlFormatterService) {
        this.scsbXmlFormatterService = scsbXmlFormatterService;
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

        List<List<BibliographicEntity>> partitionList = Lists.partition(bibliographicEntities, 1000);

        for (Iterator<List<BibliographicEntity>> iterator = partitionList.iterator(); iterator.hasNext(); ) {
            List<BibliographicEntity> bibliographicEntityList = iterator.next();
            BibRecordPreparerCallable scsbRecordPreparerCallable =
                    new BibRecordPreparerCallable(bibliographicEntityList, scsbXmlFormatterService);
            callables.add(scsbRecordPreparerCallable);
        }

        List<Future<Map<String, Object>>> futureList = getExecutorService().invokeAll(callables);
        futureList.stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException  e) {
                        logger.error(RecapConstants.ERROR,e);
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                    catch (ExecutionException e) {
                        logger.error(RecapConstants.ERROR,e);
                        throw new RuntimeException(e);
                    }
                });
        List<Integer> itemExportedCountList = new ArrayList<>();
        List failures = new ArrayList();
        for (Future future : futureList) {
            Map<String, Object> results = (Map<String, Object>) future.get();
            Collection<? extends BibRecord> successRecords = (Collection<? extends BibRecord>) results.get(RecapCommonConstants.SUCCESS);
            if (!CollectionUtils.isEmpty(successRecords)) {
                records.addAll(successRecords);
            }
            Collection failureRecords = (Collection) results.get(RecapCommonConstants.FAILURE);
            if (!CollectionUtils.isEmpty(failureRecords)) {
                failures.addAll(failureRecords);
            }
            Integer itemCount = (Integer) results.get(RecapConstants.ITEM_EXPORTED_COUNT);
            if (itemCount !=0 && itemCount != null){
                itemExportedCountList.add(itemCount);
            }
        }
        Integer itemExportedCount = 0;
        for (Integer itemCount : itemExportedCountList) {
            itemExportedCount = itemExportedCount + itemCount;
        }
        String batchHeaders = (String) exchange.getIn().getHeader(RecapConstants.BATCH_HEADERS);
        String requestId = getDataExportHeaderUtil().getValueFor(batchHeaders, "requestId");
        FluentProducerTemplate fluentProducerTemplate = DefaultFluentProducerTemplate.on(exchange.getContext());
        processFailures(failures, batchHeaders, requestId, fluentProducerTemplate);

        long endTime = System.currentTimeMillis();

        logger.info("Time taken to prepare {} scsb records : {} seconds " , bibliographicEntities.size() , (endTime - startTime) / 1000 );


        fluentProducerTemplate
                .to(RecapConstants.SCSB_RECORD_FOR_DATA_EXPORT_Q)
                .withBody(records)
                .withHeader(RecapConstants.BATCH_HEADERS, exchange.getIn().getHeader(RecapConstants.BATCH_HEADERS))
                .withHeader("exportFormat", exchange.getIn().getHeader("exportFormat"))
                .withHeader("transmissionType", exchange.getIn().getHeader("transmissionType"))
                .withHeader(RecapConstants.ITEM_EXPORTED_COUNT,itemExportedCount);
        fluentProducerTemplate.send();
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
            values.put(RecapConstants.NUM_RECORDS, String.valueOf(failures.size()));
            values.put(RecapConstants.FAILURE_CAUSE,failures.get(0));
            fluentProducerTemplate
                    .to(RecapConstants.DATADUMP_FAILURE_REPORT_Q)
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
            executorService = Executors.newFixedThreadPool(500);
        }
        if (executorService.isShutdown()) {
            executorService = Executors.newFixedThreadPool(500);
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

