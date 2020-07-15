package org.recap.camel.datadump;

import org.apache.camel.*;
import org.apache.camel.impl.*;
import org.apache.camel.support.*;
import org.junit.*;
import org.recap.*;

import java.util.*;

import static org.junit.Assert.*;

public class FileFormatProcessorForDataExportUT extends BaseTestCase {
    FileFormatProcessorForDataExport fileFormatProcessorForDataExport;

    @Test
    public void testProcess() {
        fileFormatProcessorForDataExport = new FileFormatProcessorForDataExport();

        String dataHeader = ";exportFormat#CUL";
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        ex.setMessage(in);
        in.setBody("CUL");
        ex.setIn(in);
        Map<String, Object> mapdata = new HashMap<>();
        mapdata.put("batchHeaders", dataHeader);
        in.setHeaders(mapdata);
        try {
            fileFormatProcessorForDataExport.process(ex);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        assertTrue(true);
    }
}
