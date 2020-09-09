package org.recap.camel.datadump.consumer;

import org.apache.camel.ProducerTemplate;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.csv.DataExportFailureReport;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.ReportDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by peris on 11/11/16.
 */
@Component
public class DataExportReportActiveMQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(DataExportReportActiveMQConsumer.class);
    /**
     * The Report detail repository.
     */
    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Autowired
    ProducerTemplate producerTemplate;

    /**
     * This method is invoked by the route to save the success report entity for data export using the values in Map.
     *
     * @param body the body
     * @return the report entity
     */
    public ReportEntity saveSuccessReportEntity(Map body){
        String requestingInstitutionCode = (String) body.get(RecapConstants.REQUESTING_INST_CODE);
        String institutionCodes = (String) body.get(RecapConstants.INSTITUTION_CODES);
        String fetchType = (String) body.get(RecapConstants.FETCH_TYPE);
        String collectionGroupIds = (String) body.get(RecapConstants.COLLECTION_GROUP_IDS);
        String transmissionType = (String) body.get(RecapConstants.TRANSMISSION_TYPE);
        String exportFormat = (String) body.get(RecapConstants.EXPORT_FORMAT);
        String fromDate = body.get(RecapConstants.EXPORT_FROM_DATE) != null ? (String) body.get(RecapConstants.EXPORT_FROM_DATE) :"";
        String toEmailId = (String) body.get(RecapConstants.TO_EMAIL_ID);
        String type = (String) body.get(RecapConstants.BATCH_EXPORT);
        String requestId = (String) (body.get(RecapCommonConstants.REQUEST_ID));
        String numBibsExported = (String) body.get(RecapConstants.NUM_BIBS_EXPORTED);
        String numRecords = (String) body.get(RecapConstants.NUM_RECORDS);
        Integer exportedItemCount = (Integer) body.get(RecapConstants.ITEM_EXPORTED_COUNT);
        logger.info("No. of bib exported for a single batch---->{}",numRecords);
        logger.info("No. of item exported for a single batch---->{}",exportedItemCount);

        List<ReportEntity> byFileName = getReportDetailRepository().findByFileNameAndType(requestId, RecapConstants.BATCH_EXPORT_SUCCESS);

        ReportEntity reportEntity;
        if (CollectionUtils.isEmpty(byFileName)) {
            reportEntity = new ReportEntity();
            reportEntity.setCreatedDate(new Date());
            reportEntity.setInstitutionName(requestingInstitutionCode);
            reportEntity.setType(type);
            reportEntity.setFileName(requestId);

            ArrayList<ReportDataEntity> reportDataEntities = new ArrayList<>();
            ReportDataEntity reportDataEntity = new ReportDataEntity();
            reportDataEntities.add(reportDataEntity);
            reportDataEntity.setHeaderName(numBibsExported);
            reportDataEntity.setHeaderValue(numRecords);
            reportEntity.setReportDataEntities(reportDataEntities);

            ReportDataEntity reportDataEntityReqInst = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityReqInst);
            reportDataEntityReqInst.setHeaderName("RequestingInstitution");
            reportDataEntityReqInst.setHeaderValue(requestingInstitutionCode);

            ReportDataEntity reportDataEntityInstCodes = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityInstCodes);
            reportDataEntityInstCodes.setHeaderName("InstitutionCodes");
            reportDataEntityInstCodes.setHeaderValue(institutionCodes);

            ReportDataEntity reportDataEntityFetchType = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityFetchType);
            reportDataEntityFetchType.setHeaderName("FetchType");
            reportDataEntityFetchType.setHeaderValue(fetchType);

            ReportDataEntity reportDataEntityFromDate = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityFromDate);
            reportDataEntityFromDate.setHeaderName("ExportFromDate");
            reportDataEntityFromDate.setHeaderValue(fromDate.replaceAll("null",""));

            ReportDataEntity reportDataEntityCollecGroupIds = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityCollecGroupIds);
            reportDataEntityCollecGroupIds.setHeaderName("CollectionGroupIds");
            reportDataEntityCollecGroupIds.setHeaderValue(collectionGroupIds);

            ReportDataEntity reportDataEntityTransType = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityTransType);
            reportDataEntityTransType.setHeaderName("TransmissionType");
            reportDataEntityTransType.setHeaderValue(transmissionType);

            ReportDataEntity reportDataEntityExportFormat = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityExportFormat);
            reportDataEntityExportFormat.setHeaderName("ExportFormat");
            reportDataEntityExportFormat.setHeaderValue(exportFormat);

            ReportDataEntity reportDataEntityMailedTo = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityMailedTo);
            reportDataEntityMailedTo.setHeaderName("ToEmailId");
            reportDataEntityMailedTo.setHeaderValue(toEmailId);

            ReportDataEntity reportDataEntityExportedItemCount = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityExportedItemCount);
            reportDataEntityExportedItemCount.setHeaderName(RecapConstants.EXPORTED_ITEM_COUNT);
            reportDataEntityExportedItemCount.setHeaderValue(String.valueOf(exportedItemCount));


        } else {
            reportEntity = byFileName.get(0);
            List<ReportDataEntity> reportDataEntities = reportEntity.getReportDataEntities();
            for (Iterator<ReportDataEntity> iterator = reportDataEntities.iterator(); iterator.hasNext(); ) {
                ReportDataEntity reportDataEntity = iterator.next();
                if (reportDataEntity.getHeaderName().equals(numBibsExported)) {
                    logger.info("Updated bib count-->{}",(Integer.valueOf(reportDataEntity.getHeaderValue()) + Integer.valueOf(numRecords)));
                    reportDataEntity.setHeaderValue(String.valueOf(Integer.valueOf(reportDataEntity.getHeaderValue()) + Integer.valueOf(numRecords)));
                }
                if(reportDataEntity.getHeaderName().equals(RecapConstants.EXPORTED_ITEM_COUNT)){
                    logger.info("Updated item count-->{}",(Integer.valueOf(reportDataEntity.getHeaderValue())+exportedItemCount));
                    reportDataEntity.setHeaderValue(String.valueOf(Integer.valueOf(reportDataEntity.getHeaderValue())+exportedItemCount));
                }
            }
        }

        getReportDetailRepository().save(reportEntity);

        return reportEntity;
    }

    /**
     * This method is invoked by the route to save the failure report entity for data export using the values in Map.
     *
     * @param body the body
     * @return the report entity
     */
    public ReportEntity saveFailureReportEntity(Map body){

        String requestingInstitutionCode = (String) body.get(RecapConstants.REQUESTING_INST_CODE);
        String institutionCodes = (String) body.get(RecapConstants.INSTITUTION_CODES);
        String fetchType = (String) body.get(RecapConstants.FETCH_TYPE);
        String collectionGroupIds = (String) body.get(RecapConstants.COLLECTION_GROUP_IDS);
        String transmissionType = (String) body.get(RecapConstants.TRANSMISSION_TYPE);
        String exportFormat = (String) body.get(RecapConstants.EXPORT_FORMAT);
        String fromDate = body.get(RecapConstants.EXPORT_FROM_DATE) != null ? (String) body.get(RecapConstants.EXPORT_FROM_DATE) :"";
        String toEmailId = (String) body.get(RecapConstants.TO_EMAIL_ID);
        String type = (String) body.get(RecapConstants.BATCH_EXPORT);
        String requestId = (String) (body.get(RecapCommonConstants.REQUEST_ID));
        String failedBibs = (String) body.get(RecapConstants.FAILED_BIBS);
        String numRecords = (String) body.get(RecapConstants.NUM_RECORDS);
        String failureCause = (String) body.get(RecapConstants.FAILURE_CAUSE);
        List<String> failureList = (List<String>) body.get(RecapConstants.FAILURE_LIST);

        List<ReportEntity> byFileName = getReportDetailRepository().findByFileNameAndType(requestId, RecapConstants.BATCH_EXPORT_FAILURE);


        ReportEntity reportEntity;
        if (CollectionUtils.isEmpty(byFileName)) {
            exportFailureToReport(fetchType, requestId, failureList,requestingInstitutionCode);
            reportEntity = new ReportEntity();
            reportEntity.setCreatedDate(new Date());
            reportEntity.setInstitutionName(requestingInstitutionCode);
            reportEntity.setType(type);
            reportEntity.setFileName(requestId);

            ArrayList<ReportDataEntity> reportDataEntities = new ArrayList<>();
            ReportDataEntity reportDataEntity = new ReportDataEntity();
            reportDataEntities.add(reportDataEntity);
            reportDataEntity.setHeaderName(failedBibs);
            reportDataEntity.setHeaderValue(numRecords);
            reportEntity.setReportDataEntities(reportDataEntities);

            ReportDataEntity reportDataEntityReqInst = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityReqInst);
            reportDataEntityReqInst.setHeaderName("RequestingInstitution");
            reportDataEntityReqInst.setHeaderValue(requestingInstitutionCode);

            ReportDataEntity reportDataEntityFailureCause = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityFailureCause);
            reportDataEntityFailureCause.setHeaderName(RecapConstants.FAILURE_CAUSE);
            reportDataEntityFailureCause.setHeaderValue(failureCause);

            ReportDataEntity reportDataEntityInstCodes = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityInstCodes);
            reportDataEntityInstCodes.setHeaderName("InstitutionCodes");
            reportDataEntityInstCodes.setHeaderValue(institutionCodes);

            ReportDataEntity reportDataEntityFetchType = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityFetchType);
            reportDataEntityFetchType.setHeaderName("FetchType");
            reportDataEntityFetchType.setHeaderValue(fetchType);

            ReportDataEntity reportDataEntityFromDate = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityFromDate);
            reportDataEntityFromDate.setHeaderName("ExportFromDate");
            reportDataEntityFromDate.setHeaderValue(fromDate.replaceAll("null",""));

            ReportDataEntity reportDataEntityCollecGroupIds = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityCollecGroupIds);
            reportDataEntityCollecGroupIds.setHeaderName("CollectionGroupIds");
            reportDataEntityCollecGroupIds.setHeaderValue(collectionGroupIds);

            ReportDataEntity reportDataEntityTransType = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityTransType);
            reportDataEntityTransType.setHeaderName("TransmissionType");
            reportDataEntityTransType.setHeaderValue(transmissionType);

            ReportDataEntity reportDataEntityExportFormat = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityExportFormat);
            reportDataEntityExportFormat.setHeaderName("ExportFormat");
            reportDataEntityExportFormat.setHeaderValue(exportFormat);

            ReportDataEntity reportDataEntityMailedTo = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityMailedTo);
            reportDataEntityMailedTo.setHeaderName("ToEmailId");
            reportDataEntityMailedTo.setHeaderValue(toEmailId);

        } else {
            reportEntity = byFileName.get(0);
            List<ReportDataEntity> reportDataEntities = reportEntity.getReportDataEntities();
            for (Iterator<ReportDataEntity> iterator = reportDataEntities.iterator(); iterator.hasNext(); ) {
                ReportDataEntity reportDataEntity = iterator.next();
                if (reportDataEntity.getHeaderName().equals(RecapConstants.FAILED_BIBS)) {
                    Integer exitingRecords = Integer.valueOf(reportDataEntity.getHeaderValue());
                    reportDataEntity.setHeaderValue(String.valueOf(exitingRecords + Integer.valueOf(numRecords)));
                }
            }
            exportFailureToReport(fetchType, requestId, failureList,requestingInstitutionCode);
        }

        getReportDetailRepository().saveAndFlush(reportEntity);

        return reportEntity;
    }

    public void exportFailureToReport(String fetchType, String requestId, List<String> failureList,String requestingInstitutionCode) {
        List<DataExportFailureReport> dataExportFailureReportList = new ArrayList<>();
        String fetchTypeValue = getFetchType(fetchType);
        for (String failure : failureList) {
            populateExportFailureRecords(requestId, dataExportFailureReportList, fetchTypeValue, failure,requestingInstitutionCode);
        }

        producerTemplate.sendBodyAndHeader(RecapConstants.DATADUMP_FAILURE_REPORT_SFTP_Q, dataExportFailureReportList, "FetchType", fetchTypeValue);
    }

    public void populateExportFailureRecords(String requestId, List<DataExportFailureReport> dataExportFailureReportList, String fetchTypeValue, String failure,String requestingInstitutionCode) {
        String split[] = failure.split("\\*");
        DataExportFailureReport dataExportFailureReport = new DataExportFailureReport();
        dataExportFailureReport.setOwningInstitutionBibId(split[0]);
        dataExportFailureReport.setFailureReason(split[1]);
        dataExportFailureReport.setFilename(fetchTypeValue + "-FailureReport-" + requestId);
        dataExportFailureReport.setReportType(fetchTypeValue);
        dataExportFailureReport.setRequestingInstitutionCode(requestingInstitutionCode);
        dataExportFailureReportList.add(dataExportFailureReport);
    }

    public String getFetchType(String fetchTypeNumber) {
        String fetchType ="";
        switch (fetchTypeNumber) {
            case "10":
                fetchType="Full Dump";
                break;
            case "1":
                fetchType= "Incremental";
                break;
            case "2":
                fetchType= "Deleted";
                break;
            default:
                fetchType= "Export";

        }
        return fetchType;
    }

    /**
     * Gets report detail repository.
     *
     * @return ReportDetailRepository
     */
    private ReportDetailRepository getReportDetailRepository() {
        return reportDetailRepository;
    }

    /**
     * Sets report detail repository.
     *
     * @param reportDetailRepository
     */
    public void setReportDetailRepository(ReportDetailRepository reportDetailRepository) {
        this.reportDetailRepository = reportDetailRepository;
    }

}
