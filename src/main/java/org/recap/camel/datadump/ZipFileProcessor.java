package org.recap.camel.datadump;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.OnCompletionProcessor;
import org.apache.camel.processor.aggregate.zipfile.ZipAggregationStrategy;
import org.recap.RecapConstants;
import org.recap.util.datadump.DataExportHeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by peris on 11/6/16.
 */
@Component
public class ZipFileProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(ZipFileProcessor.class);

    /**
     * The Ftp user name.
     */
    @Value("${ftp.userName}")
    String ftpUserName;

    /**
     * The Ftp known host.
     */
    @Value("${ftp.knownHost}")
    String ftpKnownHost;

    /**
     * The Ftp private key.
     */
    @Value("${ftp.privateKey}")
    String ftpPrivateKey;

    /**
     * The Ftp data dump remote server.
     */
    @Value("${ftp.datadump.remote.server}")
    String ftpDataDumpRemoteServer;

    @Value("${etl.dump.ftp.staging.directory}")
    private String ftpStagingDir;

    /**
     * The Data export email processor.
     */
    @Autowired
    DataExportEmailProcessor dataExportEmailProcessor;


    ProducerTemplate producer;
    Exchange exchange;

    public ZipFileProcessor() {

    }

    public ZipFileProcessor(ProducerTemplate producer, Exchange exchange) {
        this.producer = producer;
        this.exchange = exchange;
    }

    /**
     * This method is invoked by route to zip the data dump files.
     *
     * @param exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        String batchHeaders = (String) exchange.getIn().getHeader("batchHeaders");
        String folderName = getValueFor(batchHeaders, "folderName");

        dataExportEmailProcessor.setInstitutionCodes(getInstitutionCodes(getValueFor(batchHeaders, "institutionCodes")));
        dataExportEmailProcessor.setTransmissionType(getValueFor(batchHeaders, "transmissionType"));
        dataExportEmailProcessor.setFolderName(folderName);
        dataExportEmailProcessor.setRequestingInstitutionCode(getValueFor(batchHeaders, "requestingInstitutionCode"));
        dataExportEmailProcessor.setToEmailId(getValueFor(batchHeaders, "toEmailId"));
        dataExportEmailProcessor.setRequestId(getValueFor(batchHeaders, "requestId"));
        dataExportEmailProcessor.setFetchType(getValueFor(batchHeaders, "fetchType"));

        Route ftpRoute = exchange.getContext().getRoute(RecapConstants.FTP_ROUTE);
        if (null != ftpRoute) {
            exchange.getContext().removeRoute(RecapConstants.FTP_ROUTE);
            logger.info(RecapConstants.FTP_ROUTE + " Removed");
        }

        exchange.getContext().addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                onCompletion()
                        .choice()
                        .when(exchangeProperty(RecapConstants.CAMEL_BATCH_COMPLETE))
                        .log("Sending Email After FTP Zipping")
                        .process(dataExportEmailProcessor)
                        .log("Data dump zipping completed.")
                        .bean(new ZipFileProcessor(exchange.getContext().createProducerTemplate(), exchange), "ftpOnCompletion")
                        .end();
                from("file:" + ftpStagingDir + File.separator + folderName + "?noop=true&antInclude=*.xml,*.json")
                        .routeId(RecapConstants.FTP_ROUTE)
                        .aggregate(new ZipAggregationStrategy(true, true))
                        .constant(true)
                        .completionFromBatchConsumer()
                        .eagerCheckCompletion()
                        .to("sftp://" + ftpUserName + "@" + ftpDataDumpRemoteServer + File.separator + "?fileName=" + folderName + ".zip"
                                + "&privateKeyFile=" + ftpPrivateKey + "&knownHostsFile=" + ftpKnownHost);
            }
        });
    }

    private String getValueFor(String batchHeaderString, String key) {
        return new DataExportHeaderUtil().getValueFor(batchHeaderString, key);
    }

    private List<String> getInstitutionCodes(String institutionCodes) {
        List codes = new ArrayList();
        StringTokenizer stringTokenizer = new StringTokenizer(institutionCodes, "*");
        while (stringTokenizer.hasMoreTokens()) {
            codes.add(stringTokenizer.nextToken());
        }
        return codes;
    }

    public void ftpOnCompletion() {
        logger.info("FTP OnCompletionProcessor");
        String batchHeaders = (String) exchange.getIn().getHeader("batchHeaders");
        String reqestingInst = getValueFor(batchHeaders, "requestingInstitutionCode");
        logger.info("Req Inst -> " + reqestingInst);
        if (RecapConstants.EXPORT_SCHEDULER_CALL) {
            producer.sendBody(RecapConstants.DATA_DUMP_COMPLETION_FROM, reqestingInst);
        }
        if(reqestingInst.equalsIgnoreCase(RecapConstants.PRINCETON)){
            producer.sendBody(RecapConstants.DATA_DUMP_COMPLETION_TOPIC_STATUS_PUL, RecapConstants.DATA_DUMP_COMPLETION_TOPIC_MESSAGE);
        }else if(reqestingInst.equalsIgnoreCase(RecapConstants.COLUMBIA)){
            producer.sendBody(RecapConstants.DATA_DUMP_COMPLETION_TOPIC_STATUS_CUL, RecapConstants.DATA_DUMP_COMPLETION_TOPIC_MESSAGE);
        }else if(reqestingInst.equalsIgnoreCase(RecapConstants.NYPL)){
            producer.sendBody(RecapConstants.DATA_DUMP_COMPLETION_TOPIC_STATUS_NYPL, RecapConstants.DATA_DUMP_COMPLETION_TOPIC_MESSAGE);
        }
    }
}
