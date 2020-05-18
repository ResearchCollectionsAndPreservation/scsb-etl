package org.recap.repository;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.CollectionGroupEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by chenchulakshmig on 14/7/16.
 */
public class CollectionGroupDetailsRepositoryUT extends BaseTestCase {

    @Autowired
    CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Test
    public void saveAndFind() throws Exception {
        assertNotNull(collectionGroupDetailsRepository);

        CollectionGroupEntity collectionGroupEntity = new CollectionGroupEntity();
        collectionGroupEntity.setCollectionGroupCode("test");
        collectionGroupEntity.setCollectionGroupDescription("test");
        Date date = new Date();
        collectionGroupEntity.setCreatedDate(date);
        collectionGroupEntity.setLastUpdatedDate(date);

        CollectionGroupEntity savedCollectionGroupEntity = collectionGroupDetailsRepository.save(collectionGroupEntity);
        assertNotNull(savedCollectionGroupEntity);
        assertNotNull(savedCollectionGroupEntity.getCollectionGroupId());
        assertEquals(savedCollectionGroupEntity.getCollectionGroupCode(), "test");
        assertEquals(savedCollectionGroupEntity.getCollectionGroupDescription(), "test");
        assertEquals(savedCollectionGroupEntity.getCreatedDate(), date);
        assertEquals(savedCollectionGroupEntity.getLastUpdatedDate(), date);

        CollectionGroupEntity byCollectionGroupCode = collectionGroupDetailsRepository.findByCollectionGroupCode("test");
        assertNotNull(byCollectionGroupCode);
    }

    @Test
    public void update() throws Exception {
        assertNotNull(collectionGroupDetailsRepository);

        CollectionGroupEntity collectionGroupEntity = new CollectionGroupEntity();
        collectionGroupEntity.setCollectionGroupId(1);
        collectionGroupEntity.setCollectionGroupCode("Shared");
        collectionGroupEntity.setCollectionGroupDescription("Shared");
        collectionGroupEntity.setCreatedDate(new Date());
        collectionGroupEntity.setLastUpdatedDate(new Date());

        collectionGroupDetailsRepository.save(collectionGroupEntity);

        Optional<CollectionGroupEntity> savedCollectionGroupEntity = collectionGroupDetailsRepository.findById(1);
        assertEquals(savedCollectionGroupEntity.get().getCreatedDate(), collectionGroupEntity.getCreatedDate());
        assertEquals(savedCollectionGroupEntity.get().getLastUpdatedDate(), collectionGroupEntity.getLastUpdatedDate());
    }

}