package org.recap.camel.datadump.callable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.service.formatter.datadump.SCSBXmlFormatterService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BibRecordPreparerCallableUT extends BaseTestCaseUT {
    @Mock
    BibRecordPreparerCallable bibRecordPreparerCallable;

    @Mock
    BibliographicEntity bibliographicEntity;

    @Mock
    SCSBXmlFormatterService scsbXmlFormatterService;

    @BeforeEach
    public void before() {
        bibRecordPreparerCallable = Mockito.mock(BibRecordPreparerCallable.class);
        scsbXmlFormatterService = Mockito.mock(SCSBXmlFormatterService.class);
        bibliographicEntity = Mockito.mock(BibliographicEntity.class);
    }

    @Test
    public void testbibRecordPreparerCallable() throws Exception {
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        bibliographicEntities.add(bibliographicEntity);
        bibRecordPreparerCallable = new BibRecordPreparerCallable(bibliographicEntities, scsbXmlFormatterService);
        assertTrue(true);
        assertNotNull(bibRecordPreparerCallable);
        Map<String, Object> obj = new HashMap<>();
        obj = bibRecordPreparerCallable.call();
        assertNotNull(obj);
    }
}
