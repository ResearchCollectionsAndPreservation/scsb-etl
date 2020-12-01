package org.recap.camel.datadump;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.recap.RecapConstants;
import org.recap.model.ILSConfigProperties;
import org.recap.repository.InstitutionDetailsRepository;
import org.recap.service.executor.datadump.DataDumpSchedulerExecutorService;
import org.recap.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by rajeshbabuk on 7/7/17.
 */
@Component
public class DataDumpSequenceProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(DataDumpSequenceProcessor.class);

    @Autowired
    private DataDumpSchedulerExecutorService dataDumpSchedulerExecutorService;

    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    PropertyUtil propertyUtil;

    /**
     * This method is invoked by route when data export completion message is available and initiates the other institutions data export in sequence.
     *
     * @param exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        String institution = (String) exchange.getIn().getBody();
        String fetchTypeString = RecapConstants.EXPORT_FETCH_TYPE_INSTITUTION.contains(RecapConstants.INCREMENTAL) ? RecapConstants.INCREMENTAL : RecapConstants.DELETED;
        logger.info("Completed {} export for {} institution", fetchTypeString, institution);

        List<String> allInstitutionCodeExceptHTC = institutionDetailsRepository.findAllInstitutionCodeExceptHTC();
        int i = allInstitutionCodeExceptHTC.indexOf(institution);
        String nextInstitution =(i<allInstitutionCodeExceptHTC.size()-1) ? allInstitutionCodeExceptHTC.get(i + 1) : "";
        ILSConfigProperties ilsConfigProperties = propertyUtil.getILSConfigProperties(institution);

            if (ilsConfigProperties.getEtlIncrementalDump().equalsIgnoreCase(RecapConstants.EXPORT_FETCH_TYPE_INSTITUTION)) {
                RecapConstants.EXPORT_FETCH_TYPE_INSTITUTION = ilsConfigProperties.getEtlDeletedDump();
                dataDumpSchedulerExecutorService.initiateDataDumpForScheduler(RecapConstants.EXPORT_DATE_SCHEDULER, institution, RecapConstants.DATADUMP_FETCHTYPE_DELETED);
            } else if(!nextInstitution.isEmpty() && ilsConfigProperties.getEtlDeletedDump().equalsIgnoreCase(RecapConstants.EXPORT_FETCH_TYPE_INSTITUTION) ) {
                RecapConstants.EXPORT_FETCH_TYPE_INSTITUTION = RecapConstants.EXPORT_INCREMENTAL+nextInstitution;
                dataDumpSchedulerExecutorService.initiateDataDumpForScheduler(RecapConstants.EXPORT_DATE_SCHEDULER, nextInstitution, null);
            }
            else  {
                RecapConstants.EXPORT_SCHEDULER_CALL = false;
                RecapConstants.EXPORT_DATE_SCHEDULER = "";
                RecapConstants.EXPORT_FETCH_TYPE_INSTITUTION = "";
            }
    }
}
