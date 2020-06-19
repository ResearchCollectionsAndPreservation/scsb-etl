package org.recap.controller;

import org.recap.RecapConstants;
import org.recap.camel.dynamicrouter.DynamicRouteBuilder;
import org.recap.service.executor.datadump.DataDumpSchedulerExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by rajeshbabuk on 7/7/17.
 */
@RestController
@RequestMapping("/dataDumpSequence")
public class DataDumpSequenceRestController {

    @Autowired
    private DataDumpSchedulerExecutorService dataDumpSchedulerExecutorService;

    @Autowired
    private DynamicRouteBuilder dynamicRouteBuilder;

    /**
     * Gets dynamic route builder.
     *
     * @return the dynamic route builder
     */
    public DynamicRouteBuilder getDynamicRouteBuilder() {
        return dynamicRouteBuilder;
    }

    /**
     * API to initiate the data dump export for scheduler to run in sequence.
     *
     * @param date the date
     * @return string
     */
    @RequestMapping(value = "/exportDataDumpSequence", method = RequestMethod.GET)
    @ResponseBody
    public String exportDataDump(@RequestParam String date) {
        RecapConstants.EXPORT_SCHEDULER_CALL = true;
        RecapConstants.EXPORT_DATE_SCHEDULER = date;
        RecapConstants.EXPORT_FETCH_TYPE_INSTITUTION = RecapConstants.EXPORT_INCREMENTAL_PUL;
        getDynamicRouteBuilder().addDataDumpExportRoutes();
        return dataDumpSchedulerExecutorService.initiateDataDumpForScheduler(date, RecapConstants.PRINCETON, null);
    }
}
