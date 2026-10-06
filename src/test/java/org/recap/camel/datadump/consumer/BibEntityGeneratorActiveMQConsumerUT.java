package org.recap.camel.datadump.consumer;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.repository.BibliographicDetailsRepository;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

public class BibEntityGeneratorActiveMQConsumerUT extends BaseTestCaseUT {

    @InjectMocks
    @Spy
    BibEntityGeneratorActiveMQConsumer bibEntityGeneratorActiveMQConsumer;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    ExecutorService executorService;

    @Mock
    Future future;

    @Test
    public void exportDataDumpForMarcXML() throws Exception {
        try {
            Map results = new HashMap();
            List<HashMap> dataDumpSearchResults = new ArrayList<>();
            HashMap hashMap = new HashMap();
            hashMap.put("bibId", 1);
            hashMap.put("itemIds", Arrays.asList(1));
            dataDumpSearchResults.add(hashMap);
            results.put("dataDumpSearchResults", dataDumpSearchResults);
            Exchange ex = getExchange(results);

            List<Future<Object>> futures = new ArrayList<>();
            futures.add(future);

            List<BibliographicEntity> bibliographicEntityList = new ArrayList<>();
            bibliographicEntityList.add(getBibliographicEntity());
            ReflectionTestUtils.setField(bibEntityGeneratorActiveMQConsumer,"dataDumpBibEntityBatchSize",10);
            ReflectionTestUtils.setField(bibEntityGeneratorActiveMQConsumer,"dataDumpBibEntityThreadSize",10);
            Mockito.when(executorService.invokeAll(any())).thenReturn(futures);
            Mockito.when(bibliographicDetailsRepository.getBibliographicEntityList(Mockito.anyList())).thenReturn(bibliographicEntityList);
            bibEntityGeneratorActiveMQConsumer.processBibEntities(ex);
        } catch (Exception e) {
        }
    }

    @Test
    public void getExecutorServiceShutDown() {
        Mockito.when(executorService.isShutdown()).thenReturn(Boolean.TRUE);
        ReflectionTestUtils.setField(bibEntityGeneratorActiveMQConsumer,"dataDumpBibEntityThreadSize",10);
        bibEntityGeneratorActiveMQConsumer.getExecutorService();
    }

    @Test
    public void getBibliographicEntityFutureInterruptedException() throws ExecutionException, InterruptedException {
        Mockito.when(future.get()).thenThrow(new InterruptedException());
        try {
            ReflectionTestUtils.invokeMethod(bibEntityGeneratorActiveMQConsumer, "getBibliographicEntityFuture", future);
        } catch (RuntimeException e) {
        }
    }

    @Test
    public void getBibliographicEntityFutureExecutionException() throws ExecutionException, InterruptedException {
        Mockito.when(future.get()).thenThrow(new ExecutionException(new Throwable()));
        try {
            ReflectionTestUtils.invokeMethod(bibEntityGeneratorActiveMQConsumer, "getBibliographicEntityFuture", future);
        } catch (RuntimeException e) {
        }
    }

    @Test
    public void getExecutorServiceNull() {
        BibEntityGeneratorActiveMQConsumer bibEntityGeneratorActiveMQConsumer = new BibEntityGeneratorActiveMQConsumer(bibliographicDetailsRepository, 2, 10);
        bibEntityGeneratorActiveMQConsumer.getExecutorService();
    }
    @Test
    public void getBibEntityGeneratorActiveMQConsumer() {
        BibEntityGeneratorActiveMQConsumer bibEntityGeneratorActiveMQConsumer = new BibEntityGeneratorActiveMQConsumer(bibliographicDetailsRepository);
        assertNotNull(bibEntityGeneratorActiveMQConsumer);
    }

    private BibliographicEntity getBibliographicEntity() {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setId(1);
        return bibliographicEntity;
    }

    private Exchange getExchange(Map results) {
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setHeader("batchHeaders", "Head001");
        ex.getIn().setBody(results);
        ex.setProperty("CamelSplitComplete", true);
        return ex;
    }

}
