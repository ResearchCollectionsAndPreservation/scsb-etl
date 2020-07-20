package org.recap.camel.datadump;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;
import org.mockito.Mock;
import org.recap.BaseTestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class DataExportAggregatorUT extends BaseTestCase {

    DataExportAggregator dataExportAggregator;
    @Mock
    Exchange exchange;

    @Test
    public void testaggregate() {
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Exchange exOld = null;
        List list = new ArrayList();
        list.add("Test");
        list.add("test1");
        Message in = ex.getIn();
        in.setBody(list);
        Map<String, Object> map = new HashMap<>();
        Integer data = 1;
        String databatchHeaders = "test batchHeaders";
        map.put("itemExportedCount", data);
        map.put("batchHeaders", databatchHeaders);

        in.setHeaders(map);
        ex.setIn(in);
        dataExportAggregator = new DataExportAggregator();
        Exchange exAssert = dataExportAggregator.aggregate(ex, ex);
        Exchange exAssertNew = dataExportAggregator.aggregate(exOld, ex);
        assertNotNull(exAssert);
        assertNotNull(exAssertNew);
    }
}
