package org.recap.camel.datadump.callable;

import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.ItemEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by peris on 11/3/16.
 */
public class BibEntityPreparerCallable implements Callable {

    private final List<Integer> itemIds;
    /**
     * The Bibliographic entity.
     */
    BibliographicEntity bibliographicEntity;

    /**
     * Instantiates a new Bib entity preparer callable which is used for multithreading.
     *
     * @param bibliographicEntity the bibliographic entity
     * @param itemIds             the item ids
     */
    public BibEntityPreparerCallable(BibliographicEntity bibliographicEntity, List<Integer> itemIds) {
        this.bibliographicEntity = bibliographicEntity;
        this.itemIds = itemIds;
    }

    /**
     * This method is processed by thread to build bibliographic entity with the item entities that are part of the passed item ids.
     *
     * @return BibliographicEntity
     * @throws Exception
     */
    @Override
    public BibliographicEntity call() throws Exception {
        List<ItemEntity> itemEntities =  bibliographicEntity.getItemEntities();
        List<ItemEntity> filteredItems = new ArrayList<>();
        for (Iterator<ItemEntity> itemEntityIterator = itemEntities.iterator(); itemEntityIterator.hasNext(); ) {
            ItemEntity itemEntity = itemEntityIterator.next();
            if(itemIds.contains(itemEntity.getId())){
                filteredItems.add(itemEntity);
            }
        }
        bibliographicEntity.setItemEntities(filteredItems);
        return bibliographicEntity;
    }

}
