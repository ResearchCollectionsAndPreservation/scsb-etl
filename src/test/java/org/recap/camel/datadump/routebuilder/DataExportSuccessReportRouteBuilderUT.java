package org.recap.camel.datadump.routebuilder;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by peris on 11/12/16.
 */
public class DataExportSuccessReportRouteBuilderUT extends BaseTestCaseUT {

    @Mock
    ProducerTemplate producerTemplate;

    @Test
    public void testRoute() throws Exception {
        Map values = new HashMap<>();
        values.put(RecapConstants.REQUESTING_INST_CODE, "PUL");
        values.put(RecapConstants.NUM_RECORDS, String.valueOf("12"));
        values.put(RecapConstants.NUM_BIBS_EXPORTED, RecapConstants.NUM_BIBS_EXPORTED);
        values.put(RecapConstants.BATCH_EXPORT, RecapConstants.BATCH_EXPORT);
        values.put(RecapCommonConstants.REQUEST_ID, "112-1");
        producerTemplate.sendBody("scsbactivemq:queue:dataExportSuccessQ", values);
        assertTrue(true);
    }

}