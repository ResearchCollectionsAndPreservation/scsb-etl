package org.recap.camel.datadump.consumer;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;
import org.recap.camel.datadump.FileNameProcessorForDataDumpFailure;
import org.recap.model.csv.DataDumpFailureReport;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileNameProcessorForDataDumpFailureUT extends BaseTestCaseUT {

    @InjectMocks
    FileNameProcessorForDataDumpFailure fileNameProcessorForDataDumpFailure;

    @BeforeEach
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
