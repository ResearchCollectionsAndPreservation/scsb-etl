package org.recap.report;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.FilenameUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.csv.FailureReportReCAPCSVRecord;
import org.recap.model.csv.ReCAPCSVFailureRecord;
import org.recap.model.jpa.ReportEntity;
import org.recap.util.ReCAPCSVFailureRecordGenerator;
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
public class FTPFailureReportGenerator implements ReportGeneratorInterface {

    /**
     * The Producer template.
     */
    @Autowired
    ProducerTemplate producerTemplate;

    /**
     * Returns true if report type is 'Failure'.
     *
     * @param reportType the report type
     * @return
     */
    @Override
    public boolean isInterested(String reportType) {
        return reportType.equalsIgnoreCase(RecapCommonConstants.FAILURE) ? true : false;
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
     * Generates report with failure records for initial data load.
     *
     * @param reportEntities the report entities
     * @param fileName       the file name
     * @return the file name
     */
    @Override
    public String generateReport(List<ReportEntity> reportEntities, String fileName) {

        if(!CollectionUtils.isEmpty(reportEntities)) {
            ReCAPCSVFailureRecord reCAPCSVFailureRecord = new ReCAPCSVFailureRecord();
            List<FailureReportReCAPCSVRecord> failureReportReCAPCSVRecords = new ArrayList<>();
            for(ReportEntity reportEntity : reportEntities) {
                FailureReportReCAPCSVRecord failureReportReCAPCSVRecord = new ReCAPCSVFailureRecordGenerator().prepareFailureReportReCAPCSVRecord(reportEntity);
                failureReportReCAPCSVRecords.add(failureReportReCAPCSVRecord);
            }
            ReportEntity reportEntity = reportEntities.get(0);
            reCAPCSVFailureRecord.setReportType(reportEntity.getType());
            reCAPCSVFailureRecord.setInstitutionName(reportEntity.getInstitutionName());
            reCAPCSVFailureRecord.setFileName(fileName);
            reCAPCSVFailureRecord.setFailureReportReCAPCSVRecordList(failureReportReCAPCSVRecords);
            producerTemplate.sendBody(RecapConstants.FTP_SUCCESS_Q, reCAPCSVFailureRecord);
            DateFormat df = new SimpleDateFormat(RecapCommonConstants.DATE_FORMAT_FOR_FILE_NAME);
            return FilenameUtils.removeExtension(reCAPCSVFailureRecord.getFileName()) + "-" + reCAPCSVFailureRecord.getReportType() + "-" + df.format(new Date()) + ".csv";
        }
        return null;
    }
}
