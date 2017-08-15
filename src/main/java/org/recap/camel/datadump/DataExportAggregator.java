package org.recap.camel.datadump;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.recap.RecapConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by peris on 11/5/16.
 */
public class DataExportAggregator implements AggregationStrategy {

    private Logger logger = LoggerFactory.getLogger(DataExportAggregator.class);
    /**
     * This method aggregates an old and new exchange together to create a single combined exchange.
     * The strategy is to update the batch size in old exchange with the records size in the new exchange.
     * @param oldExchange
     * @param newExchange
     * @return
     */
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        boolean isFirstBatch = false;
        if (oldExchange == null) {
            oldExchange = new DefaultExchange(newExchange);
            oldExchange.getIn().setHeaders(newExchange.getIn().getHeaders());
            List<Object> body = new ArrayList<>();
            oldExchange.getIn().setBody(body);
            oldExchange.getExchangeId();
            isFirstBatch = true;
        }
        List body = (List) newExchange.getIn().getBody();
        List oldBody = oldExchange.getIn().getBody(List.class);
        if (null != oldBody && null != body) {
            if (!isFirstBatch) {
                Integer itemExportCount = (Integer) newExchange.getIn().getHeader(RecapConstants.ITEM_EXPORTED_COUNT);
                Integer previousItemExportCount = (Integer) oldExchange.getIn().getHeader(RecapConstants.ITEM_EXPORTED_COUNT);
                Integer updatedItemExportCount = previousItemExportCount + itemExportCount;
                newExchange.getIn().setHeader(RecapConstants.ITEM_EXPORTED_COUNT,updatedItemExportCount);
                logger.info("itemExportCount--->{} previousItemExportCount--->{} updatedItemExportCount--->{}",itemExportCount,previousItemExportCount,updatedItemExportCount);
            } else {
                Integer itemExportCount = (Integer) newExchange.getIn().getHeader(RecapConstants.ITEM_EXPORTED_COUNT);
                logger.info("first batch...itemExportCount-->{}",itemExportCount);
            }
            oldBody.addAll(body);
            Object oldBatchSize = oldExchange.getIn().getHeader("batchSize");
            Integer newBatchSize = 0;
            if (null != oldBatchSize) {
                newBatchSize = body.size() + (Integer) oldBatchSize;
            } else {
                newBatchSize = body.size();
            }
            oldExchange.getIn().setHeader("batchSize", newBatchSize);

            Map<String, Object> headersForNewExchange = newExchange.getIn().getHeaders();
            for (Iterator<String> iterator = headersForNewExchange.keySet().iterator(); iterator.hasNext(); ) {
                String header = iterator.next();
                oldExchange.getIn().setHeader(header, headersForNewExchange.get(header));
            }

            for (String key : newExchange.getProperties().keySet()) {
                oldExchange.setProperty(key, newExchange.getProperty(key));
            }
        }

        return oldExchange;
    }
}
