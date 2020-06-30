package org.recap.camel.datadump.callable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.service.formatter.datadump.SCSBXmlFormatterService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BibRecordPreparerCallableUT extends BaseTestCase {
    @Mock
    BibRecordPreparerCallable bibRecordPreparerCallable;

    @Mock
    BibliographicEntity bibliographicEntity;

    @Mock
    SCSBXmlFormatterService scsbXmlFormatterService;

    @Before
    public void before() {
        bibRecordPreparerCallable = Mockito.mock(BibRecordPreparerCallable.class);
        scsbXmlFormatterService = Mockito.mock(SCSBXmlFormatterService.class);
        bibliographicEntity = Mockito.mock(BibliographicEntity.class);
    }

    @Test
    public void testbibRecordPreparerCallable() throws Exception {
        List<BibliographicEntity> bibliographicEntities= new ArrayList<>();
        bibliographicEntities.add(bibliographicEntity);
        bibRecordPreparerCallable = new BibRecordPreparerCallable(bibliographicEntities, scsbXmlFormatterService);
        assertTrue(true);
        assertNotNull(bibRecordPreparerCallable);
        Map<String, Object> obj = new HashMap<>();
        obj = bibRecordPreparerCallable.call();
        assertNotNull(obj);
    }
}
