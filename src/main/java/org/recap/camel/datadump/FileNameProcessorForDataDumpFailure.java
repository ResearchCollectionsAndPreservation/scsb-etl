package org.recap.camel.datadump;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.FilenameUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.csv.DataDumpFailureReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by premkb on 01/10/16.
 */
public class FileNameProcessorForDataDumpFailure implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(FileNameProcessorForDataDumpFailure.class);

    /**
     * This method is invoked by route to set the data dump file name, report type and institution name in headers for failure data dump.
     *
     * @param exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        DataDumpFailureReport dataDumpFailureReport = (DataDumpFailureReport) exchange.getIn().getBody();
        String fileName = FilenameUtils.removeExtension(dataDumpFailureReport.getFileName());
        exchange.getIn().setHeader(RecapCommonConstants.REPORT_FILE_NAME, fileName);
        exchange.getIn().setHeader(RecapConstants.REPORT_TYPE, dataDumpFailureReport.getReportType());
        exchange.getIn().setHeader(RecapConstants.DIRECTORY_NAME, dataDumpFailureReport.getInstitutionName());
    }
}
