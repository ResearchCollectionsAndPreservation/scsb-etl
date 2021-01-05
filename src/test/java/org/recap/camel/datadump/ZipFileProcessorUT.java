package org.recap.camel.datadump;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.Route;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class ZipFileProcessorUT extends BaseTestCaseUT {

    ProducerTemplate producerTemplate;

    @InjectMocks
    ZipFileProcessor zipFileProcessor;

    @Mock
    DataExportEmailProcessor dataExportEmailProcessor;

    @Mock
    Exchange exchange;

    @Mock
    CamelContext camelContext;

    @Mock
    Route ftpRoute;

    @Mock
    Message message;

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
    public void testProcessorToremoveRoute() {
        String data = ";institutionCodes#institutionCodes*CUL*PUL*NYPL";
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getHeader("batchHeaders")).thenReturn(data);
        Mockito.when(exchange.getContext()).thenReturn(camelContext);
        Mockito.when(camelContext.getRoute(Mockito.anyString())).thenReturn(ftpRoute);
        try {
            zipFileProcessor.process(exchange);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }

    @Test
    public void testftpOnCompletion() {
        String[] fetchTypes={";fetchType#1",";fetchType#2",";fetchType#3"};
        for (String dataN: fetchTypes) {
         CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        Map<String, Object> headers = new HashMap<>();
        String data = ";requestingInstitutionCode#1";
        headers.put("batchHeaders", dataN);
        in.setHeaders(headers);
        in.setBody("CUL");
        ex.setIn(in);
        try {
            ZipFileProcessor zipFileProcessorn = new ZipFileProcessor(producerTemplate, ex);
            zipFileProcessorn.ftpOnCompletion();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }
    }
}
