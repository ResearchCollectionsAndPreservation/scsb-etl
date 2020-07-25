package org.recap.camel.datadump;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.recap.util.datadump.BatchCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by peris on 11/5/16.
 */
public class DataExportPredicate implements Predicate {
    private static final Logger logger = LoggerFactory.getLogger(DataExportPredicate.class);
    private Integer batchSize;

    /**
     * Instantiates a new Data export predicate.
     *
     * @param batchSize the batch size
     */
    public DataExportPredicate(Integer batchSize) {
        this.batchSize = batchSize;
    }

    /**
     * Evaluates the predicate on the message exchange and returns true if this exchange matches the predicate
     * This predicate evaluates the current page count with total pages count or the current batch size with total batch size to identify if the exporting of data export process is complete.
     *
     * @param exchange
     * @return
     */
    @Override
    public boolean matches(Exchange exchange) {
        Integer batchSizeFromHeader = (Integer) exchange.getIn().getHeader("batchSize");
        if (batchSizeFromHeader == null) {
            batchSizeFromHeader = 0;
        }
        Integer totalPageCount = BatchCounter.getTotalPages();
        Integer currentPageCount = BatchCounter.getCurrentPage();

        logger.info("Total page count: {} and Current page count: {}, configured batch size-> {}, current batch size-> {}" , totalPageCount ,  currentPageCount,this.batchSize,batchSizeFromHeader);

        if (this.batchSize.equals(batchSizeFromHeader) || batchSizeFromHeader > this.batchSize || (currentPageCount.equals(totalPageCount))) {
            exchange.getIn().setHeader("batchSize", 0);
            return true;
        }
        return false;
    }
}
