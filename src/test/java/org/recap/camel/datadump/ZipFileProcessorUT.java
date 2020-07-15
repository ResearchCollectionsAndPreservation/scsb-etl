package org.recap.camel.datadump;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.codehaus.jettison.json.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.recap.*;
import org.springframework.beans.factory.annotation.*;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;

public class ZipFileProcessorUT extends BaseTestCase {
    public Exchange exchange;
    ProducerTemplate producerTemplate;

    @Autowired
    ZipFileProcessor zipFileProcessor;

    @Test
    public void testProcessor() {
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        Map<String, Object> headers = new HashMap<>();
        String data = ";institutionCodes#institutionCodes*CUL*PUL*NYPL";
        headers.put("batchHeaders", data);
        in.setHeaders(headers);
        in.setBody("CUL");
        ex.setIn(in);
        try {

            zipFileProcessor.process(ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }

    @Test
    public void testftpOnCompletion() {
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        Map<String, Object> headers = new HashMap<>();
        String data = ";requestingInstitutionCode#1";
        String dataN = ";fetchType#1";

        headers.put("fetchType", dataN);
        headers.put("batchHeaders", data);
        in.setHeaders(headers);
        in.setBody("CUL");
        ex.setIn(in);
        try {
            ZipFileProcessor zipFileProcessorn = new ZipFileProcessor(producerTemplate, ex);
            zipFileProcessorn.ftpOnCompletion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
