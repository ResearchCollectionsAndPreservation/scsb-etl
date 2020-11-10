package org.recap.report;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.model.jparw.ReportEntity;
import org.recap.repositoryrw.ReportDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by peris on 8/17/16.
 */
@Component
public class ReportGenerator {

    /**
     * The Report detail repository.
     */
    @Autowired
    ReportDetailRepository reportDetailRepository;

    /**
     * The Producer template.
     */
    @Autowired
    ProducerTemplate producerTemplate;

    @Value("${etl.report.directory}")
    private String reportDirectory;

    /**
     * The Csv failure report generator.
     */
    @Autowired
    CSVFailureReportGenerator csvFailureReportGenerator;

    /**
     * The Csv success report generator.
     */
    @Autowired
    CSVSuccessReportGenerator csvSuccessReportGenerator;

    /**
     * The Ftp failure report generator.
     */
    @Autowired
    FTPFailureReportGenerator ftpFailureReportGenerator;

    /**
     * The Ftp success report generator.
     */
    @Autowired
    FTPSuccessReportGenerator ftpSuccessReportGenerator;

    /**
     * The Csv data dump success report generator.
     */
    @Autowired
    CSVDataDumpSuccessReportGenerator csvDataDumpSuccessReportGenerator;

    /**
     * The Csv data dump failure report generator.
     */
    @Autowired
    CSVDataDumpFailureReportGenerator csvDataDumpFailureReportGenerator;

    /**
     * The Ftp data dump success report generator.
     */
    @Autowired
    FTPDataDumpSuccessReportGenerator ftpDataDumpSuccessReportGenerator;

    /**
     * The Ftp data dump failure report generator.
     */
    @Autowired
    FTPDataDumpFailureReportGenerator ftpDataDumpFailureReportGenerator;

    /**
     * The Report generators.
     */
    List<ReportGeneratorInterface> reportGenerators;

    /**
     * Generate report for the type of operation.
     *
     * @param fileName         the file name
     * @param operationType    the operation type
     * @param reportType       the report type
     * @param institutionName  the institution name
     * @param from             the from
     * @param to               the to
     * @param transmissionType the transmission type
     * @return the string
     */
    public String generateReport(String fileName, String operationType, String reportType, String institutionName, Date from, Date to, String transmissionType) {

        List<ReportEntity> reportEntities;
        if(operationType.equals(RecapConstants.BATCH_EXPORT)){
            reportType = operationType+reportType;
            reportEntities = reportDetailRepository.findByInstitutionAndTypeAndDateRange(institutionName, reportType, from, to);
            fileName = institutionName;
        } else if(StringUtils.isNotBlank(fileName)) {
            reportEntities = reportDetailRepository.findByFileAndInstitutionAndTypeAndDateRange(fileName, institutionName, reportType, from, to);
        } else {
            reportEntities = reportDetailRepository.findByInstitutionAndTypeAndDateRange(institutionName, reportType, from, to);
            fileName = institutionName;
        }

        for (Iterator<ReportGeneratorInterface> iterator = getReportGenerators().iterator(); iterator.hasNext(); ) {
            ReportGeneratorInterface reportGeneratorInterface = iterator.next();
            if(reportGeneratorInterface.isOperationType(operationType) && reportGeneratorInterface.isInterested(reportType)
                    && reportGeneratorInterface.isTransmitted(transmissionType)){
                return reportGeneratorInterface.generateReport(reportEntities, fileName);
            }
        }
        return null;
    }

    /**
     * Gets report generators.
     *
     * @return the report generators
     */
    public List<ReportGeneratorInterface> getReportGenerators() {
        if(CollectionUtils.isEmpty(reportGenerators)) {
            reportGenerators = new ArrayList<>();
            reportGenerators.add(csvFailureReportGenerator);
            reportGenerators.add(csvSuccessReportGenerator);
            reportGenerators.add(ftpFailureReportGenerator);
            reportGenerators.add(ftpSuccessReportGenerator);
            reportGenerators.add(csvDataDumpSuccessReportGenerator);
            reportGenerators.add(csvDataDumpFailureReportGenerator);
            reportGenerators.add(ftpDataDumpSuccessReportGenerator);
            reportGenerators.add(ftpDataDumpFailureReportGenerator);
        }
        return reportGenerators;
    }
}
