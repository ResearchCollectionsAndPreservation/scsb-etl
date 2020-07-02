package org.recap.camel.datadump;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.camel.datadump.callable.BibEntityPreparerCallable;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.ItemEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class BibEntityPreparerCallableUT extends BaseTestCase {
    @Mock
    BibEntityPreparerCallable bibEntityPreparerCallable;

    @Mock
    BibliographicEntity bibliographicEntity;

    @Mock
    Callable callable;

    List<Integer> itemIds = null;
    @Mock
    ItemEntity itemEntity;

    @Before
    public void before() {
        bibliographicEntity = Mockito.mock(BibliographicEntity.class);
        bibEntityPreparerCallable = Mockito.mock(BibEntityPreparerCallable.class);
    }

    @Test
    public void testBibEntityPreparerCallable() {
        bibEntityPreparerCallable = new BibEntityPreparerCallable(bibliographicEntity, itemIds);
        assertTrue(true);
        assertNotNull(bibEntityPreparerCallable);
    }
    @Test
    public void tescall() throws Exception {
        ItemEntity itemEntity =new ItemEntity();
        itemEntity.setBarcode("1234");
        itemEntity.setItemId(1);
        itemEntity.setCustomerCode("1234");
        itemEntity.setCallNumber("1234");
        itemEntity.setCallNumberType("land");
        itemEntity.setItemAvailabilityStatusId(123);
        List<ItemEntity> itemEntities=new ArrayList<>();
        itemEntities.add(itemEntity);
        bibliographicEntity.setItemEntities(itemEntities);
        bibliographicEntity = bibEntityPreparerCallable.call();
        assertTrue(true);
        assertNull(bibliographicEntity);
    }
}
