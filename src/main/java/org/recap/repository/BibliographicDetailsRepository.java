package org.recap.repository;

import org.recap.model.jpa.BibliographicEntity;
import org.recap.repository.jpa.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by pvsubrah on 6/10/16.
 */
public interface BibliographicDetailsRepository extends BaseRepository<BibliographicEntity> {

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
    Page<BibliographicEntity> findByOwningInstitutionId(Pageable pageable, Integer owningInstitutionId);

    /**
     * Find by owning institution id and owning institution bib id bibliographic entity.
     *
     * @param owningInstitutionId    the owning institution id
     * @param owningInstitutionBibId the owning institution bib id
     * @return the bibliographic entity
     */
    BibliographicEntity findByOwningInstitutionIdAndOwningInstitutionBibId(Integer owningInstitutionId, String owningInstitutionBibId);

    /**
     * Find by owning institution bib id list.
     *
     * @param owningInstitutionBibId the owning institution bib id
     * @return the list
     */
    List<BibliographicEntity> findByOwningInstitutionBibId(String owningInstitutionBibId);

    /**
     * Gets bibliographic entity list.
     *
     * @param bibIds the bib ids
     * @return the bibliographic entity list
     */
    @Query(value="SELECT BIB FROM BibliographicEntity as BIB WHERE BIB.id IN (?1)")
    List<BibliographicEntity> getBibliographicEntityList(Collection<Integer> bibIds);


    /**
     * Find count of bibliographic holdings long.
     *
     * @return the long
     */
    @Query(value = "select count(bibliographic_id) from bibliographic_holdings_t",  nativeQuery = true)
    Long findCountOfBibliographicHoldings();

    /**
     * Find count of bibliographic holdings by inst id long.
     *
     * @param instId the inst id
     * @return the long
     */
    @Query(value = "select count(bibliographic_holdings_t.bibliographic_id) from bibliographic_holdings_t,bibliographic_t where bibliographic_t.bibliographic_id=bibliographic_holdings_t.bibliographic_id and owning_inst_id = ?1",  nativeQuery = true)
    Long findCountOfBibliographicHoldingsByInstId(Integer instId);

    /**
     * Count records for full dump long.
     *
     * @param cgIds            the cg ids
     * @param institutionCodes the institution codes
     * @return the long
     */
    @Query(value="SELECT COUNT(BIB) FROM BibliographicEntity AS BIB INNER JOIN BIB.institutionEntity AS INST WHERE INST.institutionCode IN (?2) AND " +
            "BIB.owningInstitutionBibId IN (SELECT DISTINCT BIB1.owningInstitutionBibId FROM BibliographicEntity as BIB1 INNER JOIN BIB1.institutionEntity AS INST1 " +
            "INNER JOIN BIB1.holdingsEntities AS HOLDING INNER JOIN HOLDING.itemEntities AS ITEMS WHERE ITEMS.collectionGroupId IN (?1) AND INST1.institutionCode IN (?2) " +
            "AND ITEMS.isDeleted = 0)")
    Long countRecordsForFullDump(Collection<Integer> cgIds, Collection<String> institutionCodes);

    /**
     * Count deleted records for full dump long.
     *
     * @param cgIds            the cg ids
     * @param institutionCodes the institution codes
     * @return the long
     */
    @Query(value="SELECT COUNT(BIB) FROM BibliographicEntity AS BIB INNER JOIN BIB.institutionEntity AS INST WHERE INST.institutionCode IN (?2) AND " +
            "BIB.owningInstitutionBibId IN (SELECT DISTINCT BIB1.owningInstitutionBibId FROM BibliographicEntity as BIB1 INNER JOIN BIB1.institutionEntity AS INST1 " +
            "INNER JOIN BIB1.holdingsEntities AS HOLDING INNER JOIN HOLDING.itemEntities AS ITEMS WHERE ITEMS.collectionGroupId IN (?1) AND INST1.institutionCode IN (?2) " +
            "AND ITEMS.isDeleted != 0)")
    Long countDeletedRecordsForFullDump(Collection<Integer> cgIds, Collection<String> institutionCodes);

    /**
     * Count records for incremental dump long.
     *
     * @param cgIds           the cg ids
     * @param institutionIds  the institution ids
     * @param lastUpdatedDate the last updated date
     * @return the long
     */
    @Query(value="SELECT COUNT(BIB) FROM BibliographicEntity as BIB INNER JOIN BIB.institutionEntity AS INST WHERE INST.institutionCode IN (?2) AND  " +
            "BIB.owningInstitutionBibId IN (SELECT DISTINCT BIB1.owningInstitutionBibId FROM BibliographicEntity as BIB1 INNER JOIN BIB1.institutionEntity AS INST1 " +
            "INNER JOIN BIB1.holdingsEntities AS HOLDINGS INNER JOIN BIB1.itemEntities AS ITEMS WHERE ITEMS.collectionGroupId IN (?1) AND INST1.institutionCode IN (?2) AND (BIB1.lastUpdatedDate >= (?3) " +
            "OR HOLDINGS.lastUpdatedDate >= (?3) OR ITEMS.lastUpdatedDate >= (?3)) AND ITEMS.isDeleted = 0)")
    Long countRecordsForIncrementalDump(Collection<Integer> cgIds, Collection<String> institutionIds, Date lastUpdatedDate);

    /**
     * Count deleted records for incremental long.
     *
     * @param cgIds           the cg ids
     * @param institutionIds  the institution ids
     * @param lastUpdatedDate the last updated date
     * @return the long
     */
    @Query(value="SELECT COUNT(BIB) FROM BibliographicEntity as BIB INNER JOIN BIB.institutionEntity AS INST WHERE INST.institutionCode IN (?2) AND  " +
            "BIB.owningInstitutionBibId IN (SELECT DISTINCT BIB1.owningInstitutionBibId FROM BibliographicEntity as BIB1 INNER JOIN BIB1.institutionEntity AS INST1 " +
            "INNER JOIN BIB1.holdingsEntities AS HOLDINGS INNER JOIN BIB1.itemEntities AS ITEMS WHERE ITEMS.collectionGroupId IN (?1) AND INST1.institutionCode IN (?2) AND (BIB1.lastUpdatedDate >= (?3) " +
            "OR HOLDINGS.lastUpdatedDate >= (?3) OR ITEMS.lastUpdatedDate >= (?3)) AND ITEMS.isDeleted != 0)")
    Long countDeletedRecordsForIncremental(Collection<Integer> cgIds, Collection<String> institutionIds, Date lastUpdatedDate);

    /**
     * Gets records for full dump.
     *
     * @param pageable         the pageable
     * @param cgIds            the cg ids
     * @param institutionCodes the institution codes
     * @return the records for full dump
     */
    @Query(value="SELECT BIB FROM BibliographicEntity as BIB INNER JOIN BIB.institutionEntity AS INST WHERE INST.institutionCode IN (?2) AND " +
            "BIB.owningInstitutionBibId IN (SELECT DISTINCT BIB1.owningInstitutionBibId FROM BibliographicEntity as BIB1 INNER JOIN BIB1.institutionEntity AS INST1 " +
            "INNER JOIN BIB1.itemEntities AS ITEMS WHERE ITEMS.collectionGroupId IN (?1) AND INST1.institutionCode IN (?2) AND ITEMS.isDeleted = 0)")
    Page<BibliographicEntity> getRecordsForFullDump(Pageable pageable, Collection<Integer> cgIds, Collection<String> institutionCodes);

    /**
     * Gets deleted records for full dump.
     *
     * @param pageable         the pageable
     * @param cgIds            the cg ids
     * @param institutionCodes the institution codes
     * @return the deleted records for full dump
     */
    @Query(value="SELECT BIB FROM BibliographicEntity as BIB INNER JOIN BIB.institutionEntity AS INST WHERE INST.institutionCode IN (?2) AND " +
            "BIB.owningInstitutionBibId IN (SELECT DISTINCT BIB1.owningInstitutionBibId FROM BibliographicEntity as BIB1 INNER JOIN BIB1.institutionEntity AS INST1 " +
            "INNER JOIN BIB1.itemEntities AS ITEMS WHERE ITEMS.collectionGroupId IN (?1) AND INST1.institutionCode IN (?2) AND ITEMS.isDeleted != 0)")
    Page<BibliographicEntity> getDeletedRecordsForFullDump(Pageable pageable, Collection<Integer> cgIds, Collection<String> institutionCodes);

    /**
     * Gets records for incremental dump.
     *
     * @param pageable         the pageable
     * @param cgIds            the cg ids
     * @param institutionCodes the institution codes
     * @param lastUpdatedDate  the last updated date
     * @return the records for incremental dump
     */
    @Query(value="SELECT BIB FROM BibliographicEntity as BIB  INNER JOIN BIB.institutionEntity AS INST WHERE INST.institutionCode IN (?2) AND  " +
            "BIB.owningInstitutionBibId IN (SELECT DISTINCT BIB1.owningInstitutionBibId FROM BibliographicEntity as BIB1 INNER JOIN BIB1.institutionEntity AS INST1 " +
            "INNER JOIN BIB1.holdingsEntities AS HOLDINGS INNER JOIN BIB1.itemEntities AS ITEMS WHERE ITEMS.collectionGroupId IN (?1) AND INST1.institutionCode IN (?2) AND (BIB1.lastUpdatedDate >= ?3 " +
            "OR HOLDINGS.lastUpdatedDate >= (?3) OR ITEMS.lastUpdatedDate >= (?3)) AND ITEMS.isDeleted = 0)")
    Page<BibliographicEntity> getRecordsForIncrementalDump(Pageable pageable, Collection<Integer> cgIds, Collection<String> institutionCodes, Date lastUpdatedDate);

    /**
     * Gets deleted records for incremental dump.
     *
     * @param pageable         the pageable
     * @param cgIds            the cg ids
     * @param institutionCodes the institution codes
     * @param lastUpdatedDate  the last updated date
     * @return the deleted records for incremental dump
     */
    @Query(value="SELECT BIB FROM BibliographicEntity as BIB  INNER JOIN BIB.institutionEntity AS INST WHERE INST.institutionCode IN (?2) AND  " +
            "BIB.owningInstitutionBibId IN (SELECT DISTINCT BIB1.owningInstitutionBibId FROM BibliographicEntity as BIB1 INNER JOIN BIB1.institutionEntity AS INST1 " +
            "INNER JOIN BIB1.holdingsEntities AS HOLDINGS INNER JOIN BIB1.itemEntities AS ITEMS WHERE ITEMS.collectionGroupId IN (?1) AND INST1.institutionCode IN (?2) AND (BIB1.lastUpdatedDate >= ?3 " +
            "OR HOLDINGS.lastUpdatedDate >= (?3) OR ITEMS.lastUpdatedDate >= (?3)) AND ITEMS.isDeleted != 0) ")
    Page<BibliographicEntity> getDeletedRecordsForIncrementalDump(Pageable pageable, Collection<Integer> cgIds, Collection<String> institutionCodes, Date lastUpdatedDate);

}
