package org.recap.camel.datadump;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.recap.ScsbConstants;
import org.recap.camel.datadump.routebuilder.BaseProcessor;
import org.recap.util.datadump.DataExportHeaderUtil;
import org.springframework.stereotype.Component;

/**
 * Created by peris on 11/4/16.
 */
@Slf4j
@Component
public class FileNameProcessorForDataExport extends BaseProcessor {



    /**
     * This method is invoked by route to set the data dump file name, report type and institution name in headers for failure data dump.
     *
     * @param exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        String batchHeaders = (String) exchange.getIn().getHeader("batchHeaders");
        String fileName = getValueFor(batchHeaders, "fileName");
        log.info("fileName for data export--->{}", fileName);
        String exportFormat = getValueFor(batchHeaders, "exportFormat");
        if (exportFormat.equals(ScsbConstants.DATADUMP_DELETED_JSON_FORMAT)) {
            exchange.getMessage().setHeader(Exchange.FILE_NAME, fileName + ".json");
        } else {
            exchange.getMessage().setHeader(Exchange.FILE_NAME, fileName + ".xml");
        }
        setProcessExchange(exchange);
    }

    private static String getValueFor(String batchHeaderString, String key) {
        return new DataExportHeaderUtil().getValueFor(batchHeaderString, key);
    }

}

