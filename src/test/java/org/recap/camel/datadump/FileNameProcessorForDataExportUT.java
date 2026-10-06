package org.recap.camel.datadump;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;

import java.util.HashMap;
import java.util.Map;

public class FileNameProcessorForDataExportUT extends BaseTestCaseUT {
    @InjectMocks
    FileNameProcessorForDataExport fileNameProcessorForDataExport;

    @Test
    public void testProcess() {
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