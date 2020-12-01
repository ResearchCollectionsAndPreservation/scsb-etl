package org.recap.controller;

import org.recap.RecapConstants;
import org.recap.camel.dynamicrouter.DynamicRouteBuilder;
import org.recap.repository.InstitutionDetailsRepository;
import org.recap.service.executor.datadump.DataDumpSchedulerExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;

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
    @GetMapping(value = "/exportDataDumpSequence")
    @ResponseBody
    public String exportDataDump(@RequestParam String date) {
        List<String> allInstitutionCodeExceptHTC = institutionDetailsRepository.findAllInstitutionCodeExceptHTC();
        Optional<String> firstInstitution = allInstitutionCodeExceptHTC.stream().findFirst();
        RecapConstants.EXPORT_SCHEDULER_CALL = true;
        RecapConstants.EXPORT_DATE_SCHEDULER = date;
        RecapConstants.EXPORT_FETCH_TYPE_INSTITUTION = RecapConstants.EXPORT_INCREMENTAL+firstInstitution.get();
        getDynamicRouteBuilder().addDataDumpExportRoutes();
        return dataDumpSchedulerExecutorService.initiateDataDumpForScheduler(date, firstInstitution.get(), null);
    }
}
