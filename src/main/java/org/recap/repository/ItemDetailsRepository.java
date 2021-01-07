package org.recap.repository;

import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by chenchulakshmig on 21/6/16.
 */
public interface ItemDetailsRepository extends BaseRepository<ItemEntity> {

    
    /**
     * Count by owning institution id long.
     *
     * @param owningInstitutionId the owning institution id
     * @return the long
     */
    Long countByOwningInstitutionId(Integer owningInstitutionId);

    /**
     * Find by owning institution id page.
     *
     * @param pageable            the pageable
     * @param owningInstitutionId the owning institution id
     * @return the page
     */
    Page<ItemEntity> findByOwningInstitutionId(Pageable pageable, Integer owningInstitutionId);

    /**
     * Find by owning institution id list.
     *
     * @param owningInstitutionId the owning institution id
     * @return the list
     */
    List<ItemEntity> findByOwningInstitutionId(Integer owningInstitutionId);

    /**
     * Find by owning institution item id item entity.
     *
     * @param owningInstitutionItemId the owning institution item id
     * @return the item entity
     */
    ItemEntity findByOwningInstitutionItemId(String owningInstitutionItemId);

    /**
     * Find count of bibliographic items long.
     *
     * @return the long
     */
    @Query(value = "select count(*) from bibliographic_item_t",  nativeQuery = true)
    Long findCountOfBibliographicItems();

    /**
     * Find count of bibliographic items by inst id long.
     *
     * @param instId the inst id
     * @return the long
     */
    @Query(value = "select count(*) from bibliographic_item_t where bib_inst_id = ?1",  nativeQuery = true)
    Long findCountOfBibliographicItemsByInstId(Integer instId);


    /**
     * Find itementity for the given barcode .
     *
     * @param barcode the barcodes
     * @return the list
     */
    List<ItemEntity> findByBarcode(String barcode);

}
