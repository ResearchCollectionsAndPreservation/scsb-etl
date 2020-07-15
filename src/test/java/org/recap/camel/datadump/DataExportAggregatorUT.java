package org.recap.camel.datadump;

import org.apache.camel.*;
import org.apache.camel.impl.*;
import org.apache.camel.support.*;
import org.junit.*;
import org.mockito.*;
import org.recap.*;

import java.util.*;

import static org.junit.Assert.*;

public class DataExportAggregatorUT extends BaseTestCase {

    DataExportAggregator dataExportAggregator;
    @Mock
    Exchange exchange;
    @Test
    public void testaggregate(){
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Exchange exNew = new DefaultExchange(ctx);
        List list= new ArrayList();
        list.add("Test");
        list.add("test1");
        Message in = ex.getIn();
        in.setBody(list);
        Map<String,Object>  map = new HashMap<>();
        Integer data = 1;
        String databatchHeaders= "test batchHeaders";
        map.put("itemExportedCount",data);
        map.put("batchHeaders",databatchHeaders);

        in.setHeaders(map);
        ex.setIn(in);
        dataExportAggregator = new DataExportAggregator();
        exNew =dataExportAggregator.aggregate(ex,ex);
        assertNotNull(exNew);
    }
}
