package org.recap.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.processor.aggregate.UseOriginalAggregationStrategy;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.model.jpa.XmlRecordEntity;
import org.recap.repository.XmlRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by peris on 7/17/16.
 */
public class XmlProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(XmlProcessor.class);

    private final XmlRecordRepository xmlRecordRepository;

    /**
     * Instantiates a new Xml processor.
     *
     * @param xmlRecordRepository the xml record repository
     */
    @Autowired
    public XmlProcessor(XmlRecordRepository xmlRecordRepository) {
        this.xmlRecordRepository = xmlRecordRepository;
    }

    /**
     * This method is invoked by route to process the xml record entity from exchange and persist.
     *
     * @param exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        String xmlRecord = (String) exchange.getIn().getBody();
        if (StringUtils.isNotEmpty(xmlRecord)) {
            XmlRecordEntity xmlRecordEntity = new XmlRecordEntity();
            xmlRecordEntity.setXml(xmlRecord.getBytes());

            String camelFileName = (String) exchange.getIn().getHeader("CamelFileName");
            xmlRecordEntity.setXmlFileName(camelFileName);

            String owningInstitutionId = StringUtils.substringBetween(xmlRecord, "<owningInstitutionId>", "</owningInstitutionId>");
            setInstitutionOnHeader(exchange, owningInstitutionId);

            xmlRecordEntity.setOwningInst(owningInstitutionId);
            String owningInstitutionBibId = StringUtils.substringBetween(xmlRecord, "<owningInstitutionBibId>", "</owningInstitutionBibId>");

            if (StringUtils.isBlank(owningInstitutionBibId)) {
                owningInstitutionBibId = StringUtils.substringBetween(xmlRecord, "<controlfield tag=\"001\">", "</controlfield>");
            }

            if (StringUtils.isBlank(owningInstitutionBibId)) {
                owningInstitutionBibId = StringUtils.substringBetween(xmlRecord, "<controlfield tag='001'>", "</controlfield>");
            }
            xmlRecordEntity.setOwningInstBibId(owningInstitutionBibId);
            Date date = new Date();
            xmlRecordEntity.setDataLoaded(date);

            try {
                xmlRecordRepository.save(xmlRecordEntity);
            } catch (Exception e) {
                logger.error("Exception" , e);
            }

        }
    }

    private void setInstitutionOnHeader(Exchange exchange, String owningInstitutionId) {
        Map<?, ?> property = exchange.getProperty(Exchange.AGGREGATION_STRATEGY, Map.class);
        for (Iterator<?> iterator = property.keySet().iterator(); iterator.hasNext(); ) {
            Object key = iterator.next();
            Object value = property.get(key);
            if(value instanceof UseOriginalAggregationStrategy) {
                UseOriginalAggregationStrategy useOriginalAggregationStrategy = (UseOriginalAggregationStrategy) value;
                Exchange originalExchange = useOriginalAggregationStrategy.aggregate(exchange, null);
                originalExchange.getProperties().put(RecapConstants.INST_NAME,owningInstitutionId);
            }
        }
    }
}
