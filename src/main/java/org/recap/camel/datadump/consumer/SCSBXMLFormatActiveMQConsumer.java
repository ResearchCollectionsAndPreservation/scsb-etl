package org.recap.camel.datadump.consumer;

import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.recap.RecapConstants;
import org.recap.model.jaxb.marc.BibRecord;
import org.recap.report.CommonReportGenerator;
import org.recap.service.formatter.datadump.SCSBXmlFormatterService;
import org.recap.util.XmlFormatter;
import org.recap.util.datadump.DataExportHeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

/**
 * Created by peris on 11/1/16.
 */
public class SCSBXMLFormatActiveMQConsumer extends CommonReportGenerator {

    private static final Logger logger = LoggerFactory.getLogger(SCSBXMLFormatActiveMQConsumer.class);

    /**
     * The Scsb xml formatter service.
     */
    SCSBXmlFormatterService scsbXmlFormatterService;
    /**
     * The Xml formatter.
     */
    XmlFormatter xmlFormatter;
    private DataExportHeaderUtil dataExportHeaderUtil;

    /**
     * Instantiates a new Scsbxml format active mq consumer.
     *
     * @param scsbXmlFormatterService the scsb xml formatter service
     * @param xmlFormatter            the xml formatter
     */
    public SCSBXMLFormatActiveMQConsumer(SCSBXmlFormatterService scsbXmlFormatterService, XmlFormatter xmlFormatter) {
        this.scsbXmlFormatterService = scsbXmlFormatterService;
        this.xmlFormatter = xmlFormatter;
    }

    /**
     * This method is invoked by the route to build the scsb xml format string with the bib records list for data export.
     *
     * @param exchange the exchange
     * @return the string
     * @throws Exception the exception
     */
    public String processSCSBXmlString(Exchange exchange) throws Exception {
        List<BibRecord> records = (List<BibRecord>) exchange.getIn().getBody();
        logger.info("Num records to generate scsb XMl for: {} " , records.size());
        long startTime = System.currentTimeMillis();

        String toSCSBXmlString = null;
        String batchHeaders = (String) exchange.getIn().getHeader(RecapConstants.BATCH_HEADERS);
        String requestId = getDataExportHeaderUtil().getValueFor(batchHeaders, "requestId");
        try {
            toSCSBXmlString = scsbXmlFormatterService.getSCSBXmlForBibRecords(records);
            processSuccessReportEntity(exchange, records.size(), batchHeaders, requestId);
        } catch (Exception e) {
            logger.error(RecapConstants.ERROR,e);
            processFailureReportEntity(exchange, records.size(), batchHeaders, requestId);
        }
        long endTime = System.currentTimeMillis();

        logger.info("Time taken to generate scsb xml for : {} is {} : seconds " , records.size() ,  (endTime - startTime) / 1000 );

        return toSCSBXmlString;
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
    private void processFailureReportEntity(Exchange exchange, Integer size, String batchHeaders, String requestId) {

        HashMap<String, String> values = processReport(batchHeaders, requestId, getDataExportHeaderUtil());
        values.put(RecapConstants.NUM_RECORDS, String.valueOf(size));

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

