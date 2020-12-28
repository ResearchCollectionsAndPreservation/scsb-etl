package org.recap.service.transmission.datadump;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.model.export.DataDumpRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DataDumpTransmissionServiceUT extends BaseTestCaseUT {

    @InjectMocks
    DataDumpTransmissionService dataDumpTransmissionService;

    @Mock
    DataDumpFileSystemTranmissionService dataDumpFileSystemTranmissionService;

    @Mock
    DataDumpS3TransmissionService DataDumpS3TransmissionService;

    @Mock
    DataDumpTransmissionInterface dataDumpTransmissionInterface;


    String requestingInstitutionCode = "NYPL";

    String dateTimeString = null;

    @Test
    public void teststartTranmission(){
        List<DataDumpTransmissionInterface> dataDumpTransmissionInterfaceList=new ArrayList<>();
        dataDumpTransmissionInterfaceList.add(dataDumpTransmissionInterface);
        ReflectionTestUtils.setField(dataDumpTransmissionService,"dataDumpTransmissionInterfaceList",dataDumpTransmissionInterfaceList);
        Mockito.when(dataDumpTransmissionInterface.isInterested(Mockito.any())).thenReturn(true);
        dataDumpTransmissionService.startTranmission(getDataDumpRequest(),getRouteMap());
        assertTrue(true);
    }

    @Test
    public void teststartTranmissionException() throws Exception {
        List<DataDumpTransmissionInterface> dataDumpTransmissionInterfaceList=new ArrayList<>();
        dataDumpTransmissionInterfaceList.add(dataDumpTransmissionInterface);
        ReflectionTestUtils.setField(dataDumpTransmissionService,"dataDumpTransmissionInterfaceList",dataDumpTransmissionInterfaceList);
        Mockito.when(dataDumpTransmissionInterface.isInterested(Mockito.any())).thenReturn(true);
        Mockito.doThrow(NullPointerException.class).when(dataDumpTransmissionInterface).transmitDataDump(Mockito.any());
        dataDumpTransmissionService.startTranmission(getDataDumpRequest(),getRouteMap());
        assertTrue(true);
    }

    @Test
    public void getTransmissionService() throws Exception {
        List<DataDumpTransmissionInterface> dataDumpTransmissionInterfaces=dataDumpTransmissionService.getTransmissionService();
        assertNotNull(dataDumpTransmissionInterfaces);
    }

    private DataDumpRequest getDataDumpRequest() {
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
        return dataDumpRequest;
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
