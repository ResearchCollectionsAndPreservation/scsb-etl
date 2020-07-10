package org.recap.camel.datadump.consumer;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.service.formatter.datadump.DeletedJsonFormatterService;

public class DeletedJsonFormatActiveMQConsumerUT extends BaseTestCase {

    DeletedJsonFormatActiveMQConsumer deletedJsonFormatActiveMQConsumer;
    @Before
    public void before() {
        DeletedJsonFormatterService deletedJsonFormatterService = new DeletedJsonFormatterService();
        deletedJsonFormatActiveMQConsumer = new DeletedJsonFormatActiveMQConsumer(deletedJsonFormatterService);
    }
    @Test
    public void testprocessDeleteJsonString(){
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        in.setBody("CUL");
        ex.setIn(in);
        System.err.println(ex.getIn().getBody());
        try {

           // DeletedJsonFormatActiveMQConsumer deletedJsonFormatActiveMQConsumern = new DeletedJsonFormatActiveMQConsumer(deletedJsonFormatterService);
            deletedJsonFormatActiveMQConsumer.processDeleteJsonString(ex);
        }catch(Exception e){
        }
    }
}
