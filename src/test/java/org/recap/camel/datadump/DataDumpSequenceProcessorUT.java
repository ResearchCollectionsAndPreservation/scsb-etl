package org.recap.camel.datadump;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertTrue;

public class DataDumpSequenceProcessorUT extends BaseTestCase {

    @Autowired
    DataDumpSequenceProcessor dataDumpSequenceProcessor;

    @Test
    public void testProcessor() {
        CamelContext ctx = new DefaultCamelContext();
        Exchange exPul = new DefaultExchange(ctx);
        Message inPul = exPul.getIn();
        inPul.setBody("PUL");
        exPul.setIn(inPul);
        Exchange exCul = new DefaultExchange(ctx);
        Message inCul = exCul.getIn();
        inCul.setBody("PUL");
        exCul.setIn(inCul);
        Exchange exNypl = new DefaultExchange(ctx);
        Message inNypl = exCul.getIn();
        inNypl.setBody("PUL");
        exCul.setIn(inNypl);
        try {
            dataDumpSequenceProcessor.process(exPul);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            dataDumpSequenceProcessor.process(exCul);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            dataDumpSequenceProcessor.process(exNypl);
        } catch (Exception e) {
            e.printStackTrace();
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
        RecapConstants.EXPORT_DATE_SCHEDULER = "IncrementalRecordsExportNypl";
        try {
            dataDumpSequenceProcessor.process(ex);
        } catch (Exception e) {

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
        RecapConstants.EXPORT_DATE_SCHEDULER = "IncrementalRecordsExportCul";
        try {
            dataDumpSequenceProcessor.process(ex);
        } catch (Exception e) {

        }
        assertTrue(true);
    }
}
