package org.recap.service.executor.datadump;

import org.recap.model.export.DataDumpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by premkb on 27/9/16.
 */
@Service
public class DataDumpExecutorService {

    private List<DataDumpExecutorInterface> dataDumpExecutorInterfaceList;

    @Autowired
    private IncrementalDataDumpExecutorService incrementalDataDumpExecutorService;

    @Autowired
    private FullDataDumpExecutorService fullDataDumpExecutorService;

    @Autowired
    private DeletedDataDumpExecutorService deletedDataDumpExecutorService;

    /**
     * Generate full data dump or incremental data dump or deleted records data dump.
     *
     * @param dataDumpRequest the data dump request
     * @return the string
     * @throws ExecutionException   the execution exception
     * @throws InterruptedException the interrupted exception
     */
    public String generateDataDump(DataDumpRequest dataDumpRequest) throws ExecutionException, InterruptedException, ParseException {
        String outputString = null;
        for(DataDumpExecutorInterface dataDumpExecutorInterface:getExecutor()){
            if(dataDumpExecutorInterface.isInterested(dataDumpRequest.getFetchType())){
                outputString = dataDumpExecutorInterface.process(dataDumpRequest);
            }
        }
        return outputString;
    }

    /**
     * Get data dump executor list.
     *
     * @return the list
     */
    public List<DataDumpExecutorInterface> getExecutor(){
        if(CollectionUtils.isEmpty(dataDumpExecutorInterfaceList)){
            dataDumpExecutorInterfaceList = new ArrayList<>();
            dataDumpExecutorInterfaceList.add(fullDataDumpExecutorService);
            dataDumpExecutorInterfaceList.add(incrementalDataDumpExecutorService);
            dataDumpExecutorInterfaceList.add(deletedDataDumpExecutorService);
        }
        return dataDumpExecutorInterfaceList;
    }

}
