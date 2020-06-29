package org.recap.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.FilenameUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.csv.ReCAPCSVSuccessRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by angelind on 18/8/16.
 */
public class FileNameProcessorForSuccessRecord implements Processor {

    /**
     * This method is invoked by route to set the data load report file name, directory name and report type in headers for success data load.
     *
     * @param exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        ReCAPCSVSuccessRecord reCAPCSVSuccessRecord = (ReCAPCSVSuccessRecord) exchange.getIn().getBody();
        String fileName = FilenameUtils.removeExtension(reCAPCSVSuccessRecord.getReportFileName());
        exchange.getIn().setHeader(RecapCommonConstants.REPORT_FILE_NAME, fileName);
        exchange.getIn().setHeader(RecapConstants.REPORT_TYPE, reCAPCSVSuccessRecord.getReportType());
        exchange.getIn().setHeader(RecapConstants.DIRECTORY_NAME, reCAPCSVSuccessRecord.getInstitutionName());
    }
}
