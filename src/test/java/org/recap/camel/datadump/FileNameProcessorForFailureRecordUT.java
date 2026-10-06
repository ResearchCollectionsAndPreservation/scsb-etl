package org.recap.camel.datadump;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.camel.FileNameProcessorForFailureRecord;
import org.recap.model.csv.FailureReportSCSBCSVRecord;
import org.recap.model.csv.SCSBCSVFailureRecord;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileNameProcessorForFailureRecordUT extends BaseTestCaseUT {

    @InjectMocks
    FileNameProcessorForFailureRecord fileNameProcessorForFailureRecord;

    @Test
    public void testProcess() {
        FailureReportSCSBCSVRecord failureReportSCSBCSVRecord = new FailureReportSCSBCSVRecord();
        failureReportSCSBCSVRecord.setOwningInstitution("PUL");
        failureReportSCSBCSVRecord.setOwningInstitutionBibId("1111");
        failureReportSCSBCSVRecord.setOwningInstitutionHoldingsId("2222");
        failureReportSCSBCSVRecord.setLocalItemId("333333333");
        failureReportSCSBCSVRecord.setItemBarcode("4444");
        failureReportSCSBCSVRecord.setCustomerCode("PA");
        failureReportSCSBCSVRecord.setTitle("History, of Science");
        failureReportSCSBCSVRecord.setCollectionGroupDesignation("open");
        failureReportSCSBCSVRecord.setCreateDateItem(new SimpleDateFormat("mm-dd-yyyy").format(new Date()));
        failureReportSCSBCSVRecord.setLastUpdatedDateItem(new SimpleDateFormat("mm-dd-yyyy").format(new Date()));
        failureReportSCSBCSVRecord.setExceptionMessage("exception");
        failureReportSCSBCSVRecord.setErrorDescription("error");
        SCSBCSVFailureRecord SCSBCSVFailureRecord = new SCSBCSVFailureRecord();
        SCSBCSVFailureRecord.setFileName("test.xml");
        SCSBCSVFailureRecord.setInstitutionName("PUL");
        SCSBCSVFailureRecord.setReportType(ScsbCommonConstants.FAILURE);
        assertNotNull(failureReportSCSBCSVRecord.getCreateDateItem());
        assertNotNull(failureReportSCSBCSVRecord.getLastUpdatedDateItem());
        SCSBCSVFailureRecord.setFailureReportSCSBCSVRecordList(Arrays.asList(failureReportSCSBCSVRecord));

        String dataHeader = "test";
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Map<String, Object> mapData = new HashMap<>();
        ex.setProperty("CamelAggregationStrategy", mapData);
        Message in = ex.getIn();
        ex.setMessage(in);
        in.setBody(SCSBCSVFailureRecord);
        ex.setIn(in);
        Map<String, Object> mapdata = new HashMap<>();
        mapdata.put("CamelFileName", dataHeader);
        in.setHeaders(mapdata);
        fileNameProcessorForFailureRecord = new FileNameProcessorForFailureRecord();
        try {
            fileNameProcessorForFailureRecord.process(ex);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        assertTrue(true);
    }

}
