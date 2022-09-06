package org.recap.camel.datadump;

import org.apache.camel.Exchange;
import org.recap.camel.datadump.routebuilder.BaseProcessor;
import org.recap.util.datadump.DataExportHeaderUtil;

/**
 * Created by peris on 11/8/16.
 */
public class TransmissionTypeProcessorForDataExport extends BaseProcessor {

    /**
     * This method is invoked by route to set the transmission type to the exchanges for data dump process.
     *
     * @param exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        String batchHeaders = (String) exchange.getIn().getHeader("batchHeaders");
        String exportFormat = getValueFor(batchHeaders, "transmissionType");
        exchange.getMessage().setHeader("transmissionType", exportFormat);
        setProcessExchange(exchange);
    }

    private static String getValueFor(String batchHeaderString, String key) {
        return new DataExportHeaderUtil().getValueFor(batchHeaderString, key);
    }
}
