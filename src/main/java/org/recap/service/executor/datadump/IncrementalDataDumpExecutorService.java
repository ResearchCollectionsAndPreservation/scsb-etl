package org.recap.service.executor.datadump;

import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.export.DataDumpRequest;
import org.recap.model.search.SearchRecordsRequest;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Created by premkb on 27/9/16.
 */
@Service
@Scope("prototype")
public class IncrementalDataDumpExecutorService extends AbstractDataDumpExecutorService {

    /**
     * Returns true if selected fetch type is incremental data dump.
     *
     * @param fetchType the fetch type
     * @return
     */
    @Override
    public boolean isInterested(String fetchType) {
        return fetchType.equals(RecapConstants.DATADUMP_FETCHTYPE_INCREMENTAL);
    }

    /**
     * Populates search request with bib item last updated date.
     *
     * @param searchRecordsRequest the search records request
     * @param dataDumpRequest      the data dump request
     */
    @Override
    public void populateSearchRequest(SearchRecordsRequest searchRecordsRequest, DataDumpRequest dataDumpRequest) {
        searchRecordsRequest.setFieldName(RecapCommonConstants.BIBITEM_LASTUPDATED_DATE);
        searchRecordsRequest.setFieldValue(getFormattedDateString(dataDumpRequest.getDate(), dataDumpRequest.getToDate()));
        searchRecordsRequest.setRequestingInstitution(dataDumpRequest.getRequestingInstitutionCode());
    }
}
