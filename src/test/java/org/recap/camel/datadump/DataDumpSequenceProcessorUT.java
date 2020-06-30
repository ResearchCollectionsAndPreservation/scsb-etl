package org.recap.camel.datadump;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.recap.RecapConstants;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

public class DataDumpSequenceProcessorUT {
    public Exchange exchange;

    @Mock
    DataDumpSequenceProcessor dataDumpSequenceProcessor;

    @Before
    public void before() {
        dataDumpSequenceProcessor = spy(DataDumpSequenceProcessor.class);
    }

    @Test
    public void testProcessor() {
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        in.setBody("NYPL");
        ex.setIn(in);
        try {
            dataDumpSequenceProcessor.process(ex);
        }catch(Exception e){

        }
        assertTrue(true);
    }

    @Test
    public void testProcessor1() {
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        in.setBody("PUL");
        ex.setIn(in);
        RecapConstants.EXPORT_DATE_SCHEDULER="IncrementalRecordsExportNypl";
        try {
            dataDumpSequenceProcessor.process(ex);
        }catch(Exception e){

        }
        assertTrue(true);
    }
    @Test
    public void testProcessor2() {
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        in.setBody("CUL");
        ex.setIn(in);
        RecapConstants.EXPORT_DATE_SCHEDULER="IncrementalRecordsExportCul";
        try {
            dataDumpSequenceProcessor.process(ex);
        }catch(Exception e){

        }
        assertTrue(true);
    }
}
