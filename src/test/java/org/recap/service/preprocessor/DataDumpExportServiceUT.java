package org.recap.service.preprocessor;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.junit.*;
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
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DataDumpExportServiceUT extends BaseTestCase {

    DataDumpExportService dataDumpExportService = new DataDumpExportService();

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

    @Value("${datadump.status.file.name}")
    private String dataDumpStatusFileName;

    @Value("${datadump.fetchtype.full}")
    private String fetchTypeFull;

    @Value("${datadump.incremental.date.limit}")
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
        String response = dataDumpExportService.startDataDumpProcess(dataDumpRequest);
        assertNotNull(response);
    }
    @Test
    public void testsetDataDumpRequest(){
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
        dataDumpExportService.setDataDumpRequest(dataDumpRequest,"1","PUL",date.toString(),date.toString(),"1","1","CUL",recapAssistEmailAddress,"0");
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
}
