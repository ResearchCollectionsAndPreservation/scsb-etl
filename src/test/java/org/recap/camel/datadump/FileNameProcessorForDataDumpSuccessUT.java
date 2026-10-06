package org.recap.camel.datadump;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;
import org.recap.model.csv.DataDumpSuccessReport;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileNameProcessorForDataDumpSuccessUT extends BaseTestCaseUT {
    FileNameProcessorForDataDumpSuccess fileNameProcessorForDataDumpSuccess;

    @BeforeEach
    public void setUpBefore() {
        fileNameProcessorForDataDumpSuccess = new FileNameProcessorForDataDumpSuccess();
    }

    @Test
    public void testProcess() {
        DataDumpSuccessReport dataDumpSuccessReport = new DataDumpSuccessReport();
        dataDumpSuccessReport.setFileName("test");
        String dataHeader = ";currentPageCount#1";
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        in.setBody(dataDumpSuccessReport);
        ex.setMessage(in);
        ex.setIn(in);
        Map<String, Object> mapdata = new HashMap<>();
        mapdata.put("batchHeaders", dataHeader);
        try {
            fileNameProcessorForDataDumpSuccess.process(ex);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        assertTrue(true);
    }
}
