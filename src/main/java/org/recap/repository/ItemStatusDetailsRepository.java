package org.recap.repository;

import org.recap.model.jpa.ItemStatusEntity;
import org.recap.repository.jpa.BaseRepository;

/**
 * Created by pvsubrah on 6/27/16.
 */
public interface ItemStatusDetailsRepository extends BaseRepository<ItemStatusEntity> {

    /**
     * Find by status code item status entity.
     *
     * @param statusCode the status code
     * @return the item status entity
     */
    ItemStatusEntity findByStatusCode(String statusCode);

}
