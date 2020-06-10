package org.recap.repository;

import org.recap.model.jpa.MatchingBibInfoDetail;
import org.recap.repository.jpa.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by premkb on 29/1/17.
 */
public interface MatchingBibInfoDetailRepository extends BaseRepository<MatchingBibInfoDetail> {

    /**
     * Gets record num.
     *
     * @param bibIdList the bib id list
     * @return the record num
     */
    @Query(value ="SELECT recordNum FROM MatchingBibInfoDetail WHERE bibId IN (?1)")
    List<Integer> getRecordNum(List<String> bibIdList);

    /**
     * Find by record num list.
     *
     * @param recordNumList the record num list
     * @return the list
     */
    @Query(value ="SELECT MBID FROM MatchingBibInfoDetail MBID WHERE recordNum IN (?1)")
    List<MatchingBibInfoDetail> findByRecordNum(List<Integer> recordNumList);
}
