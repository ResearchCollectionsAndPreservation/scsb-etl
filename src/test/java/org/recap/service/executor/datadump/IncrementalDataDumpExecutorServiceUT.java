package org.recap.service.executor.datadump;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.model.export.DataDumpRequest;
import org.recap.model.search.SearchRecordsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by premkb on 29/9/16.
 */
public class IncrementalDataDumpExecutorServiceUT extends BaseTestCaseUT {

    private static final Logger logger = LoggerFactory.getLogger(FullDataDumpExecutorServiceUT.class);

    @InjectMocks
    IncrementalDataDumpExecutorService incrementalDataDumpExecutorService;

    private String requestingInstitutionCode = "CUL";

    @Before
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
        dataDumpRequest.setOutputFileFormat(RecapConstants.XML_FILE_FORMAT);
        dataDumpRequest.setDateTimeString(new SimpleDateFormat(RecapConstants.DATE_FORMAT_DDMMMYYYYHHMM).format(new Date()));
        SearchRecordsRequest searchRecordsRequest=new SearchRecordsRequest();
        incrementalDataDumpExecutorService.populateSearchRequest(searchRecordsRequest,dataDumpRequest);
        boolean fetchType=incrementalDataDumpExecutorService.isInterested(RecapConstants.DATADUMP_FETCHTYPE_INCREMENTAL);
        assertTrue(fetchType);
    }
}
