package org.recap.camel.datadump.consumer;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.impl.engine.DefaultFluentProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.recap.ScsbConstants;
import org.recap.camel.datadump.callable.BibEntityPreparerCallable;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.util.datadump.DataExportHeaderUtil;

import java.util.ArrayList;
import java.util.HashMap;
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
public class BibEntityGeneratorActiveMQConsumer {

    private BibliographicDetailsRepository bibliographicDetailsRepository;
    private ExecutorService executorService;
    private static String batchHeaderName = "batchHeaders";
    private Integer dataDumpBibEntityThreadSize;
    private Integer dataDumpBibEntityBatchSize;

    /**
     * Instantiates a new Bib entity generator active mq consumer.
     *
     * @param bibliographicDetailsRepository the bibliographic details repository
     */
    public BibEntityGeneratorActiveMQConsumer(BibliographicDetailsRepository bibliographicDetailsRepository) {
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;
    }

    /**
     * Instantiates a new Bib entity generator active mq consumer.
     *
     * @param bibliographicDetailsRepository the bibliographic details repository
     * @param dataDumpBibEntityThreadSize the data dump bib entity thread size
     * @param dataDumpBibEntityBatchSize the data dump bib entity batch size
     */
    public BibEntityGeneratorActiveMQConsumer(BibliographicDetailsRepository bibliographicDetailsRepository, Integer dataDumpBibEntityThreadSize, Integer dataDumpBibEntityBatchSize) {
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;
        this.dataDumpBibEntityThreadSize = dataDumpBibEntityThreadSize;
        this.dataDumpBibEntityBatchSize = dataDumpBibEntityBatchSize;
    }

    /**
     * This method is invoked by the route to build bibliographic entities using the data dump solr search results.
     * The bibliographic entities are then passed to another route for data dump export.
     *
     * @param exchange the exchange
     * @throws Exception the exception
     */
    public void processBibEntities(Exchange exchange) throws Exception {

        long startTime = System.currentTimeMillis();

        Map results = (Map) exchange.getIn().getBody();
        List<HashMap> dataDumpSearchResults = (List<HashMap>) results.get("dataDumpSearchResults");

        String batchHeaders = (String) exchange.getIn().getHeader(batchHeaderName);
        String currentPageCountStr = new DataExportHeaderUtil().getValueFor(batchHeaders, "currentPageCount");
        log.info("Current page in BibEntityGeneratorActiveMQConsumer--->{}",currentPageCountStr);
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();

        List<Integer> bibIdList = new ArrayList<>();
        Map<Integer, List<Integer>> bibItemMap = new HashMap<>();
        for (Iterator<HashMap> iterator = dataDumpSearchResults.iterator(); iterator.hasNext(); ) {
            HashMap hashMap = iterator.next();
            Integer bibId = (Integer) hashMap.get("bibId");
            List<Integer> itemIds = (List<Integer>) hashMap.get("itemIds");
            bibIdList.add(bibId);
            bibItemMap.put(bibId, itemIds);
        }

        if(CollectionUtils.isNotEmpty(bibIdList)) {
            List<Callable<BibliographicEntity>> callables = new ArrayList<>();
            List<BibliographicEntity> bibliographicEntityList=new ArrayList<>();

            List<List<Integer>> partition = Lists.partition(bibIdList, dataDumpBibEntityBatchSize);
            for (List<Integer> integers : partition) {
                List<BibliographicEntity> bibliographicEntityList1 = bibliographicDetailsRepository.getBibliographicEntityList(integers);
                bibliographicEntityList.addAll(bibliographicEntityList1);
            }

            for (Iterator<BibliographicEntity> iterator = bibliographicEntityList.iterator(); iterator.hasNext(); ) {
                BibliographicEntity bibliographicEntity = iterator.next();
                BibEntityPreparerCallable bibEntityPreparerCallable = new BibEntityPreparerCallable(bibliographicEntity, bibItemMap.get(bibliographicEntity.getId()));
                callables.add(bibEntityPreparerCallable);
            }

            List<Future<BibliographicEntity>> futureList = getExecutorService().invokeAll(callables);
            List<Future<BibliographicEntity>> collectedFutures = futureList.stream()
                    .map(this::getBibliographicEntityFuture)
                    .collect(Collectors.toCollection(ArrayList::new));


            for (Future<BibliographicEntity> future : collectedFutures) {
                bibliographicEntities.add(future.get());
            }

            long endTime = System.currentTimeMillis();

        log.info("Time taken to prepare {} bib entities is : {} seconds, solr result size {}" , bibliographicEntities.size() , (endTime - startTime) / 1000,dataDumpSearchResults.size());

        getExecutorService().shutdown();

        log.info("sending page count {} to marcrecord formatter route",currentPageCountStr);
            String currentPageCountStrbeforesendingToNxt = new DataExportHeaderUtil().getValueFor(batchHeaders, "currentPageCount");
            log.info("currentPageCountStrbeforesendingToNxt--->{}",currentPageCountStrbeforesendingToNxt);
            FluentProducerTemplate fluentProducerTemplate = DefaultFluentProducerTemplate.on(exchange.getContext());
            fluentProducerTemplate
                    .to(ScsbConstants.BIB_ENTITY_FOR_DATA_EXPORT_Q)
                    .withBody(bibliographicEntities)
                    .withHeader(batchHeaderName, exchange.getIn().getHeader(batchHeaderName))
                    .withHeader("exportFormat", exchange.getIn().getHeader("exportFormat"))
                    .withHeader("transmissionType", exchange.getIn().getHeader("transmissionType"));
            fluentProducerTemplate.send();
         }
    }

    private Future<BibliographicEntity> getBibliographicEntityFuture(Future<BibliographicEntity> future) {
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
     * Gets executor service.
     *
     * @return the executor service
     */
    public ExecutorService getExecutorService() {
        if (null == executorService) {
            log.info("Creating Thread Pool of Size : {}", dataDumpBibEntityThreadSize);
            executorService = Executors.newFixedThreadPool(dataDumpBibEntityThreadSize);
        }
        if (executorService.isShutdown()) {
            log.info("On Shutdown, Creating Thread Pool of Size : {}", dataDumpBibEntityThreadSize);
            executorService = Executors.newFixedThreadPool(dataDumpBibEntityThreadSize);
        }
        return executorService;
    }
}
