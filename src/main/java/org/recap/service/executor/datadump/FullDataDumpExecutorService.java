package org.recap.service.executor.datadump;

import org.recap.model.export.DataDumpRequest;
import org.recap.model.search.SearchRecordsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


/**
 * Created by premkb on 27/9/16.
 */
@Service
@Scope("prototype")
public class FullDataDumpExecutorService extends AbstractDataDumpExecutorService {

    @Value("${etl.data.dump.fetchtype.full}")
    private String fetchTypeFull;

    /**
     * Returns true if selected fetch type is full data dump.
     *
     * @param fetchType the fetch type
     * @return
     */
    @Override
    public boolean isInterested(String fetchType) {
        return fetchType.equals(fetchTypeFull);
    }

    /**
     * Implementation method for populate search request for full data dump.
     *
     * @param searchRecordsRequest the search records request
     * @param dataDumpRequest      the data dump reques
     */
    @Override
    public void populateSearchRequest(SearchRecordsRequest searchRecordsRequest, DataDumpRequest dataDumpRequest) {
        //Do nothing
    }
}
