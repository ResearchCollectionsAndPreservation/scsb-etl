package org.recap.camel.datadump.consumer;

import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.recap.RecapConstants;
import org.recap.model.export.DeletedRecord;
import org.recap.report.CommonReportGenerator;
import org.recap.service.formatter.datadump.DeletedJsonFormatterService;
import org.recap.util.datadump.DataExportHeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

/**
 * Created by peris on 11/1/16.
 */
public class DeletedJsonFormatActiveMQConsumer extends CommonReportGenerator  {
    private static final Logger logger = LoggerFactory.getLogger(DeletedJsonFormatActiveMQConsumer.class);

    /**
     * The Deleted json formatter service.
     */
    DeletedJsonFormatterService deletedJsonFormatterService;
    private DataExportHeaderUtil dataExportHeaderUtil;

    /**
     * Instantiates a new Deleted json format active mq consumer.
     *
     * @param deletedJsonFormatterService the deleted json formatter service
     */
    public DeletedJsonFormatActiveMQConsumer(DeletedJsonFormatterService deletedJsonFormatterService) {
        this.deletedJsonFormatterService = deletedJsonFormatterService;
    }

    /**
     * This method is invoked by the route to build the json format string with the deleted records list for data export.
     *
     * @param exchange the exchange
     * @return the string
     * @throws Exception the exception
     */
    public String processDeleteJsonString(Exchange exchange) throws Exception {
        List<DeletedRecord> deletedRecordList = (List<DeletedRecord>) exchange.getIn().getBody();
        logger.info("Num records to generate json for: {} " , deletedRecordList.size());
        long startTime = System.currentTimeMillis();

        String deletedJsonString = null;
        String batchHeaders = (String) exchange.getIn().getHeader(RecapConstants.BATCH_HEADERS);
        String requestId = getDataExportHeaderUtil().getValueFor(batchHeaders, "requestId");

        try {
            String formattedOutputForDeletedRecords = deletedJsonFormatterService.getJsonForDeletedRecords(deletedRecordList);
            deletedJsonString = String.format(formattedOutputForDeletedRecords);
            processSuccessReportEntity(exchange, deletedRecordList.size(), batchHeaders, requestId);
        } catch (Exception e) {
            logger.error(RecapConstants.ERROR,e);
            processFailureReportEntity(exchange, deletedRecordList.size(), batchHeaders, requestId, e);
        }
        long endTime = System.currentTimeMillis();

        logger.info("Time taken to generate json for : {} is : {} seconds " , deletedRecordList.size() , (endTime-startTime)/1000 );

        return deletedJsonString;
    }

    /**
     * This method builds a map with the values for success report entity and sends to the route to save the report entity.
     * @param exchange
     * @param size
     * @param batchHeaders
     * @param requestId
     */
    private void processSuccessReportEntity(Exchange exchange, Integer size, String batchHeaders, String requestId) {
        processSuccessReport(exchange, size, batchHeaders, requestId, getDataExportHeaderUtil());
    }

    /**
     * This method builds a map with the values for failure report entity and sends to the route to save the report entity.
     * @param exchange
     * @param size
     * @param batchHeaders
     * @param requestId
     * @param e
     */
    private void processFailureReportEntity(Exchange exchange, Integer size, String batchHeaders, String requestId, Exception e) {
        HashMap<String, String> values = processReport(batchHeaders, requestId, getDataExportHeaderUtil());
        values.put(RecapConstants.NUM_RECORDS, String.valueOf(size));
        values.put(RecapConstants.FAILURE_CAUSE, String.valueOf(e.getCause()));

        FluentProducerTemplate fluentProducerTemplate = generateFluentProducerTemplate(exchange, values, RecapConstants.DATADUMP_FAILURE_REPORT_Q);
        fluentProducerTemplate.send();
    }

    /**
     * Gets data export header util.
     *
     * @return the data export header util
     */
    public DataExportHeaderUtil getDataExportHeaderUtil() {
        if (null == dataExportHeaderUtil) {
            dataExportHeaderUtil = new DataExportHeaderUtil();
        }
        return dataExportHeaderUtil;
    }
}

