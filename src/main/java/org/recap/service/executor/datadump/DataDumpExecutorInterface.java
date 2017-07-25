package org.recap.service.executor.datadump;

import org.recap.model.export.DataDumpRequest;

import java.text.ParseException;
import java.util.concurrent.ExecutionException;

/**
 * Created by premkb on 27/9/16.
 */
public interface DataDumpExecutorInterface {

    /**
     * Is interested boolean.
     *
     * @param fetchType the fetch type
     * @return the boolean
     */
    public boolean isInterested(String fetchType);

    /**
     * Process data dump.
     *
     * @param dataDumpRequest the data dump request
     * @return the string
     * @throws ExecutionException   the execution exception
     * @throws InterruptedException the interrupted exception
     */
    public String process(DataDumpRequest dataDumpRequest) throws ExecutionException, InterruptedException, ParseException;

}
