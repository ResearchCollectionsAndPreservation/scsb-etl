package org.recap.repository;

import org.recap.model.jpa.EtlGfaEntity;
import org.recap.repository.jpa.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hemalathas on 21/7/16.
 */
public interface EtlGfaRepository extends JpaRepository<EtlGfaEntity,Integer>{

    /**
     * Find bystatus list.
     *
     * @param status the status
     * @return the list
     */
    List<EtlGfaEntity> findBystatus(String status);

    /**
     * Find by item barcode list.
     *
     * @param itemBarcode the item barcode
     * @return the list
     */
    List<EtlGfaEntity> findByItemBarcode(String itemBarcode);


}
