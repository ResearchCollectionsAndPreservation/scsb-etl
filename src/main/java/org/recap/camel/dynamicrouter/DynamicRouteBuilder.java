package org.recap.camel.dynamicrouter;

import org.apache.camel.CamelContext;
import org.recap.camel.datadump.DataDumpSequenceProcessor;
import org.recap.camel.datadump.consumer.DataExportCompletionStatusActiveMQConsumer;
import org.recap.camel.datadump.routebuilder.DataExportRouteBuilder;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.service.formatter.datadump.DeletedJsonFormatterService;
import org.recap.service.formatter.datadump.MarcXmlFormatterService;
import org.recap.service.formatter.datadump.SCSBXmlFormatterService;
import org.recap.util.XmlFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by angelind on 15/11/16.
 */
@Component
public class DynamicRouteBuilder {

    @Autowired
    CamelContext camelContext;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    MarcXmlFormatterService marcXmlFormatterService;

    @Autowired
    SCSBXmlFormatterService scsbXmlFormatterService;

    @Autowired
    DeletedJsonFormatterService deletedJsonFormatterService;

    @Autowired
    XmlFormatter xmlFormatter;

    @Autowired
    private DataExportCompletionStatusActiveMQConsumer dataExportCompletionStatusActiveMQConsumer;

    @Autowired
    private DataDumpSequenceProcessor dataDumpSequenceProcessor;

    @Value("${data.dump.records.per.file}")
    String dataDumpRecordsPerFile;

    /**
     * This method initiates the camel routes for data dump dynamically.
     */
    public void addDataDumpExportRoutes() {
        new DataExportRouteBuilder(camelContext, bibliographicDetailsRepository, marcXmlFormatterService, scsbXmlFormatterService,
                deletedJsonFormatterService, xmlFormatter, dataDumpRecordsPerFile, dataExportCompletionStatusActiveMQConsumer, dataDumpSequenceProcessor);
    }
}
