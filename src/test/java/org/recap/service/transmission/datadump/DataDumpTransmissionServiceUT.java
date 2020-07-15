package org.recap.service.transmission.datadump;

import org.junit.*;
import org.recap.*;
import org.recap.model.export.*;
import org.springframework.beans.factory.annotation.*;

import java.text.*;
import java.util.*;

import static org.junit.Assert.*;

public class DataDumpTransmissionServiceUT extends BaseTestCase {
    @Autowired
    DataDumpTransmissionService dataDumpTransmissionService;

    String requestingInstitutionCode = "NYPL";

    String dateTimeString = null;

    @Test
    public void teststartTranmission(){
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setFetchType("0");
        dataDumpRequest.setRequestingInstitutionCode("NYPL");
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        dataDumpRequest.setCollectionGroupIds(cgIds);
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("CUL");
        institutionCodes.add("NYPL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setTransmissionType("2");
        dataDumpRequest.setOutputFileFormat(RecapConstants.XML_FILE_FORMAT);
        dataDumpRequest.setDateTimeString(getDateTimeString());
        dataDumpTransmissionService.startTranmission(dataDumpRequest,getRouteMap());
        assertTrue(true);
    }
    private String getDateTimeString() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(RecapConstants.DATE_FORMAT_DDMMMYYYYHHMM);
        return sdf.format(date);
    }
    private Map<String, String> getRouteMap() {
        dateTimeString = getDateTimeString();
        Map<String, String> routeMap = new HashMap<>();
        String fileName = RecapConstants.DATA_DUMP_FILE_NAME + requestingInstitutionCode;
        routeMap.put(RecapConstants.FILENAME, fileName);
        routeMap.put(RecapConstants.DATETIME_FOLDER, dateTimeString);
        routeMap.put(RecapConstants.REQUESTING_INST_CODE, requestingInstitutionCode);
        routeMap.put(RecapConstants.FILE_FORMAT, RecapConstants.XML_FILE_FORMAT);
        return routeMap;
    }
}
