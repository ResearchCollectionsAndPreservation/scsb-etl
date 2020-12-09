package org.recap.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.processor.aggregate.UseOriginalAggregationStrategy;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.repository.XmlRecordRepository;
import org.recap.repositoryrw.ReportDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;


import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by pvsubrah on 6/21/16.
 */


public class XMLProcessorUT extends BaseTestCaseUT {

    private static final Logger logger = LoggerFactory.getLogger(XMLProcessorUT.class);


    @InjectMocks
    XmlProcessor xmlProcessor;

    @Mock
    CamelContext camelContext;

    @Mock
    RecordProcessor recordProcessor;

    @Mock
    XmlRecordRepository xmlRecordRepository;

    @Mock
    ReportDetailRepository reportDetailRepository;

    @Value("${etl.load.directory}")
    private String etlLoadDir;


    @Test
    public void testProcess() throws Exception {
        xmlProcessor = new XmlProcessor(xmlRecordRepository);
        String dataHeader="test";
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        UseOriginalAggregationStrategy useOriginalAggregationStrategy = new UseOriginalAggregationStrategy();
        Map<String,Object> mapData= new HashMap<>();
        mapData.put("Key",useOriginalAggregationStrategy);
        ex.setProperty("CamelAggregationStrategy",mapData);
        Message in = ex.getIn();
        ex.setMessage(in);
        in.setBody("<owningInstitutionId>CUL</owningInstitutionId>");
        ex.setIn(in);
        Map<String,Object> mapdata = new HashMap<>();
        mapdata.put("CamelFileName",dataHeader);
        in.setHeaders(mapdata);
        xmlProcessor.process(ex);
        assertTrue(true);
    }

    @Test
    public void testProcessException() throws Exception {
        xmlProcessor = new XmlProcessor(xmlRecordRepository);
        String dataHeader="test";
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        UseOriginalAggregationStrategy useOriginalAggregationStrategy = new UseOriginalAggregationStrategy();
        Map<String,Object> mapData= new HashMap<>();
        mapData.put("Key",useOriginalAggregationStrategy);
        ex.setProperty("CamelAggregationStrategy",mapData);
        Message in = ex.getIn();
        ex.setMessage(in);
        in.setBody("<owningInstitutionId>CUL</owningInstitutionId>");
        ex.setIn(in);
        Map<String,Object> mapdata = new HashMap<>();
        mapdata.put("CamelFileName",dataHeader);
        in.setHeaders(mapdata);
        Mockito.when(xmlRecordRepository.save(Mockito.any())).thenThrow(NullPointerException.class);
        xmlProcessor.process(ex);
        assertTrue(true);
    }
}
