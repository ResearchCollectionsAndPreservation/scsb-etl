package org.recap.repository;

import org.recap.model.jpa.ImsLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ImsLocationDetailsRepository extends JpaRepository<ImsLocationEntity, Integer> {
    ImsLocationEntity findByImsLocationCode(String imsLocationCode);

    @Query(value = "select ims_location_code from ims_location_t",nativeQuery = true)
    List<String> findAllImsLocationCode();
}