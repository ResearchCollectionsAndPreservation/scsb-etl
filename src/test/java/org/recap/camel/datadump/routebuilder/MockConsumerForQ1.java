package org.recap.camel.datadump.routebuilder;

import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.impl.engine.DefaultFluentProducerTemplate;

/**
 * Created by peris on 11/20/16.
 */
public class MockConsumerForQ1 {

    public void processMessage(String message, Exchange exchange) {
        System.out.println(Thread.currentThread().getId() + message);
        String batchHeaders = (String) exchange.getIn().getHeader("batchHeaders");
        System.out.println(batchHeaders);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FluentProducerTemplate fluentProducerTemplate = DefaultFluentProducerTemplate.on(exchange.getContext());
        fluentProducerTemplate
                .to("seda:multipleConsumerQ2")
                .withBody("passing along "+message)
                .withHeader("batchHeaders", "updated"+batchHeaders);
        fluentProducerTemplate.send();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
