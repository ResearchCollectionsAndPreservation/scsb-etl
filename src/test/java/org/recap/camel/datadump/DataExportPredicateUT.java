package org.recap.camel.datadump;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class DataExportPredicateUT extends BaseTestCase {

    DataExportPredicate dataExportPredicate;

    @Before
    public void setUpBefore() {
        dataExportPredicate = new DataExportPredicate(1);
    }

    @Test
    public void testMatches() {
        Integer dataHeader = 1;
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        ex.setMessage(in);
        ex.setIn(in);
        Map<String, Object> mapdata = new HashMap<>();
        mapdata.put("batchSize", dataHeader);
        in.setHeaders(mapdata);
        boolean status = dataExportPredicate.matches(ex);
        assertTrue(status);
    }
}
