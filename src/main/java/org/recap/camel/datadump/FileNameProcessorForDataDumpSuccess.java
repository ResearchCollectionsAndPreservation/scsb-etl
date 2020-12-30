package org.recap.camel.datadump;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.FilenameUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.csv.DataDumpSuccessReport;

import java.util.List;

/**
 * Created by premkb on 01/10/16.
 */
public class FileNameProcessorForDataDumpSuccess implements Processor {

    /**
     * This method is invoked by route to set the data dump file name, report type and institution name in headers for success data dump.
     *
     * @param exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        DataDumpSuccessReport dataDumpSuccessReport = (DataDumpSuccessReport) exchange.getIn().getBody();
        List<DataDumpSuccessReport> dataDumpSuccessReportList = dataDumpSuccessReport.getDataDumpSuccessReportList();
        String fileName = FilenameUtils.removeExtension(dataDumpSuccessReport.getFileName());
        exchange.getIn().setHeader(RecapCommonConstants.REPORT_FILE_NAME, fileName);
        exchange.getIn().setHeader(RecapConstants.REPORT_TYPE, dataDumpSuccessReport.getReportType());
        exchange.getIn().setHeader(RecapConstants.DIRECTORY_NAME, dataDumpSuccessReport.getInstitutionName());
        exchange.getIn().setBody(dataDumpSuccessReportList);
    }
}
