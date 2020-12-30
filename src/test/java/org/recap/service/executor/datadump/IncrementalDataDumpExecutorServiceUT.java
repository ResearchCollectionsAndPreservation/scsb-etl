package org.recap.service.executor.datadump;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.model.export.DataDumpRequest;
import org.recap.model.search.SearchRecordsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by premkb on 29/9/16.
 */
public class IncrementalDataDumpExecutorServiceUT extends BaseTestCaseUT {

    private static final Logger logger = LoggerFactory.getLogger(FullDataDumpExecutorServiceUT.class);

    @InjectMocks
    IncrementalDataDumpExecutorService incrementalDataDumpExecutorService;

    @Test
    public void isInterested(){
        boolean response = incrementalDataDumpExecutorService.isInterested(RecapConstants.DATADUMP_FETCHTYPE_INCREMENTAL);
        assertTrue(response);
    }

    @Test
    public void populateSearchRequest()throws Exception{
         incrementalDataDumpExecutorService.populateSearchRequest(getSearchRecordsRequest(),getDataDumpRequest("CUL"));
        assertTrue(true);
    }

    private SearchRecordsRequest getSearchRecordsRequest() {
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
        return searchRecordsRequest;
    }

    private String getDateTimeString() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(RecapConstants.DATE_FORMAT_DDMMMYYYYHHMM);
        return sdf.format(date);
    }
    private DataDumpRequest getDataDumpRequest(String requestingInstitutionCode) {
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
        institutionCodes.add("NYPL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setTransmissionType("0");
        dataDumpRequest.setOutputFileFormat(RecapConstants.XML_FILE_FORMAT);
        dataDumpRequest.setDateTimeString(getDateTimeString());
        return dataDumpRequest;
    }
}
