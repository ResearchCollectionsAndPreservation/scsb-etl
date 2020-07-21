package org.recap.camel.datadump.consumer;

import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.impl.engine.DefaultFluentProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.recap.RecapConstants;
import org.recap.camel.datadump.callable.BibEntityPreparerCallable;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.util.datadump.DataExportHeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * Created by peris on 11/1/16.
 */
public class BibEntityGeneratorActiveMQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(BibEntityGeneratorActiveMQConsumer.class);

    private BibliographicDetailsRepository bibliographicDetailsRepository;
    private ExecutorService executorService;
    private static String batchHeaderName = "batchHeaders";

    /**
     * Instantiates a new Bib entity generator active mq consumer.
     *
     * @param bibliographicDetailsRepository the bibliographic details repository
     */
    public BibEntityGeneratorActiveMQConsumer(BibliographicDetailsRepository bibliographicDetailsRepository) {
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;
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
        logger.info("Current page in BibEntityGeneratorActiveMQConsumer--->{}",currentPageCountStr);
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
            List<BibliographicEntity> bibliographicEntityList = bibliographicDetailsRepository.getBibliographicEntityList(bibIdList);

            List<Callable<BibliographicEntity>> callables = new ArrayList<>();

            for (Iterator<BibliographicEntity> iterator = bibliographicEntityList.iterator(); iterator.hasNext(); ) {
                BibliographicEntity bibliographicEntity = iterator.next();
                BibEntityPreparerCallable bibEntityPreparerCallable = new BibEntityPreparerCallable(bibliographicEntity, bibItemMap.get(bibliographicEntity.getBibliographicId()));
                callables.add(bibEntityPreparerCallable);
            }

            List<Future<BibliographicEntity>> futureList = getExecutorService().invokeAll(callables);
            futureList.stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            logger.error(RecapConstants.ERROR,e);
                            throw new RuntimeException(e);
                        }
                        catch (ExecutionException e) {
                            logger.error(RecapConstants.ERROR,e);
                            throw new RuntimeException(e);
                        }
                    });


            for (Future future : futureList) {
                bibliographicEntities.add((BibliographicEntity) future.get());
            }

            long endTime = System.currentTimeMillis();

        logger.info("Time taken to prepare {} bib entities is : {} seconds, solr result size {}" , bibliographicEntities.size() , (endTime - startTime) / 1000,dataDumpSearchResults.size());

            getExecutorService().shutdown();
        logger.info("sending page count {} to marcrecord formatter route",currentPageCountStr);
            String currentPageCountStrbeforesendingToNxt = new DataExportHeaderUtil().getValueFor(batchHeaders, "currentPageCount");
            logger.info("currentPageCountStrbeforesendingToNxt--->{}",currentPageCountStrbeforesendingToNxt);
            FluentProducerTemplate fluentProducerTemplate = new DefaultFluentProducerTemplate(exchange.getContext());
            fluentProducerTemplate
                    .to(RecapConstants.BIB_ENTITY_FOR_DATA_EXPORT_Q)
                    .withBody(bibliographicEntities)
                    .withHeader(batchHeaderName, exchange.getIn().getHeader(batchHeaderName))
                    .withHeader("exportFormat", exchange.getIn().getHeader("exportFormat"))
                    .withHeader("transmissionType", exchange.getIn().getHeader("transmissionType"));
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
}
