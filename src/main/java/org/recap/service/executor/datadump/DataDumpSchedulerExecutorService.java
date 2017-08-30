package org.recap.service.executor.datadump;

import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.model.export.DataDumpRequest;
import org.recap.service.preprocessor.DataDumpExportService;
import org.recap.util.datadump.JobDataParameterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by rajeshbabuk on 7/7/17.
 */
@Service
public class DataDumpSchedulerExecutorService {

    private static final Logger logger = LoggerFactory.getLogger(DataDumpSchedulerExecutorService.class);

    @Value("${data.dump.email.nypl.to}")
    private String dataDumpEmailNyplTo;

    @Value("${data.dump.email.pul.to}")
    private String dataDumpEmailPulTo;

    @Value("${data.dump.email.cul.to}")
    private String dataDumpEmailCulTo;

    @Autowired
    private DataDumpExportService dataDumpExportService;

    @Autowired
    private JobDataParameterUtil jobDataParameterUtil;

    /**
     * Gets data dump export service.
     *
     * @return the data dump export service
     */
    public DataDumpExportService getDataDumpExportService() {
        return dataDumpExportService;
    }

    /**
     * Gets job data parameter util
     * @return the job data parameter util
     */
    public JobDataParameterUtil getJobDataParameterUtil() {
        return jobDataParameterUtil;
    }

    /**
     * This method initiates the data export (Incremental and Deleted data only) to run in sequence for all the institutions.
     *
     * @param date
     * @param requestingInstitutionCode
     * @param fetchType
     * @return
     */
    public String initiateDataDumpForScheduler(String date, String requestingInstitutionCode, String fetchType) {
        logger.info("Export data dump for {} from {}", requestingInstitutionCode, date);
        DataDumpRequest dataDumpRequest = new DataDumpRequest();

        Map<String, String> requestParameterMap = getJobDataParameterUtil().buildJobRequestParameterMap(getExportJobNameByInstitution(requestingInstitutionCode));
        if (StringUtils.isBlank(fetchType)) {
            fetchType = requestParameterMap.get(RecapConstants.FETCH_TYPE);
        }
        getDataDumpExportService().setDataDumpRequest(dataDumpRequest, fetchType, requestParameterMap.get(RecapConstants.INSTITUTION_CODES), date, null, requestParameterMap.get(RecapConstants.COLLECTION_GROUP_IDS), requestParameterMap.get(RecapConstants.TRANSMISSION_TYPE), requestingInstitutionCode, getToEmailAddress(requestingInstitutionCode), requestParameterMap.get("outputFormat"));
        String responseMessage = getDataDumpExportService().validateIncomingRequest(dataDumpRequest);
        if (responseMessage != null) {
            RecapConstants.EXPORT_SCHEDULER_CALL = false;
            RecapConstants.EXPORT_DATE_SCHEDULER = "";
            RecapConstants.EXPORT_FETCH_TYPE_INSTITUTION = "";
            return responseMessage;
        }
        responseMessage = getDataDumpExportService().startDataDumpProcess(dataDumpRequest);
        return responseMessage;
    }

    /**
     * Gets the name of export for institution.
     * @param requestingInstitutionCode
     * @return
     */
    private String getExportJobNameByInstitution(String requestingInstitutionCode) {
        if (RecapConstants.PRINCETON.equals(requestingInstitutionCode)) {
            return RecapConstants.EXPORT_INCREMENTAL_PUL;
        } else if (RecapConstants.COLUMBIA.equals(requestingInstitutionCode)) {
            return RecapConstants.EXPORT_INCREMENTAL_CUL;
        } else if (RecapConstants.NYPL.equals(requestingInstitutionCode)) {
            return RecapConstants.EXPORT_INCREMENTAL_NYPL;
        }
        return null;
    }

    /**
     * Gets to email address based on institution.
     * @param requestingInstitutionCode
     * @return
     */
    private String getToEmailAddress(String requestingInstitutionCode) {
        if (RecapConstants.PRINCETON.equals(requestingInstitutionCode)) {
            return dataDumpEmailPulTo;
        } else if (RecapConstants.COLUMBIA.equals(requestingInstitutionCode)) {
            return dataDumpEmailCulTo;
        } else if (RecapConstants.NYPL.equals(requestingInstitutionCode)) {
            return dataDumpEmailNyplTo;
        }
        return null;
    }
}
