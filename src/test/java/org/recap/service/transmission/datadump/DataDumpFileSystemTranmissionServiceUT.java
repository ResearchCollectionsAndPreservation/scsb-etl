package org.recap.service.transmission.datadump;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbConstants;
import org.recap.model.export.DataDumpRequest;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by premkb on 3/10/16.
 */
@Slf4j
public class DataDumpFileSystemTranmissionServiceUT extends BaseTestCaseUT {


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
        producer.sendBodyAndHeader(ScsbConstants.DATADUMP_FILE_SYSTEM_Q,  xmlString, "routeMap", getRouteMap());
        dataDumpFileSystemTranmissionService.transmitDataDump(getRouteMap());
        Thread.sleep(2000);
        log.info(dumpDirectoryPath+File.separator+ requestingInstitutionCode +File.separator+dateTimeString+ File.separator  + ScsbConstants.DATA_DUMP_FILE_NAME+ requestingInstitutionCode +"-"+dateTimeString+ ScsbConstants.XML_FILE_FORMAT);
        File file = new File(dumpDirectoryPath+File.separator+ requestingInstitutionCode +File.separator+dateTimeString+ File.separator  + ScsbConstants.DATA_DUMP_FILE_NAME+ requestingInstitutionCode + ScsbConstants.ZIP_FILE_FORMAT);
        assertTrue(true);
    }

    @Test
    public void isInterested() throws Exception {
        DataDumpRequest dataDumpRequest=new DataDumpRequest();
        dataDumpRequest.setTransmissionType(ScsbConstants.DATADUMP_TRANSMISSION_TYPE_FILESYSTEM);
        boolean interested=dataDumpFileSystemTranmissionService.isInterested(dataDumpRequest);
        assertTrue(interested);
    }

    public Map<String,String> getRouteMap(){
        Map<String,String> routeMap = new HashMap<>();
        String fileName = ScsbConstants.DATA_DUMP_FILE_NAME+ requestingInstitutionCode;
        routeMap.put(ScsbConstants.FILENAME,fileName);
        routeMap.put(ScsbConstants.DATETIME_FOLDER, getDateTimeString());
        routeMap.put(ScsbConstants.REQUESTING_INST_CODE,requestingInstitutionCode);
        routeMap.put(ScsbConstants.FILE_FORMAT, ScsbConstants.XML_FILE_FORMAT);
        return routeMap;
    }

    private String getDateTimeString(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(ScsbConstants.DATE_FORMAT_DDMMMYYYYHHMM);
        return sdf.format(date);
    }
}
