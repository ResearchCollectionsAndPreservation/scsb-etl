package org.recap.service.email.datadump;

import org.apache.camel.ProducerTemplate;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.camel.EmailPayLoad;
import org.recap.model.export.DataDumpRequest;
import org.recap.util.datadump.DataDumpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * Created by premkb on 21/9/16.
 */
@Service
public class DataDumpEmailService {

    @Value("${etl.dump.directory}")
    private String fileSystemDataDumpDirectory;

    @Value("${ftp.datadump.remote.server}")
    private String ftpDataDumpDirectory;

    @Value("${datadump.fetchtype.full}")
    private String dataDumpFetchType;

    @Value("${data.dump.email.nypl.cc}")
    private String dataDumpCCForNypl;

    @Value("${data.dump.email.cul.cc}")
    private String dataDumpCCForCul;

    @Value("${data.dump.email.pul.cc}")
    private String dataDumpCCForPul;

    @Value("${data.dump.notification.cc}")
    private String dataDumpNotificationCC;

    @Autowired
    private DataDumpUtil dataDumpUtil;

    @Autowired
    private ProducerTemplate producer;

    /**
     * Send email with the given parameters.
     * @param institutionCodes        the institution codes
     * @param totalRecordCount        the total record count
     * @param failedRecordCount       the failed record count
     * @param transmissionType        the transmission type
     * @param dateTimeStringForFolder the date time string for folder
     * @param toEmailAddress          the to email address
     * @param emailBodyFor            the email body for
     * @param fetchType
     * @param requestingInstitutionCode
     */
    public void sendEmail(List<String> institutionCodes, Integer totalRecordCount, Integer failedRecordCount, String transmissionType, String dateTimeStringForFolder, String toEmailAddress, String emailBodyFor, Integer exportedItemCount, String fetchType, String requestingInstitutionCode) {
        if(fetchType.equals(dataDumpFetchType)) {
            EmailPayLoad emailPayLoad = new EmailPayLoad();
            emailPayLoad.setInstitutions(institutionCodes);
            emailPayLoad.setLocation(getLocation(transmissionType, dateTimeStringForFolder));
            emailPayLoad.setCount(totalRecordCount);
            emailPayLoad.setFailedCount(failedRecordCount);
            emailPayLoad.setTo(toEmailAddress);
            emailPayLoad.setItemCount(exportedItemCount);
            producer.sendBodyAndHeader(RecapConstants.EMAIL_Q, emailPayLoad, RecapConstants.DATADUMP_EMAILBODY_FOR, emailBodyFor);
        }
        else if(fetchType.equals(RecapConstants.DATADUMP_FETCHTYPE_INCREMENTAL)){
            setEmailForDatadump(transmissionType, emailBodyFor, dateTimeStringForFolder, toEmailAddress,
                    requestingInstitutionCode, RecapConstants.SUBJECT_INCREMENTAL_DATA_DUMP, RecapConstants.EMAIL_INCREMENTAL_DATA_DUMP);
        }
        else if(fetchType.equals(RecapConstants.DATADUMP_DELETED_JSON_FORMAT)){
            setEmailForDatadump(transmissionType, emailBodyFor, dateTimeStringForFolder, toEmailAddress,
                    requestingInstitutionCode, RecapConstants.SUBJECT_DELETION_DATA_DUMP, RecapConstants.EMAIL_DELETION_DATA_DUMP);
        }
    }

    private String mailForCc(String requestingInstitutionCode) {
        if(requestingInstitutionCode.equalsIgnoreCase(RecapCommonConstants.PRINCETON)){
            return dataDumpCCForPul;
        }
        else if (requestingInstitutionCode.equalsIgnoreCase(RecapCommonConstants.COLUMBIA)){
            return dataDumpCCForCul;
        }
        else if (requestingInstitutionCode.equalsIgnoreCase(RecapCommonConstants.NYPL)){
            return dataDumpCCForNypl;
        }
        return null;
    }

    /**
     * Gets the location to write data dump files for the given transmission type.
     *
     * @param transmissionType
     * @param dateTimeStringForFolder
     * @return
     */
    private String getLocation(String transmissionType,String dateTimeStringForFolder) {
        String location = null;
        if ("0".equals(transmissionType)) {
            location = "FTP location - " + getFtpLocation(ftpDataDumpDirectory) + File.separator + dateTimeStringForFolder;
        } else if ("2".equals(transmissionType)) {
            location = "File System - " + fileSystemDataDumpDirectory + File.separator + dateTimeStringForFolder;
        }
        return location;
    }

    private String getFtpLocation(String ftpLocation) {
        if (ftpLocation.contains(File.separator)) {
            String[] splittedFtpLocation = ftpLocation.split(File.separator,2);
            return splittedFtpLocation[1];
        } else {
            return ftpLocation;
        }
    }

    public void setEmailForDatadump(String transmissionType,String emailBodyFor, String dateTimeStringForFolder, String toEmailAddress,
                                     String requestingInstitutionCode, String subject, String emailBody)

    {
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        emailPayLoad.setLocation(getLocation(transmissionType, dateTimeStringForFolder));
        emailPayLoad.setTo(toEmailAddress);
        emailPayLoad.setCc(mailForCc(requestingInstitutionCode));
        emailPayLoad.setSubject(subject);
        producer.sendBodyAndHeader(RecapConstants.EMAIL_Q, emailPayLoad, RecapConstants.DATADUMP_EMAILBODY_FOR, emailBodyFor.equals(RecapConstants.DATADUMP_NO_DATA_AVAILABLE)?emailBodyFor:emailBody);

    }

    public void sendEmailForDumpNotification(DataDumpRequest dataDumpRequest) {
        Boolean isIncrementalSequence = Optional.ofNullable(dataDumpRequest.isIncrementalSequence()).orElse(false);
        if(!isIncrementalSequence) {
            String fetchType = dataDumpUtil.getFetchType(dataDumpRequest.getFetchType());
            String outputformat = dataDumpUtil.getOutputformat(dataDumpRequest.getOutputFileFormat());
            String transmissionType = dataDumpUtil.getTransmissionType(dataDumpRequest.getTransmissionType());
            EmailPayLoad emailPayLoad = new EmailPayLoad();
            emailPayLoad.setTo(dataDumpRequest.getToEmailAddress());
            emailPayLoad.setCc(dataDumpNotificationCC);
            emailPayLoad.setSubject("Notification : Initiated data-dump for Fetch type:" + fetchType);
            emailPayLoad.setInstitutionsRequested(dataDumpRequest.getInstitutionCodes());
            emailPayLoad.setRequestingInstitution(dataDumpRequest.getRequestingInstitutionCode());
            emailPayLoad.setFetchType(fetchType);
            emailPayLoad.setCollectionGroupIds(dataDumpRequest.getCollectionGroupIds());
            emailPayLoad.setTransmissionType(transmissionType);
            emailPayLoad.setOutputFileFormat(outputformat);
            producer.sendBodyAndHeader(RecapConstants.EMAIL_Q, emailPayLoad, RecapConstants.DATADUMP_EMAILBODY_FOR, RecapConstants.DATADUMP_EXPORT_NOTIFICATION);
        }
    }
}


