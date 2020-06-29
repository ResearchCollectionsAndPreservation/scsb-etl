package org.recap.camel.datadump.routebuilder;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Iterator;
import java.util.Map;

public abstract class BaseProcessor implements Processor {

    protected void setProcessExchange(Exchange exchange)
    {
        Object body = exchange.getIn().getBody();
        exchange.getMessage().
                setBody(body);

        Map<String, Object> headersForNewExchange = exchange.getIn().getHeaders();
        for(
                Iterator<String> iterator = headersForNewExchange.keySet().iterator(); iterator.hasNext(); )
        {
            String header = iterator.next();
            exchange.getMessage().setHeader(header, headersForNewExchange.get(header));
        }
    }
}
