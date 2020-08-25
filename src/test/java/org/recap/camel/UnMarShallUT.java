package org.recap.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jaxb.marc.BibRecord;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.JAXBContext;
import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertNotNull;

/**
 * Created by angelind on 19/10/16.
 */
public class UnMarShallUT extends BaseTestCase {

    @Autowired
    CamelContext camelContext;

    @Autowired
    ProducerTemplate producerTemplate;

    @Test
    public void testUnmarshal() throws Exception {
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                JAXBContext context = JAXBContext.newInstance(BibRecord.class);
                JaxbDataFormat jaxbDataFormat = new JaxbDataFormat();
                jaxbDataFormat.setContext(context);
                from("seda:unmarshalQ")
                        .split()
                        .tokenizeXML("bibRecord")
                        .unmarshal(jaxbDataFormat)
                        .process(new UnMarshalProcessor())
                        .to("mock:meteo");
            }
        });
        URL resource = getClass().getResource("testUnMarshall.xml");
        producerTemplate.sendBody("seda:unmarshalQ", new File(resource.toURI()));
    }

    public class UnMarshalProcessor implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            BibRecord bibRecord = exchange.getIn().getBody(BibRecord.class);
            assertNotNull(bibRecord);
            assertNotNull(bibRecord.getHoldings());
            assertNotNull(bibRecord.getHoldings().get(0));
            assertNotNull(bibRecord.getHoldings().get(0).getHolding());
            assertNotNull(bibRecord.getHoldings().get(0).getHolding().get(0));
            assertNotNull(bibRecord.getHoldings().get(0).getHolding().get(0).getItems());
        }
    }
}
