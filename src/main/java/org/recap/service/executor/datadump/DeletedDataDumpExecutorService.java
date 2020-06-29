package org.recap.service.executor.datadump;

import org.apache.commons.lang3.StringUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.export.DataDumpRequest;
import org.recap.model.search.SearchRecordsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Created by premkb on 27/9/16.
 */
@Service
public class DeletedDataDumpExecutorService extends AbstractDataDumpExecutorService {

    @Value("${datadump.deleted.type.onlyorphan.institution}")
    private String deletedOnlyOrphanInstitution;

    /**
     * Returns true if selected fetch type is deleted records data dump.
     *
     * @param fetchType the fetch type
     * @return
     */
    @Override
    public boolean isInterested(String fetchType) {
        return fetchType.equals(RecapConstants.DATADUMP_FETCHTYPE_DELETED);
    }

    /**
     * Populates search request with deleted flag and item last updated date.
     *
     * @param searchRecordsRequest the search records request
     * @param dataDumpRequest      the data dump request
     */
    @Override
    public void populateSearchRequest(SearchRecordsRequest searchRecordsRequest, DataDumpRequest dataDumpRequest) {
        boolean onlyOrphan = isDeletedOnlyOrphanInstitution(dataDumpRequest);
        searchRecordsRequest.setDeleted(true);
        if(StringUtils.isNotBlank(dataDumpRequest.getDate()) && !onlyOrphan) {
            searchRecordsRequest.setFieldName(RecapCommonConstants.ITEM_LASTUPDATED_DATE);
            searchRecordsRequest.setFieldValue(getFormattedDateString(dataDumpRequest.getDate(), dataDumpRequest.getToDate()));
        } else if(StringUtils.isNotBlank(dataDumpRequest.getDate()) && onlyOrphan){
            searchRecordsRequest.setFieldName(RecapCommonConstants.BIB_LASTUPDATED_DATE);
            searchRecordsRequest.setFieldValue(getFormattedDateString(dataDumpRequest.getDate(), dataDumpRequest.getToDate()));
        }
        searchRecordsRequest.setRequestingInstitution(dataDumpRequest.getRequestingInstitutionCode());
    }

    private boolean isDeletedOnlyOrphanInstitution(DataDumpRequest dataDumpRequest){
        String requestingInstitution = dataDumpRequest.getRequestingInstitutionCode();
        List<String> deleteOnlyOrphanInstitutionList = getInstitutionList(deletedOnlyOrphanInstitution);
        return deleteOnlyOrphanInstitutionList.contains(requestingInstitution);
    }

    private List<String> getInstitutionList(String institutionString){
        return Arrays.asList(institutionString.split("\\s*,\\s*"));
    }
}
