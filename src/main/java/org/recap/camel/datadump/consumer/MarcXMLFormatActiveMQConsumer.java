package org.recap.camel.datadump.consumer;

import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.marc4j.marc.Record;
import org.recap.RecapConstants;
import org.recap.report.CommonReportGenerator;
import org.recap.service.formatter.datadump.MarcXmlFormatterService;
import org.recap.util.datadump.DataExportHeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

/**
 * Created by peris on 11/1/16.
 */
public class MarcXMLFormatActiveMQConsumer extends CommonReportGenerator {
    private static final Logger logger = LoggerFactory.getLogger(MarcXMLFormatActiveMQConsumer.class);

    private MarcXmlFormatterService marcXmlFormatterService;
    private DataExportHeaderUtil dataExportHeaderUtil;

    /**
     * Instantiates a new Marc xml format active mq consumer.
     *
     * @param marcXmlFormatterService the marc xml formatter service
     */
    public MarcXMLFormatActiveMQConsumer(MarcXmlFormatterService marcXmlFormatterService) {
        this.marcXmlFormatterService = marcXmlFormatterService;
    }

    /**
     * This method is invoked by the route to build the marc xml format string with the marc records list for data export.
     *
     * @param exchange the exchange
     * @return the string
     * @throws Exception the exception
     */
    public String processMarcXmlString(Exchange exchange) throws Exception {
        List<Record> records = (List<Record>) exchange.getIn().getBody();
        logger.info("Num records to generate marc XMl for: {} " , records.size());
        long startTime = System.currentTimeMillis();

        String toMarcXmlString = null;
        String batchHeaders = (String) exchange.getIn().getHeader(RecapConstants.BATCH_HEADERS);
        String requestId = getDataExportHeaderUtil().getValueFor(batchHeaders, "requestId");
        try {
            toMarcXmlString = marcXmlFormatterService.covertToMarcXmlString(records);
            processSuccessReportEntity(exchange, records, batchHeaders, requestId);
        } catch (Exception e) {
            logger.error(RecapConstants.ERROR,e);
            processFailureReportEntity(exchange, records, batchHeaders, requestId, e);
        }

        long endTime = System.currentTimeMillis();

        logger.info("Time taken to generate marc xml for : {} is : {} seconds " , records.size() , (endTime - startTime) / 1000 );

        return toMarcXmlString;
    }

    /**
     * This method builds a map with the values for success report entity and sends to the route to save the report entity.
     * @param exchange
     * @param records
     * @param batchHeaders
     * @param requestId
     */
    private void processSuccessReportEntity(Exchange exchange, List<Record> records, String batchHeaders, String requestId) {
        HashMap<String, String> values = new HashMap<>();
        values.put(RecapConstants.REQUESTING_INST_CODE, getDataExportHeaderUtil().getValueFor(batchHeaders, RecapConstants.REQUESTING_INST_CODE));
        values.put(RecapConstants.INSTITUTION_CODES, getDataExportHeaderUtil().getValueFor(batchHeaders, RecapConstants.INSTITUTION_CODES));
        values.put(RecapConstants.FETCH_TYPE, getDataExportHeaderUtil().getValueFor(batchHeaders, RecapConstants.FETCH_TYPE));
        values.put(RecapConstants.COLLECTION_GROUP_IDS, getDataExportHeaderUtil().getValueFor(batchHeaders, RecapConstants.COLLECTION_GROUP_IDS));
        values.put(RecapConstants.TRANSMISSION_TYPE, getDataExportHeaderUtil().getValueFor(batchHeaders, RecapConstants.TRANSMISSION_TYPE));
        values.put(RecapConstants.EXPORT_FROM_DATE, getDataExportHeaderUtil().getValueFor(batchHeaders, RecapConstants.EXPORT_FROM_DATE));
        values.put(RecapConstants.EXPORT_FORMAT, getDataExportHeaderUtil().getValueFor(batchHeaders, RecapConstants.EXPORT_FORMAT));
        values.put(RecapConstants.TO_EMAIL_ID, getDataExportHeaderUtil().getValueFor(batchHeaders, RecapConstants.TO_EMAIL_ID));
        values.put(RecapConstants.NUM_RECORDS, String.valueOf(records.size()));
        setValues(values, exchange, requestId);
        FluentProducerTemplate fluentProducerTemplate = generateFluentProducerTemplate(exchange, values, RecapConstants.DATADUMP_SUCCESS_REPORT_Q);
        fluentProducerTemplate.send();

    }

    /**
     * This method builds a map with the values for failure report entity and sends to the route to save the report entity.
     * @param exchange
     * @param records
     * @param batchHeaders
     * @param requestId
     * @param e
     */
    private void processFailureReportEntity(Exchange exchange, List<Record> records, String batchHeaders, String requestId, Exception e) {
        HashMap<String, String> values = processReport(batchHeaders, requestId, getDataExportHeaderUtil());
        values.put(RecapConstants.NUM_RECORDS, String.valueOf(records.size()));
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

    /**
     * Sets data export header util.
     *
     * @param dataExportHeaderUtil the data export header util
     */
    public void setDataExportHeaderUtil(DataExportHeaderUtil dataExportHeaderUtil) {
        this.dataExportHeaderUtil = dataExportHeaderUtil;
    }
}

