package org.recap.camel.datadump;

import org.apache.camel.Exchange;
import org.recap.camel.datadump.routebuilder.BaseProcessor;
import org.recap.util.datadump.DataExportHeaderUtil;

/**
 * Created by peris on 11/8/16.
 */
public class FileFormatProcessorForDataExport extends BaseProcessor {

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
        setProcessExchange(exchange);
    }

    private String getValueFor(String batchHeaderString, String key) {
        return new DataExportHeaderUtil().getValueFor(batchHeaderString, key);
    }
}
