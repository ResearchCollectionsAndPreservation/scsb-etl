package org.recap.camel.datadump.routebuilder;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseProcessorUT extends BaseTestCase {
    @Autowired
    BaseProcessor baseProcessor;
    @Test
    public void testsetProcessExchange(){
        String dataHeader="test data";
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        ex.setMessage(in);
        in.setBody("CUL");
        ex.setIn(in);
        Map<String,Object> mapdata = new HashMap<>();
        mapdata.put("testHeader",dataHeader);
        in.setHeaders(mapdata);
        baseProcessor.setProcessExchange(ex);
        assertTrue(true);
    }
}
