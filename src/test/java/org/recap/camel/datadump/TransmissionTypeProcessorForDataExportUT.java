package org.recap.camel.datadump;

import org.apache.camel.*;
import org.apache.camel.impl.*;
import org.apache.camel.support.*;
import org.junit.*;
import org.recap.*;

import java.util.*;

public class TransmissionTypeProcessorForDataExportUT extends BaseTestCase {

    TransmissionTypeProcessorForDataExport  transmissionTypeProcessorForDataExport;

    @Test
    public void testProcess(){

        String dataHeader=";transmissionType#exportFormat";
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        ex.setMessage(in);
        in.setBody("CUL");
        ex.setIn(in);
        Map<String,Object> mapdata = new HashMap<>();
        mapdata.put("batchHeaders",dataHeader);
        in.setHeaders(mapdata);
        transmissionTypeProcessorForDataExport = new TransmissionTypeProcessorForDataExport();
        try {
            transmissionTypeProcessorForDataExport.process(ex);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
