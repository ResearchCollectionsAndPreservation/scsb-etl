package org.recap.service.transmission.datadump;

import org.apache.camel.ProducerTemplate;
import org.junit.Ignore;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@Ignore
public class DataDumpFileSystemTranmissionServiceUT extends BaseTestCase {

    private static final Logger logger = LoggerFactory.getLogger(DataDumpFileSystemTranmissionServiceUT.class);

    @Value("${etl.dump.directory}")
    private String dumpDirectoryPath;

    @Autowired
    private ProducerTemplate producer;

    @Autowired
    private DataDumpFileSystemTranmissionService dataDumpFileSystemTranmissionService;

    private String requestingInstitutionCode = "NYPL";

    private String dateTimeString;

    private String xmlString = "<marcxml:collection xmlns:marcxml=\"http://www.loc.gov/MARC21/slim\">\n" +
            "  <marcxml:record></marcxml:record>\n" +
            "</marcxml:collection>";
   // @Test
    public void transmitFileSystemDataDump() throws Exception {
        dateTimeString = getDateTimeString();
        producer.sendBodyAndHeader(RecapConstants.DATADUMP_FILE_SYSTEM_Q,  xmlString, "routeMap", getRouteMap());
        dataDumpFileSystemTranmissionService.transmitDataDump(getRouteMap());
        Thread.sleep(2000);
        logger.info(dumpDirectoryPath+File.separator+ requestingInstitutionCode +File.separator+dateTimeString+ File.separator  + RecapConstants.DATA_DUMP_FILE_NAME+ requestingInstitutionCode +"-"+dateTimeString+ RecapConstants.XML_FILE_FORMAT);
        File file = new File(dumpDirectoryPath+File.separator+ requestingInstitutionCode +File.separator+dateTimeString+ File.separator  + RecapConstants.DATA_DUMP_FILE_NAME+ requestingInstitutionCode + RecapConstants.ZIP_FILE_FORMAT);
        boolean fileExists = file.exists();
        assertTrue(fileExists);
        file.delete();
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
