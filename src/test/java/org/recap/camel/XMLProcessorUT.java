package org.recap.camel;

import org.apache.camel.CamelContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.model.jpa.XmlRecordEntity;
import org.recap.repository.ReportDetailRepository;
import org.recap.repository.XmlRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by pvsubrah on 6/21/16.
 */
public class XMLProcessorUT extends BaseTestCase {

    @Autowired
    CamelContext camelContext;

    @Value("${etl.load.directory}")
    private String etlLoadDir;

    @Autowired
    RecordProcessor recordProcessor;

    @Autowired
    XmlRecordRepository xmlRecordRepository;

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Test
    public void process() throws Exception {
        assertNotNull(camelContext);
        assertTrue(camelContext.getStatus().isStarted());
    }

    @Test
    public void testExchangeDoesntRetainMessages() throws Exception {
        List<String> componentNames = camelContext.getComponentNames();
        for (Iterator<String> iterator = componentNames.iterator(); iterator.hasNext(); ) {
            String componentName = iterator.next();
            System.out.println("Component: " + componentName);
        }

        File inputFileEndPoint = getInputFileEndPoint();
        File loadDir = new File(etlLoadDir);
        FileUtils.copyFileToDirectory(inputFileEndPoint, loadDir);

        Thread.sleep(10000);

        FileUtils.deleteDirectory(loadDir);
    }

    private File getInputFileEndPoint() throws URISyntaxException {
        URL resource = getClass().getResource("5Records.xml");
        File file = new File(resource.toURI());
        return file;
    }

    @Test
    public void parseXPath() throws Exception {
        File file = getInputFileEndPoint();

        String fileToString = FileUtils.readFileToString(file,"UTF-8");

        String owningInstitutionId = StringUtils.substringBetween(fileToString, "<owningInstitutionId>", "</owningInstitutionId>");
        System.out.println(owningInstitutionId);
        String owningInstitutionBibId = StringUtils.substringBetween(fileToString, "<owningInstitutionBibId>", "</owningInstitutionBibId>");
        System.out.println(owningInstitutionBibId);
        assertEquals(owningInstitutionId, "NYPL");
        assertEquals(owningInstitutionBibId, ".b153286131");
    }

    @Test
    public void loadSampleData() throws Exception {
        String fileName = "sampleRecordForEtlLoadTest.xml";
        saveXmlRecordEntity(fileName, ".b100006279");
        Page<XmlRecordEntity> xmlRecordEntities = xmlRecordRepository.findByXmlFileName(PageRequest.of(0,10), fileName);
        recordProcessor.process(xmlRecordEntities);
    }

    private XmlRecordEntity saveXmlRecordEntity(String fileName, String owningInstBibId) throws URISyntaxException, IOException {
        XmlRecordEntity xmlRecordEntity = new XmlRecordEntity();
        URL resource = getClass().getResource(fileName);
        File file = new File(resource.toURI());
        String content = FileUtils.readFileToString(file, "UTF-8");
        xmlRecordEntity.setXml(content.getBytes());
        xmlRecordEntity.setOwningInst("NYPL");
        xmlRecordEntity.setOwningInstBibId(owningInstBibId);
        xmlRecordEntity.setDataLoaded(new Date());
        xmlRecordEntity.setXmlFileName(fileName);
        xmlRecordRepository.save(xmlRecordEntity);
        return xmlRecordEntity;
    }

    @Test
    public void testLoadReport() throws Exception {
        String fileName = "etlTestLoadReport.xml";
        File file = new File(getClass().getResource(fileName).toURI());
        FileUtils.copyFileToDirectory(file, new File(etlLoadDir));

        Thread.sleep(2000);

        List<ReportEntity> reportEntities = reportDetailRepository.findByFileNameAndType("etlTestLoadReport.xml", RecapConstants.XML_LOAD);
        ReportEntity reportEntity = reportEntities.get(0);
        List<ReportDataEntity> reportDataEntities =
                reportEntity.getReportDataEntities();

        ReportDataEntity reportDataEntity = reportDataEntities.get(0);
        assertEquals(RecapConstants.FILE_LOAD_STATUS, reportDataEntity.getHeaderName());
        assertEquals(RecapConstants.FILE_LOADED, reportDataEntity.getHeaderValue());
    }

    @Test
    public void testDuplicateXMLFileProcessing() throws Exception {
        String fileName = "etlTestLoadReport.xml";
        File file = new File(getClass().getResource(fileName).toURI());
        FileUtils.copyFileToDirectory(file, new File(etlLoadDir));

        Thread.sleep(2000);

        Long countByXmlFileName = xmlRecordRepository.countByXmlFileName(fileName);
        assertEquals(new Long(1), Long.valueOf(countByXmlFileName));
    }
}
