package org.recap.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.recap.RecapConstants;
import org.recap.report.CommonReportGenerator;
import org.recap.repositoryrw.ReportDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by peris on 8/19/16.
 */
@Component
public class XMLFileLoadReportProcessor implements Processor {

    /**
     * The Report detail repository.
     */
    @Autowired
    ReportDetailRepository reportDetailRepository;

    /**
     * This method is invoked by route to prepare report entity from exchange and persist.
     * @param exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        CommonReportGenerator commonReportGenerator = new CommonReportGenerator();
        commonReportGenerator.process(exchange, RecapConstants.FILE_LOADED, reportDetailRepository);
    }
}
