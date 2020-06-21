package org.recap.report;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.FilenameUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.csv.ReCAPCSVSuccessRecord;
import org.recap.model.csv.SuccessReportReCAPCSVRecord;
import org.recap.model.jpa.ReportEntity;
import org.recap.util.ReCAPCSVSuccessRecordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by angelind on 18/8/16.
 */
@Component
public class FTPSuccessReportGenerator implements ReportGeneratorInterface {

    /**
     * The Producer template.
     */
    @Autowired
    ProducerTemplate producerTemplate;

    /**
     * Returns true if report type is 'Success'.
     *
     * @param reportType the report type
     * @return
     */
    @Override
    public boolean isInterested(String reportType) {
        return reportType.equalsIgnoreCase(RecapCommonConstants.SUCCESS) ? true : false;
    }

    /**
     * Returns true if transmission type is 'FTP'.
     *
     * @param transmissionType the transmission type
     * @return
     */
    @Override
    public boolean isTransmitted(String transmissionType) {
        return transmissionType.equalsIgnoreCase(RecapCommonConstants.FTP) ? true : false;
    }

    /**
     * Returns true if operation type is 'ETL'.
     *
     * @param operationType the operation type
     * @return
     */
    @Override
    public boolean isOperationType(String operationType) {
        return operationType.equalsIgnoreCase(RecapConstants.OPERATION_TYPE_ETL) ? true : false;
    }

    /**
     * Generates report with success records for initial data load.
     *
     * @param reportEntities the report entities
     * @param fileName       the file name
     * @return the file name
     */
    @Override
    public String generateReport(List<ReportEntity> reportEntities, String fileName) {

        if(!CollectionUtils.isEmpty(reportEntities)) {
            ReCAPCSVSuccessRecord reCAPCSVSuccessRecord = new ReCAPCSVSuccessRecord();
            List<SuccessReportReCAPCSVRecord> successReportReCAPCSVRecords = new ArrayList<>();
            for(ReportEntity reportEntity : reportEntities) {
                SuccessReportReCAPCSVRecord successReportReCAPCSVRecord = new ReCAPCSVSuccessRecordGenerator().prepareSuccessReportReCAPCSVRecord(reportEntity);
                successReportReCAPCSVRecords.add(successReportReCAPCSVRecord);
            }
            ReportEntity reportEntity = reportEntities.get(0);
            reCAPCSVSuccessRecord.setReportType(reportEntity.getType());
            reCAPCSVSuccessRecord.setInstitutionName(reportEntity.getInstitutionName());
            reCAPCSVSuccessRecord.setReportFileName(fileName);
            reCAPCSVSuccessRecord.setSuccessReportReCAPCSVRecordList(successReportReCAPCSVRecords);
            producerTemplate.sendBody(RecapConstants.FTP_FAILURE_Q, reCAPCSVSuccessRecord);
            DateFormat df = new SimpleDateFormat(RecapCommonConstants.DATE_FORMAT_FOR_FILE_NAME);
            return FilenameUtils.removeExtension(reCAPCSVSuccessRecord.getReportFileName()) + "-" + reCAPCSVSuccessRecord.getReportType() + "-" + df.format(new Date()) + ".csv";
        }
        return null;
    }
}
