package org.recap.model.export;

import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.util.DateUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by premkb on 23/9/16.
 */
@Service
@Scope("prototype")
public class DataDumpCallableHelperService {

    /**
     * Gets full data dump records.
     *
     * @param page                           the page
     * @param size                           the size
     * @param dataDumpRequest                the data dump request
     * @param bibliographicDetailsRepository the bibliographic details repository
     * @return the full data dump records
     */
    public List<BibliographicEntity> getFullDataDumpRecords(int page, int size, DataDumpRequest dataDumpRequest
            , BibliographicDetailsRepository bibliographicDetailsRepository) {
        Page<BibliographicEntity> bibliographicEntities;
        bibliographicEntities = bibliographicDetailsRepository.getRecordsForFullDump(PageRequest.of(page, size)
                    , dataDumpRequest.getCollectionGroupIds(), dataDumpRequest.getInstitutionCodes());
        return bibliographicEntities.getContent();
    }

    /**
     * Gets incremental data dump records.
     *
     * @param page                           the page
     * @param size                           the size
     * @param dataDumpRequest                the data dump request
     * @param bibliographicDetailsRepository the bibliographic details repository
     * @return the incremental data dump records
     */
    public List<BibliographicEntity> getIncrementalDataDumpRecords(int page, int size, DataDumpRequest dataDumpRequest, BibliographicDetailsRepository bibliographicDetailsRepository) {
        Date inputDate = DateUtil.getDateFromString(dataDumpRequest.getDate(), RecapCommonConstants.DATE_FORMAT_YYYYMMDDHHMM);
        Page<BibliographicEntity> bibliographicEntities;
        bibliographicEntities = bibliographicDetailsRepository.getRecordsForIncrementalDump(PageRequest.of(page, size)
                    , dataDumpRequest.getCollectionGroupIds(), dataDumpRequest.getInstitutionCodes(), inputDate);
        return bibliographicEntities.getContent();
    }

    /**
     * Gets deleted records.
     *
     * @param page                           the page
     * @param size                           the size
     * @param dataDumpRequest                the data dump request
     * @param bibliographicDetailsRepository the bibliographic details repository
     * @return the deleted records
     */
    public List<BibliographicEntity> getDeletedRecords(int page, int size, DataDumpRequest dataDumpRequest, BibliographicDetailsRepository bibliographicDetailsRepository) {
        Page<BibliographicEntity> bibliographicEntities;
        Date inputDate = DateUtil.getDateFromString(dataDumpRequest.getDate(), RecapCommonConstants.DATE_FORMAT_YYYYMMDDHHMM);
        if(dataDumpRequest.getDate()==null){
            bibliographicEntities = bibliographicDetailsRepository.getDeletedRecordsForFullDump(PageRequest.of(page, size)
                    , dataDumpRequest.getCollectionGroupIds(), dataDumpRequest.getInstitutionCodes());
        }else{
            bibliographicEntities = bibliographicDetailsRepository.getDeletedRecordsForIncrementalDump(PageRequest.of(page, size)
                    , dataDumpRequest.getCollectionGroupIds(), dataDumpRequest.getInstitutionCodes(), inputDate);
        }
        return bibliographicEntities.getContent();
    }

}
