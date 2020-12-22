package org.recap.camel.datadump.consumer;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.repository.BibliographicDetailsRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Ignore
public class BibEntityGeneratorActiveMQConsumerUT extends BaseTestCaseUT {

    @InjectMocks
    BibEntityGeneratorActiveMQConsumer bibEntityGeneratorActiveMQConsumer;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    ExecutorService executorService;

    @Mock
    CamelContext camelContext;

    @Mock
    Exchange exchange;

    @Mock
    Message value;

    @Test
    public void exportDataDumpForMarcXML() throws Exception {
        Mockito.when(exchange.getIn()).thenReturn(value);
        Map results=new HashMap();
        List<HashMap> dataDumpSearchResults=new ArrayList<>();
        HashMap hashMap=new HashMap();
        hashMap.put("bibId",1);
        hashMap.put("itemIds", Arrays.asList(1));
        dataDumpSearchResults.add(hashMap);
        results.put("dataDumpSearchResults",dataDumpSearchResults);
        Mockito.when(value.getBody()).thenReturn(results);
        Mockito.when(value.getHeader(Mockito.anyString())).thenReturn("batchHeaders");
        List<BibliographicEntity> bibliographicEntityList=new ArrayList<>();
        BibliographicEntity bibliographicEntity=new BibliographicEntity();
        bibliographicEntity.setBibliographicId(1);
        bibliographicEntityList.add(bibliographicEntity);
        Mockito.when(bibliographicDetailsRepository.getBibliographicEntityList(Mockito.anyList())).thenReturn(bibliographicEntityList);
        Mockito.when(exchange.getContext()).thenReturn(camelContext);
        bibEntityGeneratorActiveMQConsumer.processBibEntities(exchange);
    }
}
