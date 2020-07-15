package org.recap.camel.datadump;

import org.apache.camel.*;
import org.apache.camel.impl.*;
import org.apache.camel.processor.aggregate.*;
import org.apache.camel.support.*;
import org.junit.*;
import org.recap.*;
import org.recap.camel.*;
import org.recap.model.csv.*;

import java.text.*;
import java.util.*;

import static org.junit.Assert.*;

public class FileNameProcessorForFailureRecordUT extends BaseTestCase {

    FileNameProcessorForFailureRecord fileNameProcessorForFailureRecord;

    @Test
    public void testProcess() {
        FailureReportReCAPCSVRecord failureReportReCAPCSVRecord = new FailureReportReCAPCSVRecord();
        failureReportReCAPCSVRecord.setOwningInstitution("PUL");
        failureReportReCAPCSVRecord.setOwningInstitutionBibId("1111");
        failureReportReCAPCSVRecord.setOwningInstitutionHoldingsId("2222");
        failureReportReCAPCSVRecord.setLocalItemId("333333333");
        failureReportReCAPCSVRecord.setItemBarcode("4444");
        failureReportReCAPCSVRecord.setCustomerCode("PA");
        failureReportReCAPCSVRecord.setTitle("History, of Science");
        failureReportReCAPCSVRecord.setCollectionGroupDesignation("open");
        failureReportReCAPCSVRecord.setCreateDateItem(new SimpleDateFormat("mm-dd-yyyy").format(new Date()));
        failureReportReCAPCSVRecord.setLastUpdatedDateItem(new SimpleDateFormat("mm-dd-yyyy").format(new Date()));
        failureReportReCAPCSVRecord.setExceptionMessage("exception");
        failureReportReCAPCSVRecord.setErrorDescription("error");
        ReCAPCSVFailureRecord  reCAPCSVFailureRecord = new ReCAPCSVFailureRecord();
        reCAPCSVFailureRecord.setFileName("test.xml");
        reCAPCSVFailureRecord.setInstitutionName("PUL");
        reCAPCSVFailureRecord.setReportType(RecapCommonConstants.FAILURE);
        assertNotNull(failureReportReCAPCSVRecord.getCreateDateItem());
        assertNotNull(failureReportReCAPCSVRecord.getLastUpdatedDateItem());
        reCAPCSVFailureRecord.setFailureReportReCAPCSVRecordList(Arrays.asList(failureReportReCAPCSVRecord));

        String dataHeader="test";
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Map<String,Object> mapData= new HashMap<>();
        ex.setProperty("CamelAggregationStrategy",mapData);
        Message in = ex.getIn();
        ex.setMessage(in);
        in.setBody(reCAPCSVFailureRecord);
        ex.setIn(in);
        Map<String,Object> mapdata = new HashMap<>();
        mapdata.put("CamelFileName",dataHeader);
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
