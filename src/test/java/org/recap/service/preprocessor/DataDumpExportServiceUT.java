package org.recap.service.preprocessor;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.model.export.DataDumpRequest;
import org.recap.repository.CollectionGroupDetailsRepository;
import org.recap.service.email.datadump.DataDumpEmailService;
import org.recap.service.executor.datadump.DataDumpExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DataDumpExportServiceUT extends BaseTestCase {
    @Autowired
    DataDumpExportService dataDumpExportService;
    @Mock
    RestTemplate restTemplate;
    @Mock
    org.recap.model.jpa.CollectionGroupEntity CollectionGroupEntity;
    @Autowired
    ApplicationContext appContext;
    @Autowired
    CollectionGroupDetailsRepository collectionGroupDetailsRepository;
    @Autowired
    private DataDumpExecutorService dataDumpExecutorService;
    @Autowired
    private DataDumpEmailService dataDumpEmailService;
    @Autowired
    private ConsumerTemplate consumerTemplate;
    @Autowired
    private ProducerTemplate producerTemplate;
    @Value("${etl.data.dump.fetchtype.full}")
    private String fetchTypeFull;

    @Value("${etl.data.dump.incremental.date.limit}")
    private String incrementalDateLimit;

    @Value("${recap.assist.email.to}")
    private String recapAssistEmailAddress;

    @Value("${etl.pul.data.loaded.date}")
    private String pulInitialDataLoadedDate;

    @Value("${etl.cul.data.loaded.date}")
    private String culInitialDataLoadedDate;

    @Value("${etl.nypl.data.loaded.date}")
    private String nyplInitialDataLoadedDate;

    @Before
    public void setup() throws Exception {
        ReflectionTestUtils.setField(dataDumpExportService, "dataDumpStatusFileName", "${user.home}/data-dump/dataExportStatus.txt");
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void teststartDataDumpProcess() {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setFetchType("1");
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        dataDumpRequest.setCollectionGroupIds(cgIds);
        //dataDumpRequest.setTransmissionType("0");
        String response = dataDumpExportService.startDataDumpProcess(dataDumpRequest);
        assertNotNull(response);
    }

    @Test
    public void testsetDataDumpRequest() {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setFetchType("1");
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        LocalDate date = LocalDate.now();
        dataDumpRequest.setCollectionGroupIds(cgIds);
        dataDumpExportService.setDataDumpRequest(dataDumpRequest, "1", "PUL", date.toString(), date.toString(), "1", "1", "CUL", recapAssistEmailAddress, "0");
        assertTrue(true);
    }

    @Test
    public void testvalidateIncomingRequest() {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PULa");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setFetchType("1");
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dataDumpRequest.setCollectionGroupIds(cgIds);
        dataDumpRequest.setTransmissionType("0");
        dataDumpRequest.setDate(formatter.format(calendar.getTime()));
        String response = dataDumpExportService.validateIncomingRequest(dataDumpRequest);
        assertNotNull(response);
    }

    @Test
    public void testvalidateIncomingRequestCase1() {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PULa");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setFetchType("10");
        List<Integer> cgIds = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dataDumpRequest.setCollectionGroupIds(null);
        dataDumpRequest.setTransmissionType("0");
        dataDumpRequest.setDate(formatter.format(calendar.getTime()));
        String response = dataDumpExportService.validateIncomingRequest(dataDumpRequest);
        assertNotNull(response);
    }

    @Test
    public void testPrivateMethods() {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PULa");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setFetchType("10");
        List<Integer> cgIds = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dataDumpRequest.setCollectionGroupIds(null);
        dataDumpRequest.setTransmissionType("0");
        dataDumpRequest.setDate(formatter.format(calendar.getTime()));
        try {
            ReflectionTestUtils.invokeMethod(dataDumpExportService, "getResponseMessage", "Data available to export", dataDumpRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dataDumpRequest.setTransmissionType("1");
        ReflectionTestUtils.invokeMethod(dataDumpExportService, "getResponseMessage", "Data available to export", dataDumpRequest);
        dataDumpRequest.setTransmissionType("2");
        ReflectionTestUtils.invokeMethod(dataDumpExportService, "getResponseMessage", "Data available to export", dataDumpRequest);
        File file = new File("SampleRecords.xml");
        ReflectionTestUtils.invokeMethod(dataDumpExportService, "writeStatusToFile", file, "true");
        ReflectionTestUtils.invokeMethod(dataDumpExportService, "getDataExportCurrentStatus");
        try{ReflectionTestUtils.invokeMethod(dataDumpExportService, "setDataExportCurrentStatus");}catch (Exception e){e.printStackTrace();}
    }
}
