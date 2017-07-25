package org.recap.camel;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.XmlRecordEntity;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.repository.HoldingsDetailsRepository;
import org.recap.repository.ItemDetailsRepository;
import org.recap.repository.XmlRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.net.URL;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by angelind on 27/7/16.
 */
public class EtlDataLoadProcessorUT extends BaseTestCase{

    @Autowired
    XmlRecordRepository xmlRecordRepository;

    @Autowired
    EtlDataLoadProcessor etlDataLoadProcessor;

    @Autowired
    RecordProcessor recordProcessor;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    HoldingsDetailsRepository holdingsDetailsRepository;

    @Autowired
    ItemDetailsRepository itemDetailsRepository;

    @Autowired
    ProducerTemplate producer;

    @Test
    public void testStartLoadProcessWithXmlFileName() throws Exception {

        assertNotNull(etlDataLoadProcessor);
        assertNotNull(recordProcessor);
        assertNotNull(xmlRecordRepository);
        assertNotNull(bibliographicDetailsRepository);
        assertNotNull(holdingsDetailsRepository);
        assertNotNull(itemDetailsRepository);
        assertNotNull(producer);

        XmlRecordEntity xmlRecordEntity = new XmlRecordEntity();
        String xmlFileName = "sampleRecordForEtlLoadTest.xml";
        xmlRecordEntity.setXmlFileName(xmlFileName);
        xmlRecordEntity.setOwningInstBibId(".b100006279");
        xmlRecordEntity.setOwningInst("NYPL");
        xmlRecordEntity.setDataLoaded(new Date());
        URL resource = getClass().getResource(xmlFileName);
        assertNotNull(resource);
        File file = new File(resource.toURI());
        String content = FileUtils.readFileToString(file, "UTF-8");
        xmlRecordEntity.setXml(content.getBytes());
        xmlRecordRepository.save(xmlRecordEntity);

        etlDataLoadProcessor.setFileName(xmlFileName);
        etlDataLoadProcessor.setBatchSize(10);
        etlDataLoadProcessor.setRecordProcessor(recordProcessor);
        etlDataLoadProcessor.setXmlRecordRepository(xmlRecordRepository);
        etlDataLoadProcessor.setBibliographicDetailsRepository(bibliographicDetailsRepository);
        etlDataLoadProcessor.setHoldingsDetailsRepository(holdingsDetailsRepository);
        etlDataLoadProcessor.setItemDetailsRepository(itemDetailsRepository);
        etlDataLoadProcessor.setProducer(producer);
        etlDataLoadProcessor.setInstitutionName("NYPL");
        assertNotNull(etlDataLoadProcessor.getXmlRecordRepository());
        assertEquals(etlDataLoadProcessor.getBatchSize(), new Integer(10));
        assertEquals(etlDataLoadProcessor.getRecordProcessor(), recordProcessor);
        assertEquals(etlDataLoadProcessor.getFileName(), xmlFileName);
        etlDataLoadProcessor.startLoadProcess();
        assertEquals(recordProcessor.getXmlFileName(),xmlFileName);

        BibliographicEntity bibliographicEntity = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(3,xmlRecordEntity.getOwningInstBibId());
        assertNotNull(bibliographicEntity);
    }

    @Test
    public void testFailureReportEntity() throws Exception {

        assertNotNull(etlDataLoadProcessor);
        assertNotNull(recordProcessor);
        assertNotNull(xmlRecordRepository);
        assertNotNull(bibliographicDetailsRepository);
        assertNotNull(holdingsDetailsRepository);
        assertNotNull(itemDetailsRepository);
        assertNotNull(producer);

        XmlRecordEntity xmlRecordEntity = new XmlRecordEntity();
        String xmlFileName = "InvalidRecordForEtlLoadTest.xml";
        xmlRecordEntity.setXmlFileName(xmlFileName);
        xmlRecordEntity.setOwningInstBibId(".b100006279");
        xmlRecordEntity.setOwningInst("NYPL");
        xmlRecordEntity.setDataLoaded(new Date());
        URL resource = getClass().getResource(xmlFileName);
        assertNotNull(resource);
        File file = new File(resource.toURI());
        String content = FileUtils.readFileToString(file, "UTF-8");
        xmlRecordEntity.setXml(content.getBytes());
        xmlRecordRepository.save(xmlRecordEntity);

        etlDataLoadProcessor.setFileName(xmlFileName);
        etlDataLoadProcessor.setBatchSize(10);
        etlDataLoadProcessor.setRecordProcessor(recordProcessor);
        etlDataLoadProcessor.setXmlRecordRepository(xmlRecordRepository);
        etlDataLoadProcessor.setBibliographicDetailsRepository(bibliographicDetailsRepository);
        etlDataLoadProcessor.setHoldingsDetailsRepository(holdingsDetailsRepository);
        etlDataLoadProcessor.setItemDetailsRepository(itemDetailsRepository);
        etlDataLoadProcessor.setProducer(producer);
        etlDataLoadProcessor.setInstitutionName("NYPL");
        assertNotNull(etlDataLoadProcessor.getXmlRecordRepository());
        assertEquals(etlDataLoadProcessor.getBatchSize(), new Integer(10));
        assertEquals(etlDataLoadProcessor.getRecordProcessor(), recordProcessor);
        assertEquals(etlDataLoadProcessor.getFileName(), xmlFileName);
        etlDataLoadProcessor.startLoadProcess();
        assertEquals(recordProcessor.getXmlFileName(),xmlFileName);

    }

}