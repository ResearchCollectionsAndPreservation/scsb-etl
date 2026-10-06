package org.recap.camel;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.XmlRecordEntity;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.repository.HoldingsDetailsRepository;
import org.recap.repository.ItemDetailsRepository;
import org.recap.repository.XmlRecordRepository;
import org.springframework.data.domain.Page;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by angelind on 27/7/16.
 */

public class EtlDataLoadProcessorUT extends  BaseTestCaseUT {

    @Mock
    XmlRecordRepository xmlRecordRepository;

    @InjectMocks
    EtlDataLoadProcessor etlDataLoadProcessor;

    @Mock
    RecordProcessor recordProcessor;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    HoldingsDetailsRepository holdingsDetailsRepository;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    ProducerTemplate producer;

    @Test
    public void testStartLoadProcessWithXmlFileName() throws Exception {
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
        List distinctFileNames=new ArrayList();
        distinctFileNames.add(xmlFileName);
        Mockito.when(xmlRecordRepository.findDistinctFileNames()).thenReturn(distinctFileNames);
        Mockito.when(xmlRecordRepository.countByXmlFileName(Mockito.anyString())).thenReturn(1l);
        Page<XmlRecordEntity> xmlRecordEntities = Mockito.mock(Page.class);
        Mockito.when(xmlRecordRepository.findByXmlFileName(Mockito.any(),Mockito.anyString())).thenReturn(xmlRecordEntities);
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
        assertEquals(etlDataLoadProcessor.getBatchSize(), Integer.valueOf(10));
        assertEquals(etlDataLoadProcessor.getRecordProcessor(), recordProcessor);
        assertEquals(etlDataLoadProcessor.getFileName(), xmlFileName);
        etlDataLoadProcessor.startLoadProcess();
    }

    @Test
    public void testFailureReportEntity() throws Exception {

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

        List distinctFileNames=new ArrayList();
        distinctFileNames.add(xmlFileName);
        Mockito.when(xmlRecordRepository.findDistinctFileNames()).thenReturn(distinctFileNames);
        Mockito.when(xmlRecordRepository.countByXmlFileName(Mockito.anyString())).thenReturn(1l);
        Page<XmlRecordEntity> xmlRecordEntities = Mockito.mock(Page.class);
        Mockito.when(xmlRecordRepository.findByXmlFileName(Mockito.any(),Mockito.anyString())).thenReturn(xmlRecordEntities);


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
        assertEquals(etlDataLoadProcessor.getBatchSize(), Integer.valueOf(10));
        assertEquals(etlDataLoadProcessor.getRecordProcessor(), recordProcessor);
        assertEquals(etlDataLoadProcessor.getFileName(), xmlFileName);
        etlDataLoadProcessor.startLoadProcess();

    }

}