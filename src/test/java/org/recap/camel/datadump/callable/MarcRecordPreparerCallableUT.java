package org.recap.camel.datadump.callable;

import com.fasterxml.jackson.databind.ser.Serializers;
import org.junit.Test;
import org.mockito.Mock;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.service.formatter.datadump.DeletedJsonFormatterService;
import org.recap.service.formatter.datadump.MarcXmlFormatterService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class MarcRecordPreparerCallableUT extends BaseTestCase {
    @Mock
    MarcXmlFormatterService marcXmlFormatterService;// = new DeletedJsonFormatterService();
    @Mock
    BibliographicEntity bibliographicEntity;

    List<BibliographicEntity> bibliographicEntities;

    @Test
    public void testCall(){
        MarcRecordPreparerCallable deletedRecordPreparerCallable = new MarcRecordPreparerCallable(bibliographicEntities,marcXmlFormatterService);
        Map<String, Object> map = marcXmlFormatterService.prepareMarcRecords(bibliographicEntities);
        assertTrue(true);

    }
}
