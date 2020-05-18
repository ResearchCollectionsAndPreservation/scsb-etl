package org.recap.camel.datadump;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.recap.util.datadump.DataExportHeaderUtil;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by peris on 11/8/16.
 */
public class FileFormatProcessorForDataExport implements Processor {

    /**
     * This method is invoked by route to set the header values to the exchange.
     * @param exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        String batchHeaders = (String) exchange.getIn().getHeader("batchHeaders");
        String exportFormat = getValueFor(batchHeaders, "exportFormat");
        exchange.getMessage().setHeader("exportFormat", exportFormat);
        Object body = exchange.getIn().getBody();
        exchange.getMessage().setBody(body);

        Map<String, Object> headersForNewExchange = exchange.getIn().getHeaders();
        for (Iterator<String> iterator = headersForNewExchange.keySet().iterator(); iterator.hasNext(); ) {
            String header = iterator.next();
            exchange.getMessage().setHeader(header, headersForNewExchange.get(header));
        }
    }

    private String getValueFor(String batchHeaderString, String key) {
        return new DataExportHeaderUtil().getValueFor(batchHeaderString, key);
    }
}
