package org.recap.camel.datadump;

import org.apache.camel.*;
import org.apache.camel.impl.*;
import org.apache.camel.support.*;
import org.junit.*;
import org.recap.*;
import org.springframework.beans.factory.annotation.*;

import java.util.*;

public class FileNameProcessorForDataExportUT extends BaseTestCase {
    @Autowired
    FileNameProcessorForDataExport fileNameProcessorForDataExport;
    @Test
    public void testProcess(){
        String dataHeader = ";fileName#test;exportFormat#2";
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
            fileNameProcessorForDataExport.process(ex);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}