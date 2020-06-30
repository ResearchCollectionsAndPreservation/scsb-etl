package org.recap.camel.datadump;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.spy;

public class ZipFileProcessorUT {
    public Exchange exchange;
    ProducerTemplate producerTemplate;

    @Mock
    ZipFileProcessor zipFileProcessor;

    @Before
    public void before() {
        zipFileProcessor = spy(ZipFileProcessor.class);
    }
    @Test
    public void testProcessor(){
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        in.setBody("CUL");
        ex.setIn(in);
        try {
            ZipFileProcessor zipFileProcessorn = new ZipFileProcessor(producerTemplate,ex);
            zipFileProcessor.process(ex);
        }catch(Exception e){
        }
    }
}
