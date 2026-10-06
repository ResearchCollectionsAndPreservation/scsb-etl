package org.recap.service.transmission.datadump;

import org.apache.camel.CamelContext;
import org.junit.jupiter.api.Test;
import org.recap.BaseTestCase;
import org.recap.ScsbConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataDumpFileSystemTranmissionServiceIT extends BaseTestCase {

    @Autowired
    DataDumpFileSystemTranmissionService dataDumpFileSystemTranmissionService;

    @Value("${etl.data.dump.directory}")
    private String dumpDirectoryPath;

    @Autowired
    private CamelContext camelContext;

    String requestingInstitutionCode = "NYPL";

    String dateTimeString = null;


    @Test
    public void transmitDataDump() throws Exception {
        Map<String, String> routeMap = getRouteMap();
        try {
            dataDumpFileSystemTranmissionService.transmitDataDump(routeMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDateTimeString() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(ScsbConstants.DATE_FORMAT_DDMMMYYYYHHMM);
        return sdf.format(date);
    }

    private Map<String, String> getRouteMap() {
        dateTimeString = getDateTimeString();
        Map<String, String> routeMap = new HashMap<>();
        String fileName = ScsbConstants.DATA_DUMP_FILE_NAME + requestingInstitutionCode;
        routeMap.put(ScsbConstants.FILENAME, fileName);
        routeMap.put(ScsbConstants.DATETIME_FOLDER, dateTimeString);
        routeMap.put(ScsbConstants.REQUESTING_INST_CODE, requestingInstitutionCode);
        routeMap.put(ScsbConstants.FILE_FORMAT, ScsbConstants.XML_FILE_FORMAT);
        return routeMap;
    }

}
