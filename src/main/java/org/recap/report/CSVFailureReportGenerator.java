package org.recap.report;

import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jparw.ReportEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by peris on 8/17/16.
 */
@Component
public class CSVFailureReportGenerator extends CommonReportGenerator implements ReportGeneratorInterface  {

    /**
     * Returns true if report type is 'Failure'.
     *
     * @param reportType the report type
     * @return
     */
    @Override
    public boolean isInterested(String reportType) {
        return reportType.equalsIgnoreCase(RecapCommonConstants.FAILURE);
    }

    /**
     * Returns true if transmission type is 'FileSystem'.
     *
     * @param transmissionType the transmission type
     * @return
     */
    @Override
    public boolean isTransmitted(String transmissionType) {
        return transmissionType.equalsIgnoreCase(RecapCommonConstants.FILE_SYSTEM);
    }

    /**
     * Returns true if operation type is 'ETL'.
     *
     * @param operationType the operation type
     * @return
     */
    @Override
    public boolean isOperationType(String operationType) {
        return operationType.equalsIgnoreCase(RecapConstants.OPERATION_TYPE_ETL);
    }

    /**
     * Generates CSV report with failure records for initial data load.
     *
     * @param reportEntities the report entities
     * @param fileName       the file name
     * @return the file name
     */
    @Override
    public String generateReport(List<ReportEntity> reportEntities, String fileName) {
        return generateFailureReport(reportEntities, fileName, RecapConstants.CSV_FAILURE_Q);
    }
}
