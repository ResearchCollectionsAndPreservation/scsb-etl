package org.recap.service.email.datadump;

import org.apache.camel.ProducerTemplate;
import org.recap.RecapConstants;
import org.recap.camel.EmailPayLoad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * Created by premkb on 21/9/16.
 */
@Service
public class DataDumpEmailService {

    @Value("${etl.dump.directory}")
    private String fileSystemDataDumpDirectory;

    @Value("${ftp.datadump.remote.server}")
    private String ftpDataDumpDirectory;

    @Autowired
    private ProducerTemplate producer;

    /**
     * Send email with the given parameters.
     *
     * @param institutionCodes        the institution codes
     * @param totalRecordCount        the total record count
     * @param failedRecordCount       the failed record count
     * @param transmissionType        the transmission type
     * @param dateTimeStringForFolder the date time string for folder
     * @param toEmailAddress          the to email address
     * @param emailBodyFor            the email body for
     */
    public void sendEmail(List<String> institutionCodes, Integer totalRecordCount, Integer failedRecordCount, String transmissionType, String dateTimeStringForFolder, String toEmailAddress,String emailBodyFor,Integer exportedItemCount) {
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        emailPayLoad.setInstitutions(institutionCodes);
        emailPayLoad.setLocation(getLocation(transmissionType,dateTimeStringForFolder));
        emailPayLoad.setCount(totalRecordCount);
        emailPayLoad.setFailedCount(failedRecordCount);
        emailPayLoad.setTo(toEmailAddress);
        emailPayLoad.setItemCount(exportedItemCount);
        producer.sendBodyAndHeader(RecapConstants.EMAIL_Q, emailPayLoad, RecapConstants.DATADUMP_EMAILBODY_FOR,emailBodyFor);
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
        if (ftpLocation.contains(RecapConstants.FTP_PORT)) {
            String[] splittedFtpLocation = ftpLocation.split(RecapConstants.FTP_PORT);
            return splittedFtpLocation[1];
        } else {
            return ftpLocation;
        }
    }

}
