package org.recap.repository;

import org.recap.model.jpa.XmlRecordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

/**
 * Created by peris on 7/17/16.
 */
public interface XmlRecordRepository extends PagingAndSortingRepository<XmlRecordEntity, Integer> {

    /**
     * Find by xml file name page.
     *
     * @param pageable    the pageable
     * @param xmlFileName the xml file name
     * @return the page
     */
    Page<XmlRecordEntity> findByXmlFileName(Pageable pageable, String xmlFileName);

    /**
     * Find by xml file name containing page.
     *
     * @param pageable    the pageable
     * @param xmlFileName the xml file name
     * @return the page
     */
    Page<XmlRecordEntity> findByXmlFileNameContaining(Pageable pageable, String xmlFileName);

    /**
     * Count by xml file name long.
     *
     * @param xmlFileName the xml file name
     * @return the long
     */
    Long countByXmlFileName(String xmlFileName);

    /**
     * Count by xml file name containing long.
     *
     * @param xmlFileName the xml file name
     * @return the long
     */
    Long countByXmlFileNameContaining(String xmlFileName);

    /**
     * Find by id xml record entity.
     *
     * @param id the id
     * @return the xml record entity
     */
    Optional<XmlRecordEntity> findById(Integer id);

    /**
     * Find distinct file names list.
     *
     * @return the list
     */
    @Query(value = "select distinct (xml_file) from xml_records_t",  nativeQuery = true)
    List findDistinctFileNames();

    /**
     * Find inst id by file names integer.
     *
     * @param fileName the file name
     * @return the integer
     */
    @Query(value = "select institution_id  from institution_t where institution_code in (select owning_inst from xml_records_t where xml_file = ?1) limit 1",  nativeQuery = true)
    Integer findInstIdByFileNames(String fileName);
}
