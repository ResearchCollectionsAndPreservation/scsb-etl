package org.recap.report;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.FilenameUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.csv.DataDumpSuccessReport;
import org.recap.model.jpa.ReportEntity;
import org.recap.util.datadump.DataDumpSuccessReportGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by premkb on 29/9/16.
 */
@Component
public class CSVDataDumpSuccessReportGenerator implements ReportGeneratorInterface {

    @Autowired
    private ProducerTemplate producerTemplate;

    /**
     * Returns true if report type is 'BatchExportSuccess'.
     *
     * @param reportType the report type
     * @return
     */
    @Override
    public boolean isInterested(String reportType) {
        return reportType.equalsIgnoreCase(RecapConstants.BATCH_EXPORT_SUCCESS) ? true : false;
    }

    /**
     * Returns true if transmission type is 'FileSystem'.
     *
     * @param transmissionType the transmission type
     * @return
     */
    @Override
    public boolean isTransmitted(String transmissionType) {
        return transmissionType.equalsIgnoreCase(RecapCommonConstants.FILE_SYSTEM) ? true : false;
    }

    /**
     * Returns true if operation type is 'BatchExport'.
     *
     * @param operationType the operation type
     * @return
     */
    @Override
    public boolean isOperationType(String operationType) {
        return operationType.equalsIgnoreCase(RecapConstants.BATCH_EXPORT) ? true : false;
    }

    /**
     * Generates CSV report with success records for data dump.
     *
     * @param reportEntities the report entities
     * @param fileName       the file name
     * @return the file name
     */
    @Override
    public String generateReport(List<ReportEntity> reportEntities, String fileName) {
        if(!CollectionUtils.isEmpty(reportEntities)) {
            DataDumpSuccessReport dataDumpSuccessReport = new DataDumpSuccessReport();
            List<DataDumpSuccessReport> dataDumpSuccessReportList = new ArrayList<>();
            for (ReportEntity reportEntity : reportEntities) {
                DataDumpSuccessReport dataDumpSuccessReport1 = new DataDumpSuccessReportGenerator().prepareDataDumpCSVSuccessRecord(reportEntity);
                dataDumpSuccessReportList.add(dataDumpSuccessReport1);
            }
            ReportEntity reportEntity = reportEntities.get(0);
            dataDumpSuccessReport.setReportType(reportEntity.getType());
            dataDumpSuccessReport.setInstitutionName(reportEntity.getInstitutionName());
            dataDumpSuccessReport.setFileName(fileName);
            dataDumpSuccessReport.setDataDumpSuccessReportList(dataDumpSuccessReportList);
            producerTemplate.sendBody(RecapConstants.DATADUMP_SUCCESS_REPORT_CSV_Q, dataDumpSuccessReport);
            DateFormat df = new SimpleDateFormat(RecapCommonConstants.DATE_FORMAT_FOR_FILE_NAME);
            return FilenameUtils.removeExtension(dataDumpSuccessReport.getFileName()) + "-" + dataDumpSuccessReport.getReportType() + "-" + df.format(new Date()) + ".csv";
        }
        return null;
    }
}
