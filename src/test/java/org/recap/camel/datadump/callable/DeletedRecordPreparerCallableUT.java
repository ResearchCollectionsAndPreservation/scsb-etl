package org.recap.camel.datadump.callable;

import org.junit.Test;
import org.mockito.Mock;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.service.formatter.datadump.DeletedJsonFormatterService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.MapKeyColumn;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class DeletedRecordPreparerCallableUT extends BaseTestCase {
    @Mock
    DeletedJsonFormatterService deletedJsonFormatterService;// = new DeletedJsonFormatterService();

    BibliographicEntity bibliographicEntity;

    List<BibliographicEntity> bibliographicEntities;
    DeletedRecordPreparerCallable deletedRecordPreparerCallable = new DeletedRecordPreparerCallable(bibliographicEntities,deletedJsonFormatterService);
    @Test
    public void testCall(){
        Map<String, Object> map = deletedJsonFormatterService.prepareDeletedRecords(bibliographicEntities);
        assertTrue(true);

    }
}
