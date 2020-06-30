package org.recap.camel.datadump.routebuilder;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.engine.DefaultFluentProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by peris on 11/20/16.
 */
public class MultipleConsumers_UT extends BaseTestCase {

    @Autowired
    CamelContext camelContext;

    @Autowired
    ConsumerTemplate consumerTemplate;
    String multipleConsumerQ1 = "seda:multipleConsumerQ1";
    String multipleConsumerQ2 = "seda:multipleConsumerQ2";


    //@Test
    public void multipleConsumers() throws Exception {
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(multipleConsumerQ1)
                        .threads(10)
                        .bean(new MockConsumerForQ1(), "processMessage");
            }
        });

        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(multipleConsumerQ2)
                        .threads(10)
                        .bean(new MockConsumerForQ2(), "processMessage");
            }
        });

        FluentProducerTemplate fluentProducerTemplate = new DefaultFluentProducerTemplate(camelContext);
        for (int loop = 0; loop < 30; loop++) {
            fluentProducerTemplate
                    .to(multipleConsumerQ1)
                    .withBody("Hello World")
                    .withHeader("batchHeaders", "headers");
            fluentProducerTemplate.send();
        }

        Thread.sleep(20000);
    }

}
