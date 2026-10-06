package org.recap.camel.datadump;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;
import org.recap.ScsbCommonConstants;
import org.recap.camel.FileNameProcessorForSuccessRecord;
import org.recap.model.csv.SCSBCSVSuccessRecord;
import org.recap.model.csv.SuccessReportSCSBCSVRecord;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileNameProcessorForSuccessRecordUT {

    FileNameProcessorForSuccessRecord fileNameProcessorForSuccessRecord;

    @Test
    public void testProcess() {
        SuccessReportSCSBCSVRecord successReportSCSBCSVRecord = new SuccessReportSCSBCSVRecord();
        successReportSCSBCSVRecord.setFileName("test");
        successReportSCSBCSVRecord.setTotalRecordsInFile("test");
        successReportSCSBCSVRecord.setTotalBibsLoaded("10");
        successReportSCSBCSVRecord.setTotalHoldingsLoaded("10");
        successReportSCSBCSVRecord.setTotalBibHoldingsLoaded("10");
        successReportSCSBCSVRecord.setTotalItemsLoaded("10");
        successReportSCSBCSVRecord.setTotalBibItemsLoaded("10");

        SCSBCSVSuccessRecord SCSBCSVSuccessRecord = new SCSBCSVSuccessRecord();
        SCSBCSVSuccessRecord.setFileName("test.xml");
        SCSBCSVSuccessRecord.setInstitutionName("PUL");
        SCSBCSVSuccessRecord.setReportType(ScsbCommonConstants.FAILURE);
        SCSBCSVSuccessRecord.setSuccessReportSCSBCSVRecordList(Arrays.asList(successReportSCSBCSVRecord));
        SCSBCSVSuccessRecord.setReportFileName("test");

        String dataHeader="test";
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Map<String,Object> mapData= new HashMap<>();
        ex.setProperty("CamelAggregationStrategy",mapData);
        Message in = ex.getIn();
        ex.setMessage(in);
        in.setBody(SCSBCSVSuccessRecord);
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
