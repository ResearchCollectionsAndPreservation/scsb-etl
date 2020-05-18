package org.recap.repository;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.ItemStatusEntity;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by chenchulakshmig on 14/7/16.
 */
public class ItemStatusDetailsRepositoryUT extends BaseTestCase {

    @Autowired
    ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Test
    public void saveAndFind() throws Exception {
        assertNotNull(itemStatusDetailsRepository);

        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setStatusCode("test");
        itemStatusEntity.setStatusDescription("test");

        ItemStatusEntity savedItemStatusEntity = itemStatusDetailsRepository.save(itemStatusEntity);
        assertNotNull(savedItemStatusEntity);
        assertNotNull(savedItemStatusEntity.getItemStatusId());
        assertEquals(itemStatusEntity.getStatusCode(), "test");
        assertEquals(itemStatusEntity.getStatusDescription(), "test");

        ItemStatusEntity byStatusCode = itemStatusDetailsRepository.findByStatusCode("test");
        assertNotNull(byStatusCode);
    }

    @Test
    public void update() throws Exception {
        assertNotNull(itemStatusDetailsRepository);

        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setItemStatusId(1);
        itemStatusEntity.setStatusCode("Available");
        itemStatusEntity.setStatusDescription("Test Available");

        itemStatusDetailsRepository.save(itemStatusEntity);

        Optional<ItemStatusEntity> savedItemStatusEntity = itemStatusDetailsRepository.findById(1);
        assertEquals(savedItemStatusEntity.get().getStatusDescription(), itemStatusEntity.getStatusDescription());
    }

}