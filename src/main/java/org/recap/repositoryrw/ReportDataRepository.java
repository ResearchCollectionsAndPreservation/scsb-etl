package org.recap.repositoryrw;

import org.recap.model.jparw.ReportDataEntity;
import org.recap.repository.jpa.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by premkb on 24/1/17.
 */
public interface ReportDataRepository extends BaseRepository<ReportDataEntity> {

    /**
     * Gets report data for matching institution bib.
     *
     * @param recordNumList  the record num list
     * @param headerNameList the header name list
     * @return the report data for matching institution bib
     */
    @Query(value="SELECT REPORTDATA FROM ReportDataEntity REPORTDATA WHERE REPORTDATA.recordNum IN (?1) AND REPORTDATA.headerName IN (?2) ORDER BY REPORTDATA.recordNum")
    List<ReportDataEntity> getReportDataForMatchingInstitutionBib(List<String> recordNumList,List<String> headerNameList);
}
