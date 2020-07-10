package org.recap.service.transmission.datadump;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.export.DataDumpRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by premkb on 2/10/16.
 */
@Ignore
public class DataDumpFtpTransmissionServiceUT extends BaseTestCase {

    /*private static final Logger logger = LoggerFactory.getLogger(DataDumpFtpTransmissionServiceUT.class);

    @Autowired
    private ProducerTemplate producer;

    @Value("${etl.dump.directory}")
    private String dumpDirectoryPath;

    @Value("${ftp.userName}")
    private String ftpUserName;

    @Value("${ftp.knownHost}")
    private String ftpKnownHost;

    @Value("${ftp.privateKey}")
    private String ftpPrivateKey;

    @Value("${ftp.datadump.remote.server}")
    private String ftpDataDumpRemoteServer;

    @Spy
    DataDumpFtpTransmissionService dataDumpFtpTransmissionService;

    private String requestingInstitutionCode = "NYPL";

    private String dateTimeString;

    private String xmlString = "<marcxml:collection xmlns:marcxml=\"http://www.loc.gov/MARC21/slim\">\n" +
            "  <marcxml:record></marcxml:record>\n" +
            "</marcxml:collection>";

    @Before
    public void beforeTest() {
        dataDumpFtpTransmissionService = Mockito.spy(DataDumpFtpTransmissionService.class);
    }

    @Test
    public void testMethod() throws Exception {
        dateTimeString = getDateTimeString();
        producer.sendBodyAndHeader(RecapConstants.DATADUMP_FILE_SYSTEM_Q, xmlString, "routeMap", getRouteMap());
        dataDumpFtpTransmissionService.transmitDataDump(getRouteMap());
        String dateTimeString = getDateTimeString();
        String ftpFileName = RecapConstants.DATA_DUMP_FILE_NAME + requestingInstitutionCode + RecapConstants.ZIP_FILE_FORMAT;
        logger.info("ftpFileName---->" + ftpFileName);
        ftpDataDumpRemoteServer = ftpDataDumpRemoteServer + File.separator + requestingInstitutionCode + File.separator + dateTimeString;
        System.out.println("ftpDataDumpRemoteServer--->" + ftpDataDumpRemoteServer);
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("seda:testDataDumpFtp")
                        .pollEnrich("sftp://" + ftpUserName + "@" + ftpDataDumpRemoteServer + "?privateKeyFile=" + ftpPrivateKey + "&knownHostsFile=" + ftpKnownHost + "&fileName=" + ftpFileName);
            }
        });
        String response = producer.requestBody("seda:testDataDumpFtp", "", String.class);
        Thread.sleep(1000);
        assertNotNull(response);
    }
    private Map<String, String> getRouteMap() {
        Map<String, String> routeMap = new HashMap<>();
        String fileName = RecapConstants.DATA_DUMP_FILE_NAME + requestingInstitutionCode;
        routeMap.put(RecapConstants.FILENAME, fileName);
        routeMap.put(RecapConstants.DATETIME_FOLDER, dateTimeString);
        routeMap.put(RecapConstants.REQUESTING_INST_CODE, requestingInstitutionCode);
        routeMap.put(RecapConstants.FILE_FORMAT, RecapConstants.XML_FILE_FORMAT);
        return routeMap;
    }*/

    private String getDateTimeString() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(RecapConstants.DATE_FORMAT_DDMMMYYYYHHMM);
        return sdf.format(date);
    }
    @Spy
    DataDumpFtpTransmissionService dataDumpFtpTransmissionService;
    @Before
    public void beforeTest() {
        dataDumpFtpTransmissionService = Mockito.spy(DataDumpFtpTransmissionService.class);
    }
    @Test
    public void testIsInterested() {
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
        dataDumpFtpTransmissionService.isInterested(dataDumpRequest);
    }
}
