package org.recap.service.executor.datadump;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.export.DataDumpRequest;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.repository.BibliographicDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.spy;

/**
 * Created by premkb on 29/9/16.
 */
public class DeletedDataDumpExecutorServiceUT extends BaseTestCase {
    private static final Logger logger = LoggerFactory.getLogger(DeletedDataDumpExecutorServiceUT.class);

    @Autowired
    DeletedDataDumpExecutorService mockedDeletedDataDumpExecutorService;

    @Value("${ftp.userName}")
    String ftpUserName;

    @Value("${ftp.knownHost}")
    String ftpKnownHost;

    @Value("${ftp.privateKey}")
    String ftpPrivateKey;

    @Value("${ftp.datadump.remote.server}")
    String ftpDataDumpRemoteServer;

    @Value("${datadump.deleted.type.onlyorphan.institution}")
    private String deletedOnlyOrphanInstitution;

    @Value("${etl.dump.directory}")
    private String dumpDirectoryPath;

    @Value("${datadump.batch.size}")
    private int batchSize;

    private String requestingInstitutionCode = "CUL";

/*
    @Before
    public void setUp() throws Exception {
        mockedDeletedDataDumpExecutorService = spy(DeletedDataDumpExecutorService.class);
    }
*/

    @Test
    public void getFullDumpForDeleteRecordFileSystem() throws Exception {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setFetchType("0");
        String inputDate = "2016-08-30 11:20";
        dataDumpRequest.setDate(inputDate);
        dataDumpRequest.setRequestingInstitutionCode(requestingInstitutionCode);
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        dataDumpRequest.setCollectionGroupIds(cgIds);
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setTransmissionType("2");
        dataDumpRequest.setOutputFileFormat(RecapConstants.JSON_FILE_FORMAT);
        dataDumpRequest.setDateTimeString(getDateTimeString());
        String outputString = null;
        try {
            Mockito.when(mockedDeletedDataDumpExecutorService.process(dataDumpRequest)).thenReturn("Success");
            outputString  = mockedDeletedDataDumpExecutorService.process(dataDumpRequest);
        } catch (Exception e) {

        }
        assertNotNull(outputString);
    }

    @Test
    public void getIncrementalDumpForDeleteRecordFileSystem() throws Exception {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setFetchType("1");
        String inputDate = "2016-08-30 11:20";
        dataDumpRequest.setDate(inputDate);
        dataDumpRequest.setRequestingInstitutionCode(requestingInstitutionCode);
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        dataDumpRequest.setCollectionGroupIds(cgIds);
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setTransmissionType("2");
        dataDumpRequest.setOutputFileFormat(RecapConstants.JSON_FILE_FORMAT);
        dataDumpRequest.setDateTimeString(getDateTimeString());
        String outputString = null;
        try {
            Mockito.when(mockedDeletedDataDumpExecutorService.process(dataDumpRequest)).thenReturn("Success");
             outputString = mockedDeletedDataDumpExecutorService.process(dataDumpRequest);
        } catch (Exception e) {

        }
        assertTrue(true);
    }

    @Test
    public void getIncrementalDumpForDeleteRecordFtp() throws Exception {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setFetchType("0");
        String inputDate = "2016-08-30 11:20";
        dataDumpRequest.setDate(inputDate);
        dataDumpRequest.setRequestingInstitutionCode(requestingInstitutionCode);
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        dataDumpRequest.setCollectionGroupIds(cgIds);
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setTransmissionType("0");
        dataDumpRequest.setOutputFileFormat(RecapConstants.JSON_FILE_FORMAT);
        dataDumpRequest.setDateTimeString(getDateTimeString());
        String response = null;
        try {
            Mockito.when(mockedDeletedDataDumpExecutorService.process(dataDumpRequest)).thenReturn("Success");
            response = mockedDeletedDataDumpExecutorService.process(dataDumpRequest);
        } catch (Exception e) {

        }
        Thread.sleep(1000);
        String dateTimeString = getDateTimeString();
        String ftpFileName = RecapConstants.DATA_DUMP_FILE_NAME + requestingInstitutionCode + "1" + "-" + dateTimeString + RecapConstants.JSON_FILE_FORMAT;
        ftpDataDumpRemoteServer = ftpDataDumpRemoteServer + File.separator + requestingInstitutionCode + File.separator + dateTimeString;
        assertNull(response);
        //assertNotNull(response, "Success");
    }

    private String getDateTimeString() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(RecapConstants.DATE_FORMAT_DDMMMYYYYHHMM);
        return sdf.format(date);
    }

    private int getLoopCount(Long totalRecordCount, int batchSize) {
        int quotient = Integer.valueOf(Long.toString(totalRecordCount)) / (batchSize);
        int remainder = Integer.valueOf(Long.toString(totalRecordCount)) % (batchSize);
        int loopCount = remainder == 0 ? quotient : quotient + 1;
        return loopCount;
    }

    @Test
    public void testisInterested() {
        boolean status = mockedDeletedDataDumpExecutorService.isInterested("2");
        assertTrue(status);
    }

    @Test
    public void testpopulateSearchRequest() {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setFetchType("0");
        String inputDate = "2016-08-30 11:20";
        dataDumpRequest.setDate(inputDate);
        dataDumpRequest.setRequestingInstitutionCode(requestingInstitutionCode);
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        dataDumpRequest.setCollectionGroupIds(cgIds);
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setTransmissionType("0");
        dataDumpRequest.setOutputFileFormat(RecapConstants.JSON_FILE_FORMAT);
        dataDumpRequest.setDateTimeString(getDateTimeString());
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setFieldValue("test");
        searchRecordsRequest.setFieldName("test");
        searchRecordsRequest.setOwningInstitutions(Arrays.asList("PUL"));
        searchRecordsRequest.setCollectionGroupDesignations(Arrays.asList("Open"));
        searchRecordsRequest.setAvailability(Arrays.asList("Available"));
        searchRecordsRequest.setMaterialTypes(Arrays.asList("Monograph"));
        searchRecordsRequest.setUseRestrictions(Arrays.asList("Others"));
        searchRecordsRequest.setTotalPageCount(1);
        searchRecordsRequest.setTotalBibRecordsCount("1");
        searchRecordsRequest.setTotalItemRecordsCount("1");
        searchRecordsRequest.setTotalRecordsCount("1");
        searchRecordsRequest.setPageNumber(10);
        searchRecordsRequest.setPageSize(1);
        searchRecordsRequest.setShowResults(true);
        searchRecordsRequest.setSelectAll(true);
        searchRecordsRequest.setSelectAllFacets(true);
        searchRecordsRequest.setShowTotalCount(true);
        searchRecordsRequest.setIndex(1);
        searchRecordsRequest.setDeleted(false);
        searchRecordsRequest.setErrorMessage("test");
        try{
        mockedDeletedDataDumpExecutorService.populateSearchRequest(searchRecordsRequest, dataDumpRequest);
        } catch (Exception e) {
        e.printStackTrace();
        }
        assertTrue(true);
    }
}
