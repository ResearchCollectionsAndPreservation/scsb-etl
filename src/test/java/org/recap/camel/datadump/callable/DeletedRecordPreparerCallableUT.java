package org.recap.camel.datadump.callable;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.service.formatter.datadump.DeletedJsonFormatterService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeletedRecordPreparerCallableUT extends BaseTestCaseUT {
    @Mock
    DeletedJsonFormatterService deletedJsonFormatterService;// = new DeletedJsonFormatterService();

    BibliographicEntity bibliographicEntity;

    List<BibliographicEntity> bibliographicEntities;
    DeletedRecordPreparerCallable deletedRecordPreparerCallable = new DeletedRecordPreparerCallable(bibliographicEntities, deletedJsonFormatterService);

    @Test
    public void testCall() {
        Map<String, Object> map = deletedJsonFormatterService.prepareDeletedRecords(bibliographicEntities);
        assertTrue(true);

    }
}
