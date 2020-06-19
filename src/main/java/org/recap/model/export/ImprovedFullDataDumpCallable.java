package org.recap.model.export;

import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.BibliographicDetailsRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by peris on 10/26/16.
 */
@Service
@Scope("prototype")
public class ImprovedFullDataDumpCallable implements Callable {

    /**
     * The Data dump search results.
     */
    List<LinkedHashMap> dataDumpSearchResults;

    /**
     * The Bibliographic details repository.
     */
    BibliographicDetailsRepository bibliographicDetailsRepository;

    /**
     * Instantiates a new Improved full data dump callable.
     *
     * @param dataDumpSearchResults          the data dump search results
     * @param bibliographicDetailsRepository the bibliographic details repository
     */
    public ImprovedFullDataDumpCallable(List<LinkedHashMap> dataDumpSearchResults, BibliographicDetailsRepository bibliographicDetailsRepository) {
        this.dataDumpSearchResults = dataDumpSearchResults;
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;

    }

    @Override
    public Object call() throws Exception {

        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();

        for (Iterator<LinkedHashMap> iterator = dataDumpSearchResults.iterator(); iterator.hasNext(); ) {
            LinkedHashMap linkedHashMap = iterator.next();
            Integer bibId = (Integer) linkedHashMap.get("bibId");
            List<Integer> itemIds = (List<Integer>) linkedHashMap.get("itemIds");
            List<BibliographicEntity> bibliographicEntityList = bibliographicDetailsRepository.getBibliographicEntityList(Arrays.asList(bibId));
            BibliographicEntity bibliographicEntity = bibliographicEntityList.get(0);
            bibliographicEntity.getItemEntities();
            for (Iterator<BibliographicEntity> bibliographicEntityIterator = bibliographicEntityList.iterator(); bibliographicEntityIterator.hasNext(); ) {
                BibliographicEntity retrievedBibEntity =  bibliographicEntityIterator.next();
                List<ItemEntity> itemEntities = retrievedBibEntity.getItemEntities();

                List<ItemEntity> filteredItems = new ArrayList<>();
                for (Iterator<ItemEntity> itemEntityIterator = itemEntities.iterator(); itemEntityIterator.hasNext(); ) {
                    ItemEntity itemEntity = itemEntityIterator.next();
                    if(itemIds.contains(itemEntity.getItemId())){
                        filteredItems.add(itemEntity);
                    }
                }

                bibliographicEntity.setItemEntities(filteredItems);
                bibliographicEntities.add(bibliographicEntity);
            }
        }

        return bibliographicEntities;
    }
}
