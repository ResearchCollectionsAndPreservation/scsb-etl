package org.recap.camel.datadump.routebuilder;

import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by peris on 11/12/16.
 */
public class DataExportSuccessReportRouteBuilderUT extends BaseTestCaseUT {

    @Mock
    ProducerTemplate producerTemplate;

    @Test
    public void testRoute() throws Exception {
        Map values = new HashMap<>();
        values.put(ScsbConstants.REQUESTING_INST_CODE, "PUL");
        values.put(ScsbConstants.NUM_RECORDS, String.valueOf("12"));
        values.put(ScsbConstants.NUM_BIBS_EXPORTED, ScsbConstants.NUM_BIBS_EXPORTED);
        values.put(ScsbConstants.BATCH_EXPORT, ScsbConstants.BATCH_EXPORT);
        values.put(ScsbCommonConstants.REQUEST_ID, "112-1");
        producerTemplate.sendBody("scsbactivemq:queue:dataExportSuccessQ", values);
        assertTrue(true);
    }

}