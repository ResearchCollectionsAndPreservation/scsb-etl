package org.recap.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.file.GenericFile;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.report.CommonReportGenerator;
import org.recap.repository.ReportDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

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
