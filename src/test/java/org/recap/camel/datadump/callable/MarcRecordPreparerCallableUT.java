package org.recap.camel.datadump.callable;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.service.formatter.datadump.MarcXmlFormatterService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MarcRecordPreparerCallableUT extends BaseTestCaseUT {

    @Mock
    MarcXmlFormatterService marcXmlFormatterService;// = new DeletedJsonFormatterService();
    @Mock
    BibliographicEntity bibliographicEntity;

    List<BibliographicEntity> bibliographicEntities;

    @Test
    public void testCall() {
        MarcRecordPreparerCallable deletedRecordPreparerCallable = new MarcRecordPreparerCallable(bibliographicEntities, marcXmlFormatterService);
        Map<String, Object> map = marcXmlFormatterService.prepareMarcRecords(bibliographicEntities);
        assertTrue(true);

    }
}
