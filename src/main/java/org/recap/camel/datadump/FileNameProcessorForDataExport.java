package org.recap.camel.datadump;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.recap.RecapConstants;
import org.recap.util.datadump.DataExportHeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by peris on 11/4/16.
 */
@Component
public class FileNameProcessorForDataExport implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(FileNameProcessorForDataExport.class);

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
        logger.info("fileName for data export--->{}",fileName);
        String exportFormat = getValueFor(batchHeaders,"exportFormat");
        if (exportFormat.equals(RecapConstants.DATADUMP_DELETED_JSON_FORMAT)) {
            exchange.getOut().setHeader(Exchange.FILE_NAME, fileName+".json");
        } else {
            exchange.getOut().setHeader(Exchange.FILE_NAME, fileName+".xml");
        }
        Object body = exchange.getIn().getBody();
        exchange.getOut().setBody(body);

        Map<String, Object> headersForNewExchange = exchange.getIn().getHeaders();
        for (Iterator<String> iterator = headersForNewExchange.keySet().iterator(); iterator.hasNext(); ) {
            String header = iterator.next();
            exchange.getOut().setHeader(header, headersForNewExchange.get(header));
        }
    }

    private String getValueFor(String batchHeaderString, String key) {
        return new DataExportHeaderUtil().getValueFor(batchHeaderString, key);
    }
}
