package org.recap.service.transmission.datadump;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.model.export.DataDumpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by premkb on 3/10/16.
 */

public class DataDumpFileSystemTranmissionServiceUT extends BaseTestCaseUT {

    private static final Logger logger = LoggerFactory.getLogger(DataDumpFileSystemTranmissionServiceUT.class);

    @Value("${etl.data.dump.directory}")
    private String dumpDirectoryPath;

    @Mock
    private ProducerTemplate producer;

    @InjectMocks
    DataDumpFileSystemTranmissionService dataDumpFileSystemTranmissionService;

    @Mock
    CamelContext camelContext;

    private String requestingInstitutionCode = "NYPL";

    private String dateTimeString;

    private String xmlString = "<marcxml:collection xmlns:marcxml=\"http://www.loc.gov/MARC21/slim\">\n" +
            "  <marcxml:record></marcxml:record>\n" +
            "</marcxml:collection>";

    @Test
    public void transmitFileSystemDataDump() throws Exception {
        dateTimeString = getDateTimeString();
        producer.sendBodyAndHeader(RecapConstants.DATADUMP_FILE_SYSTEM_Q,  xmlString, "routeMap", getRouteMap());
        dataDumpFileSystemTranmissionService.transmitDataDump(getRouteMap());
        Thread.sleep(2000);
        logger.info(dumpDirectoryPath+File.separator+ requestingInstitutionCode +File.separator+dateTimeString+ File.separator  + RecapConstants.DATA_DUMP_FILE_NAME+ requestingInstitutionCode +"-"+dateTimeString+ RecapConstants.XML_FILE_FORMAT);
        File file = new File(dumpDirectoryPath+File.separator+ requestingInstitutionCode +File.separator+dateTimeString+ File.separator  + RecapConstants.DATA_DUMP_FILE_NAME+ requestingInstitutionCode + RecapConstants.ZIP_FILE_FORMAT);
        assertTrue(true);
    }

    @Test
    public void isInterested() throws Exception {
        DataDumpRequest dataDumpRequest=new DataDumpRequest();
        dataDumpRequest.setTransmissionType(RecapConstants.DATADUMP_TRANSMISSION_TYPE_FILESYSTEM);
        boolean interested=dataDumpFileSystemTranmissionService.isInterested(dataDumpRequest);
        assertTrue(interested);
    }

    public Map<String,String> getRouteMap(){
        Map<String,String> routeMap = new HashMap<>();
        String fileName = RecapConstants.DATA_DUMP_FILE_NAME+ requestingInstitutionCode;
        routeMap.put(RecapConstants.FILENAME,fileName);
        routeMap.put(RecapConstants.DATETIME_FOLDER, getDateTimeString());
        routeMap.put(RecapConstants.REQUESTING_INST_CODE,requestingInstitutionCode);
        routeMap.put(RecapConstants.FILE_FORMAT, RecapConstants.XML_FILE_FORMAT);
        return routeMap;
    }

    private String getDateTimeString(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(RecapConstants.DATE_FORMAT_DDMMMYYYYHHMM);
        return sdf.format(date);
    }
}
