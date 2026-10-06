package org.recap.camel;

import org.apache.camel.CamelContext;
import org.junit.jupiter.api.Test;
import org.recap.BaseTestCase;
import org.recap.camel.datadump.DataDumpSequenceProcessor;
import org.recap.camel.datadump.consumer.DataExportCompletionStatusActiveMQConsumer;
import org.recap.camel.dynamicrouter.DynamicRouteBuilder;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.service.formatter.datadump.DeletedJsonFormatterService;
import org.recap.service.formatter.datadump.MarcXmlFormatterService;
import org.recap.service.formatter.datadump.SCSBXmlFormatterService;
import org.recap.util.XmlFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class DynamicRouteBuliderIT extends BaseTestCase {

    @Autowired
    DynamicRouteBuilder dynamicRouteBuilder;

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

    @Value("${etl.data.dump.records.per.file}")
    String dataDumpRecordsPerFile;

    @Test
    public void addDataDumpExportRoutes(){
        try {
            dynamicRouteBuilder.addDataDumpExportRoutes();
        }catch (Exception e){}
    }
}
