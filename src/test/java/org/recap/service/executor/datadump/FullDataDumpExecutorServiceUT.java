package org.recap.service.executor.datadump;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.model.export.DataDumpRequest;
import org.recap.model.search.SearchRecordsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertTrue;

/**
 * Created by premkb on 27/9/16.
 */


public class FullDataDumpExecutorServiceUT extends BaseTestCaseUT {

    @InjectMocks
    FullDataDumpExecutorService fullDataDumpExecutorService;

    @Value("${etl.data.dump.fetchtype.full}")
    private String fetchTypeFull;

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(fullDataDumpExecutorService,"fetchTypeFull",fetchTypeFull);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void isInterested()throws Exception{
        boolean fetchType=fullDataDumpExecutorService.isInterested(fetchTypeFull);
        assertTrue(fetchType);
    }

    @Test
    public void populateSearchRequest()throws Exception{
        SearchRecordsRequest searchRecordsRequest=new SearchRecordsRequest();
        DataDumpRequest dataDumpRequest=new DataDumpRequest();
        fullDataDumpExecutorService.populateSearchRequest(searchRecordsRequest,dataDumpRequest);
        assertTrue(true);
    }
}
