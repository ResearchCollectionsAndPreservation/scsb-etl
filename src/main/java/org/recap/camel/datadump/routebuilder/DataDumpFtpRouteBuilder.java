package org.recap.camel.datadump.routebuilder;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.recap.RecapConstants;
import org.recap.camel.datadump.FileNameProcessorForDataExport;
import org.recap.camel.datadump.ZipFileProcessor;
import org.recap.util.datadump.DataExportHeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by chenchulakshmig on 10/8/16.
 */
@Component
public class DataDumpFtpRouteBuilder extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(DataDumpFtpRouteBuilder.class);

    @Value("${etl.data.dump.ftp.staging.directory}")
    private String s3StagingDir;

    /**
     * The File name processor for data export.
     */
    @Autowired
    FileNameProcessorForDataExport fileNameProcessorForDataExport;

    /**
     * The Zip file processor.
     */
    @Autowired
    ZipFileProcessor zipFileProcessor;

    /**
     * This method is to configure the route to zip the data export file and send it to FTP.
     * @throws Exception
     */
    @Override
    public void configure() throws Exception {
        interceptFrom(RecapConstants.DATADUMP_ZIPFILE_FTP_Q)
                .process(fileNameProcessorForDataExport);

        from(RecapConstants.DATADUMP_ZIPFILE_FTP_Q)
                .onCompletion()
                .onWhen(new ExportFileDumpComplete())
                .process(zipFileProcessor)
                .end()
        .to("file:" + s3StagingDir);
    }

    private class ExportFileDumpComplete implements Predicate {

        /**
         * Evaluates the predicate on the message exchange and returns true if this exchange matches the predicate
         * This predicate evaluates the current page count with total pages count to identify if the exporting of data dump to a file is complete.
         *
         * @param exchange
         * @return
         */
        @Override
        public boolean matches(Exchange exchange) {
            String batchHeaders = (String) exchange.getIn().getHeader("batchHeaders");
            String totalPageCount = getValueFor(batchHeaders, "totalPageCount");
            String currentPageCount = getValueFor(batchHeaders, "currentPageCount");
            logger.info("currentPageCount in DataDumpFtpRouteBuilder--->{}",currentPageCount);
            return totalPageCount.equals(currentPageCount);
        }

        /**
         * Get the value for the key from headers.
         * @param batchHeaderString
         * @param key
         * @return
         */
        private String getValueFor(String batchHeaderString, String key) {
            return new DataExportHeaderUtil().getValueFor(batchHeaderString, key);
        }
    }
}
