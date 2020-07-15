package org.recap.camel.datadump;

import org.apache.camel.*;
import org.apache.camel.impl.*;
import org.apache.camel.support.*;
import org.junit.*;
import org.recap.*;
import org.recap.camel.*;
import org.recap.model.csv.*;

import java.text.*;
import java.util.*;

import static org.junit.Assert.*;

public class FileNameProcessorForSuccessRecordUT {

    FileNameProcessorForSuccessRecord fileNameProcessorForSuccessRecord;

    @Test
    public void testProcess() {
        SuccessReportReCAPCSVRecord  successReportReCAPCSVRecord = new SuccessReportReCAPCSVRecord();
        successReportReCAPCSVRecord.setFileName("test");
        successReportReCAPCSVRecord.setTotalRecordsInFile("test");
        successReportReCAPCSVRecord.setTotalBibsLoaded("10");
        successReportReCAPCSVRecord.setTotalHoldingsLoaded("10");
        successReportReCAPCSVRecord.setTotalBibHoldingsLoaded("10");
        successReportReCAPCSVRecord.setTotalItemsLoaded("10");
        successReportReCAPCSVRecord.setTotalBibItemsLoaded("10");

        ReCAPCSVSuccessRecord  reCAPCSVSuccessRecord = new ReCAPCSVSuccessRecord();
        reCAPCSVSuccessRecord.setFileName("test.xml");
        reCAPCSVSuccessRecord.setInstitutionName("PUL");
        reCAPCSVSuccessRecord.setReportType(RecapCommonConstants.FAILURE);
        reCAPCSVSuccessRecord.setSuccessReportReCAPCSVRecordList(Arrays.asList(successReportReCAPCSVRecord));
        reCAPCSVSuccessRecord.setReportFileName("test");

        String dataHeader="test";
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Map<String,Object> mapData= new HashMap<>();
        ex.setProperty("CamelAggregationStrategy",mapData);
        Message in = ex.getIn();
        ex.setMessage(in);
        in.setBody(reCAPCSVSuccessRecord);
        ex.setIn(in);
        Map<String,Object> mapdata = new HashMap<>();
        mapdata.put("CamelFileName",dataHeader);
        in.setHeaders(mapdata);
        fileNameProcessorForSuccessRecord = new FileNameProcessorForSuccessRecord();
        try {
            fileNameProcessorForSuccessRecord.process(ex);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        assertTrue(true);
    }

}
