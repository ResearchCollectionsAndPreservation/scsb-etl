package org.recap.service.executor.datadump;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbConstants;
import org.recap.model.export.DataDumpRequest;
import org.recap.model.search.SearchRecordsRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by premkb on 29/9/16.
 */
public class IncrementalDataDumpExecutorServiceUT extends BaseTestCaseUT {



    @InjectMocks
    IncrementalDataDumpExecutorService incrementalDataDumpExecutorService;

    private String requestingInstitutionCode = "CUL";

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getIncrementalDumpForMarcXmlFileSystem()throws Exception{
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
        institutionCodes.add("NYPL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setTransmissionType("2");
        dataDumpRequest.setOutputFileFormat(ScsbConstants.XML_FILE_FORMAT);
        dataDumpRequest.setDateTimeString(new SimpleDateFormat(ScsbConstants.DATE_FORMAT_DDMMMYYYYHHMM).format(new Date()));
        SearchRecordsRequest searchRecordsRequest=new SearchRecordsRequest();
        incrementalDataDumpExecutorService.populateSearchRequest(searchRecordsRequest,dataDumpRequest);
        boolean fetchType=incrementalDataDumpExecutorService.isInterested(ScsbConstants.DATADUMP_FETCHTYPE_INCREMENTAL);
        assertTrue(fetchType);
    }
}
