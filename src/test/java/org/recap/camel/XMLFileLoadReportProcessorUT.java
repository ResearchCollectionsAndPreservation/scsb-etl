package org.recap.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;
import org.recap.repositoryrw.ReportDetailRepository;

import java.util.HashMap;
import java.util.Map;

public class XMLFileLoadReportProcessorUT extends BaseTestCaseUT {

    @InjectMocks
    XMLFileLoadReportProcessor xmlFileLoadReportProcessor;

    @Mock
    ReportDetailRepository reportDetailRepository;

    @Test
    public void process() throws Exception {
        String dataHeader = ";currentPageCount#1";
        Map<String, Object> mapdata = new HashMap<>();
        mapdata.put("batchHeaders", dataHeader);
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setBody(mapdata);
        xmlFileLoadReportProcessor.process(ex);
    }
}
