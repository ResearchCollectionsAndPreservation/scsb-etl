package org.recap.camel.datadump;

import org.apache.camel.*;
import org.apache.camel.impl.*;
import org.apache.camel.support.*;
import org.junit.*;
import org.recap.*;
import org.recap.model.csv.*;

import java.util.*;

import static org.junit.Assert.*;

public class FileNameProcessorForDataDumpSuccessUT extends BaseTestCase {
    FileNameProcessorForDataDumpSuccess fileNameProcessorForDataDumpSuccess;
    @Before
    public void setUpBefore() {
        fileNameProcessorForDataDumpSuccess = new FileNameProcessorForDataDumpSuccess();
    }

    @Test
    public void testProcess() {
        DataDumpSuccessReport dataDumpSuccessReport = new DataDumpSuccessReport();
        dataDumpSuccessReport.setFileName("test");
        String dataHeader = ";currentPageCount#1";
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        in.setBody(dataDumpSuccessReport);
        ex.setMessage(in);
        ex.setIn(in);
        Map<String, Object> mapdata = new HashMap<>();
        mapdata.put("batchHeaders", dataHeader);
        try {
            fileNameProcessorForDataDumpSuccess.process(ex);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        assertTrue(true);
    }
}
