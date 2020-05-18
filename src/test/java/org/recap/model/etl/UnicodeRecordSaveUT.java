package org.recap.model.etl;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.XmlRecordEntity;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.repository.XmlRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by rajeshbabuk on 20/7/16.
 */
public class UnicodeRecordSaveUT extends BaseTestCase {

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    XmlRecordRepository xmlRecordRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void saveUnicodeRecordBibTable() throws Exception {
        Random random = new Random();
        File bibContentFile = getUnicodeContentFile("UnicodeRecord.xml");
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent(sourceBibContent.getBytes("UTF-8"));
        bibliographicEntity.setOwningInstitutionId(3);
        String owningInstitutionBibId = String.valueOf(random.nextInt());
        bibliographicEntity.setOwningInstitutionBibId(owningInstitutionBibId);
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setLastUpdatedBy("tst");

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity);

        BibliographicEntity fetchedBibliographicEntity = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(3, owningInstitutionBibId);
        assertNotNull(fetchedBibliographicEntity);
        assertNotNull(fetchedBibliographicEntity.getContent());

        String fetchedBibContent = new String(fetchedBibliographicEntity.getContent(), Charset.forName("UTF-8"));
        assertEquals(sourceBibContent, fetchedBibContent);
    }

    @Test
    public void saveUnicodeRecordXmlTable() throws Exception {
        String fileName = "NYPL-UnicodeRecord.xml";
        File bibContentFile = getUnicodeContentFile(fileName);
        String sourceBibXml = FileUtils.readFileToString(bibContentFile, "UTF-8");

        XmlRecordEntity xmlRecordEntity = new XmlRecordEntity();
        xmlRecordEntity.setXml(sourceBibXml.getBytes("UTF-8"));
        xmlRecordEntity.setXmlFileName(fileName);
        xmlRecordEntity.setOwningInst("NYPL");
        xmlRecordEntity.setOwningInstBibId(".b160150577123");
        xmlRecordEntity.setDataLoaded(new Date());

        XmlRecordEntity savedXmlRecordEntity = xmlRecordRepository.save(xmlRecordEntity);
        entityManager.refresh(savedXmlRecordEntity);
        assertNotNull(savedXmlRecordEntity);

        Optional<XmlRecordEntity> fetchedXmlRecordEntity = xmlRecordRepository.findById(savedXmlRecordEntity.getId());
        assertNotNull(fetchedXmlRecordEntity.get());
        assertNotNull(fetchedXmlRecordEntity.get().getXml());

        String fetchedBibXml = new String(fetchedXmlRecordEntity.get().getXml(), Charset.forName("UTF-8"));
        assertEquals(sourceBibXml, fetchedBibXml);
    }

    public File getUnicodeContentFile(String fileName) throws URISyntaxException {
        URL resource = getClass().getResource(fileName);
        return new File(resource.toURI());
    }

}
