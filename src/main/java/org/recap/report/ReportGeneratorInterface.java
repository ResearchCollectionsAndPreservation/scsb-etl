package org.recap.report;

import org.recap.model.jparw.ReportEntity;

import java.util.List;

/**
 * Created by peris on 8/17/16.
 */
public interface ReportGeneratorInterface {

    /**
     * Is interested boolean.
     *
     * @param reportType the report type
     * @return the boolean
     */
    boolean isInterested(String reportType);

    /**
     * Is transmitted boolean.
     *
     * @param transmissionType the transmission type
     * @return the boolean
     */
    boolean isTransmitted(String transmissionType);

    /**
     * Is operation type boolean.
     *
     * @param operationType the operation type
     * @return the boolean
     */
    boolean isOperationType(String operationType);

    /**
     * Generate report.
     *
     * @param reportEntities the report entities
     * @param fileName       the file name
     * @return the string
     */
    String generateReport(List<ReportEntity> reportEntities, String fileName);
}
