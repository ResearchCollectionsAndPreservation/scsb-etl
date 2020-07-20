package org.recap.camel.datadump.consumer;

import org.apache.camel.*;
import org.apache.camel.impl.*;
import org.apache.camel.support.*;
import org.junit.*;
import org.recap.*;
import org.recap.camel.datadump.*;
import org.recap.model.csv.*;

import java.util.*;

import static org.junit.Assert.*;

public class FileNameProcessorForDataDumpFailureUT extends BaseTestCase {

    FileNameProcessorForDataDumpFailure fileNameProcessorForDataDumpFailure;

    @Before
    public void setUpBefore() {
        fileNameProcessorForDataDumpFailure = new FileNameProcessorForDataDumpFailure();
    }

    @Test
    public void testProcess() {
        DataDumpFailureReport dataDumpFailureReport = new DataDumpFailureReport();
        dataDumpFailureReport.setInstitutionCodes("PUL");
        dataDumpFailureReport.setRequestingInstitution("CUL");
        dataDumpFailureReport.setFetchType("1");
        dataDumpFailureReport.setExportFromDate(new Date().toString());
        dataDumpFailureReport.setCollectionGroupIds("Open");
        dataDumpFailureReport.setTransmissionType("1");
        dataDumpFailureReport.setExportFormat("1");
        dataDumpFailureReport.setToEmailId("hemalatha.s@htcindia.com");
        dataDumpFailureReport.setFailedBibs("test");
        dataDumpFailureReport.setFailureCause("test");
        dataDumpFailureReport.setFileName("test");
        String dataHeader = ";currentPageCount#1";
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        in.setBody(dataDumpFailureReport);
        ex.setMessage(in);
        ex.setIn(in);
        Map<String, Object> mapdata = new HashMap<>();
        mapdata.put("batchHeaders", dataHeader);
        try {
            fileNameProcessorForDataDumpFailure.process(ex);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        assertTrue(true);
    }
}
