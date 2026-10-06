package org.recap.service.executor.datadump;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbConstants;
import org.recap.model.export.DataDumpRequest;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.spring.SwaggerAPIProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Created by premkb on 29/9/16.
 */
public class DeletedDataDumpExecutorServiceUT extends BaseTestCaseUT {

    @InjectMocks
    DeletedDataDumpExecutorService mockedDeletedDataDumpExecutorService;

    @Mock
    SwaggerAPIProvider swaggerAPIProvider;

    @Value("${etl.data.dump.deleted.type.onlyorphan.institution}")
    private final String deletedOnlyOrphanInstitution = "PUL";

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(mockedDeletedDataDumpExecutorService, "deletedOnlyOrphanInstitution", deletedOnlyOrphanInstitution);
    }

    @Test
    public void testpopulateSearchRequest() {
        String[] requestingInstitutionCodes = {"CUL", "NYPL"};
        for (String requestingInstitutionCode :
                requestingInstitutionCodes) {
            SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
            mockedDeletedDataDumpExecutorService.populateSearchRequest(searchRecordsRequest, getDataDumpRequest(requestingInstitutionCode));
            boolean status = mockedDeletedDataDumpExecutorService.isInterested(ScsbConstants.DATADUMP_FETCHTYPE_DELETED);
            assertTrue(status);
        }
    }

    @Test
    public void testpopulateSearchRequestSameInstituionCode() {
        String[] requestingInstitutionCodes = {"CUL", "NYPL"};
        for (String requestingInstitutionCode :
                requestingInstitutionCodes) {
            SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
            ReflectionTestUtils.setField(mockedDeletedDataDumpExecutorService, "deletedOnlyOrphanInstitution", "CUL");
            mockedDeletedDataDumpExecutorService.populateSearchRequest(searchRecordsRequest, getDataDumpRequest(requestingInstitutionCode));
            boolean status = mockedDeletedDataDumpExecutorService.isInterested(ScsbConstants.DATADUMP_FETCHTYPE_DELETED);
            assertTrue(status);
        }
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
        institutionCodes.add("PUL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setTransmissionType("0");
        dataDumpRequest.setOutputFileFormat(ScsbConstants.JSON_FILE_FORMAT);
        dataDumpRequest.setDateTimeString(new SimpleDateFormat(ScsbConstants.DATE_FORMAT_DDMMMYYYYHHMM).format(new Date()));
        return dataDumpRequest;
    }

}
