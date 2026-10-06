package org.recap.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbConstants;
import org.recap.camel.dynamicrouter.DynamicRouteBuilder;
import org.recap.controller.swagger.DataDumpRestController;
import org.recap.service.DataDumpSolrService;
import org.recap.service.DataExportHelperService;
import org.recap.service.DataExportValidateService;
import org.recap.service.preprocessor.DataDumpExportService;
import org.recap.util.datadump.DataDumpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

/**
 * Created by premkb on 19/8/16.
 */

@ExtendWith({SpringExtension.class})
public class DataDumpRestControllerUT extends BaseTestCaseUT {


    @InjectMocks
    DataDumpRestController mockedDataDumpRestController;

    @Mock
    DataDumpSolrService mockedDataDumpSolrService;

    @Mock
    DataExportValidateService dataExportValidateService;

    @Mock
    DataExportHelperService dataExportHelperService;

    @Value("${scsb.solr.doc.url}")
    String solrClientUrl;
    @Value("${etl.data.dump.status.file.name}")
    String dataDumpStatusFileName;
    @Mock
    RestTemplate mockedRestTemplate;
    @Mock
    DataDumpExportService mockedDataDumpExportService;
    @Mock
    DynamicRouteBuilder dynamicRouteBuilder;
    @Mock
    DataDumpUtil dataDumpUtil;
    private ExecutorService executorService;
    private final String institutionCodes = "CUL";
    private final String requestingInstitutionCode = "NYPL";
    private final String fetchType = "0";
    private final String outputFormat = "0";
    private final String date = new Date().toString();
    private final String toDate = null;
    private final String collectionGroupIds = "1,2";
    private final String transmissionType = "1";
    private final String emailToAddress = "test@gmail.com";
    private final String imsDepositoryCodes = "RECAP";
    private final String userName = "testUser";


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testExportDataDump() throws Exception {
        Mockito.doNothing().when(dataDumpUtil).setDataDumpRequest(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),any());
        Mockito.when(mockedDataDumpExportService.startDataDumpProcess(Mockito.any())).thenReturn(ScsbConstants.DATADUMP_PROCESS_STARTED);
        Mockito.when(dataExportValidateService.validateIncomingRequest(Mockito.any())).thenReturn(null);
        Mockito.when(dataExportHelperService.checkForExistingRequestAndStart(any(),any())).thenReturn("");
        String response = mockedDataDumpRestController.exportDataDump(institutionCodes, requestingInstitutionCode, imsDepositoryCodes, fetchType, outputFormat, date, toDate, collectionGroupIds, transmissionType, emailToAddress, userName);
        assertNotNull(response);
    }

}


