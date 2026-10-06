package org.recap.camel;

import org.apache.camel.CamelContext;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;
import org.recap.camel.dynamicrouter.DynamicRouteBuilder;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.service.formatter.datadump.DeletedJsonFormatterService;
import org.recap.service.formatter.datadump.MarcXmlFormatterService;
import org.recap.service.formatter.datadump.SCSBXmlFormatterService;
import org.recap.util.XmlFormatter;

public class DynamicRouteBuliderUT extends BaseTestCaseUT {

    @InjectMocks
    DynamicRouteBuilder dynamicRouteBuilder;
    
    @Mock
    CamelContext camelContext;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    MarcXmlFormatterService marcXmlFormatterService;

    @Mock
    SCSBXmlFormatterService scsbXmlFormatterService;

    @Mock
    DeletedJsonFormatterService deletedJsonFormatterService;

    @Mock
    XmlFormatter xmlFormatter;


    @Test
    public void addDataDumpExportRoutes(){
        dynamicRouteBuilder.addDataDumpExportRoutes();
    }
}
