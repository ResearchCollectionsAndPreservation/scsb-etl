package org.recap.camel.datadump.routebuilder;

import org.apache.camel.*;
import org.apache.camel.impl.*;
import org.apache.camel.support.*;
import org.junit.*;
import org.recap.*;
import org.springframework.beans.factory.annotation.*;

import java.sql.*;
import java.util.*;

import static org.junit.Assert.*;

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
