package org.recap.report;

import org.apache.camel.component.file.GenericFile;
import org.apache.commons.lang3.StringUtils;
import org.recap.ScsbConstants;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.engine.DefaultFluentProducerTemplate;
import org.apache.commons.io.FilenameUtils;
import org.recap.ScsbCommonConstants;
import org.recap.model.csv.DataDumpFailureReport;
import org.recap.model.csv.SCSBCSVFailureRecord;
import org.recap.model.csv.SCSBCSVSuccessRecord;
import org.recap.model.csv.SuccessReportSCSBCSVRecord;
import org.recap.model.csv.FailureReportSCSBCSVRecord;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;
import org.recap.repositoryrw.ReportDetailRepository;
import org.recap.util.SCSBCSVFailureRecordGenerator;
import org.recap.util.SCSBCSVSuccessRecordGenerator;
import org.recap.util.datadump.DataDumpFailureReportGenerator;
import org.recap.util.datadump.DataExportHeaderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonReportGenerator {

    /**
     * The Producer template.
     */
    @Autowired
    ProducerTemplate producerTemplate;

    /**
     * Generates CSV report with success records for initial data load.
     *
     * @param reportEntities the report entities
     * @param fileName       the file name
     * @return the file name
     */
    public String generateSuccessReport(List<ReportEntity> reportEntities, String fileName, String reportQueue) {

        if (!CollectionUtils.isEmpty(reportEntities)) {
            SCSBCSVSuccessRecord SCSBCSVSuccessRecord = new SCSBCSVSuccessRecord();
            List<SuccessReportSCSBCSVRecord> successReportSCSBCSVRecords = new ArrayList<>();
            for (ReportEntity reportEntity : reportEntities) {
                SuccessReportSCSBCSVRecord successReportSCSBCSVRecord = new SCSBCSVSuccessRecordGenerator().prepareSuccessReportReCAPCSVRecord(reportEntity);
                successReportSCSBCSVRecords.add(successReportSCSBCSVRecord);
            }
            ReportEntity reportEntity = reportEntities.get(0);
            SCSBCSVSuccessRecord.setReportType(reportEntity.getType());
            SCSBCSVSuccessRecord.setInstitutionName(reportEntity.getInstitutionName());
            SCSBCSVSuccessRecord.setReportFileName(fileName);
            SCSBCSVSuccessRecord.setSuccessReportSCSBCSVRecordList(successReportSCSBCSVRecords);
            producerTemplate.sendBody(reportQueue, SCSBCSVSuccessRecord);
            DateFormat df = new SimpleDateFormat(ScsbCommonConstants.DATE_FORMAT_FOR_FILE_NAME);
            return FilenameUtils.removeExtension(SCSBCSVSuccessRecord.getReportFileName()) + "-" + SCSBCSVSuccessRecord.getReportType() + "-" + df.format(new Date()) + ".csv";
        }
        return null;
    }

    public void processRecordFailures(Exchange exchange, List failures, String batchHeaders, String requestId, DataExportHeaderUtil dataExportHeaderUtil) {
        Map values = getValues(batchHeaders, dataExportHeaderUtil);
        values.put(ScsbConstants.NUM_RECORDS, String.valueOf(failures.size()));
        values.put(ScsbConstants.FAILURE_CAUSE, (String) failures.get(0));
        values.put(ScsbConstants.FAILED_BIBS, ScsbConstants.FAILED_BIBS);
        values.put(ScsbConstants.BATCH_EXPORT, ScsbConstants.BATCH_EXPORT_FAILURE);
        values.put(ScsbCommonConstants.REQUEST_ID, requestId);
        values.put(ScsbConstants.FAILURE_LIST, failures);

        FluentProducerTemplate fluentProducerTemplate = generateFluentProducerTemplate(exchange, values, ScsbConstants.DATADUMP_FAILURE_REPORT_Q);
        fluentProducerTemplate.send();
    }

    public void processSuccessReport(Exchange exchange, Integer size, String batchHeaders, String requestId, DataExportHeaderUtil dataExportHeaderUtil) {
        HashMap<String, String> values = getValues(batchHeaders, dataExportHeaderUtil);
        values.put(ScsbConstants.NUM_RECORDS, String.valueOf(size));
        setValues(values, exchange, requestId);
        FluentProducerTemplate fluentProducerTemplate = generateFluentProducerTemplate(exchange, values, ScsbConstants.DATADUMP_SUCCESS_REPORT_Q);
        fluentProducerTemplate.send();
    }

    private static HashMap<String, String> getValues(String batchHeaders, DataExportHeaderUtil dataExportHeaderUtil) {
        HashMap<String, String> values = new HashMap<>();
        values.put(ScsbConstants.REQUESTING_INST_CODE, dataExportHeaderUtil.getValueFor(batchHeaders, ScsbConstants.REQUESTING_INST_CODE));
        values.put(ScsbConstants.INSTITUTION_CODES, dataExportHeaderUtil.getValueFor(batchHeaders, ScsbConstants.INSTITUTION_CODES));
        values.put(ScsbConstants.FETCH_TYPE, dataExportHeaderUtil.getValueFor(batchHeaders, ScsbConstants.FETCH_TYPE));
        values.put(ScsbConstants.COLLECTION_GROUP_IDS, dataExportHeaderUtil.getValueFor(batchHeaders, ScsbConstants.COLLECTION_GROUP_IDS));
        values.put(ScsbConstants.TRANSMISSION_TYPE, dataExportHeaderUtil.getValueFor(batchHeaders, ScsbConstants.TRANSMISSION_TYPE));
        values.put(ScsbConstants.EXPORT_FROM_DATE, dataExportHeaderUtil.getValueFor(batchHeaders, ScsbConstants.EXPORT_FROM_DATE));
        values.put(ScsbConstants.EXPORT_FORMAT, dataExportHeaderUtil.getValueFor(batchHeaders, ScsbConstants.EXPORT_FORMAT));
        values.put(ScsbConstants.TO_EMAIL_ID, dataExportHeaderUtil.getValueFor(batchHeaders, ScsbConstants.TO_EMAIL_ID));
        values.put(ScsbConstants.IMS_DEPOSITORY, dataExportHeaderUtil.getValueFor(batchHeaders, ScsbConstants.IMS_DEPOSITORY));
        return values;
    }

    public String generateFailureReport(List<ReportEntity> reportEntities, String fileName, String reportQueue) {
        if (!CollectionUtils.isEmpty(reportEntities)) {
            SCSBCSVFailureRecord SCSBCSVFailureRecord = new SCSBCSVFailureRecord();
            List<FailureReportSCSBCSVRecord> failureReportSCSBCSVRecords = new ArrayList<>();
            for (ReportEntity reportEntity : reportEntities) {
                FailureReportSCSBCSVRecord failureReportSCSBCSVRecord = new SCSBCSVFailureRecordGenerator().prepareFailureReportReCAPCSVRecord(reportEntity);
                failureReportSCSBCSVRecords.add(failureReportSCSBCSVRecord);
            }
            ReportEntity reportEntity = reportEntities.get(0);
            SCSBCSVFailureRecord.setReportType(reportEntity.getType());
            SCSBCSVFailureRecord.setInstitutionName(reportEntity.getInstitutionName());
            SCSBCSVFailureRecord.setFileName(fileName);
            SCSBCSVFailureRecord.setFailureReportSCSBCSVRecordList(failureReportSCSBCSVRecords);
            producerTemplate.sendBody(reportQueue, SCSBCSVFailureRecord);
            DateFormat df = new SimpleDateFormat(ScsbCommonConstants.DATE_FORMAT_FOR_FILE_NAME);
            return FilenameUtils.removeExtension(SCSBCSVFailureRecord.getFileName()) + "-" + SCSBCSVFailureRecord.getReportType() + "-" + df.format(new Date()) + ".csv";
        }
        return null;
    }

    public HashMap<String, String> processReport(String batchHeaders, String requestId, DataExportHeaderUtil dataExportHeaderUtil) {
        HashMap<String, String> values = getValues(batchHeaders, dataExportHeaderUtil);
        values.put(ScsbConstants.FAILED_BIBS, ScsbConstants.FAILED_BIBS);
        values.put(ScsbConstants.BATCH_EXPORT, ScsbConstants.BATCH_EXPORT_FAILURE);
        values.put(ScsbCommonConstants.REQUEST_ID, requestId);
        return values;
    }

    public FluentProducerTemplate generateFluentProducerTemplate(Exchange exchange, Map values, String reportQ) {
        FluentProducerTemplate fluentProducerTemplate = DefaultFluentProducerTemplate.on(exchange.getContext());
        fluentProducerTemplate
                .to(reportQ)
                .withBody(values)
                .withHeader(ScsbConstants.BATCH_HEADERS, exchange.getIn().getHeader(ScsbConstants.BATCH_HEADERS))
                .withHeader(ScsbConstants.EXPORT_FORMAT, exchange.getIn().getHeader(ScsbConstants.EXPORT_FORMAT))
                .withHeader(ScsbConstants.TRANSMISSION_TYPE, exchange.getIn().getHeader(ScsbConstants.TRANSMISSION_TYPE));
        return fluentProducerTemplate;
    }

    public void process(Exchange exchange, String headerValue, ReportDetailRepository reportDetailRepository) {
        String institutionName = (String) exchange.getProperty(ScsbConstants.INST_NAME);
        if (StringUtils.isNotEmpty(institutionName)) {
            ReportEntity reportEntity = new ReportEntity();
            reportEntity.setCreatedDate(new Date());
            GenericFile<Object> camelFileExchangeFile = (GenericFile) exchange.getProperty(ScsbConstants.CAMEL_EXCHANGE_FILE);
            reportEntity.setFileName(camelFileExchangeFile.getFileName());
            reportEntity.setType(ScsbConstants.XML_LOAD);
            reportEntity.setInstitutionName(institutionName);

            ReportDataEntity reportDataEntity = new ReportDataEntity();
            reportDataEntity.setHeaderName(ScsbConstants.FILE_LOAD_STATUS);
            reportDataEntity.setHeaderValue(headerValue);

            reportEntity.setReportDataEntities(Arrays.asList(reportDataEntity));

            reportDetailRepository.save(reportEntity);
        }
    }

   protected  DataDumpFailureReport generateDataDumpFailureReport(List<ReportEntity> reportEntities, String fileName) {
       DataDumpFailureReport dataDumpFailureReport = new DataDumpFailureReport();
       List<DataDumpFailureReport> dataDumpFailureReportList = new ArrayList<>();
       for (ReportEntity reportEntity : reportEntities) {
           DataDumpFailureReport dataDumpFailureReportRecord = new DataDumpFailureReportGenerator().prepareDataDumpCSVFailureRecord(reportEntity);
           dataDumpFailureReportList.add(dataDumpFailureReportRecord);
       }
       ReportEntity reportEntity = reportEntities.get(0);
       dataDumpFailureReport.setReportType(reportEntity.getType());
       dataDumpFailureReport.setInstitutionName(reportEntity.getInstitutionName());
       dataDumpFailureReport.setFileName(fileName);
       dataDumpFailureReport.setDataDumpFailureReportRecordList(dataDumpFailureReportList);
       return dataDumpFailureReport;
   }

    protected void setValues(HashMap values, Exchange exchange, String requestId)
    {
        values.put(ScsbConstants.NUM_BIBS_EXPORTED, ScsbConstants.NUM_BIBS_EXPORTED);
        values.put(ScsbConstants.BATCH_EXPORT, ScsbConstants.BATCH_EXPORT_SUCCESS);
        values.put(ScsbCommonConstants.REQUEST_ID, requestId);
        values.put(ScsbConstants.ITEM_EXPORTED_COUNT,exchange.getIn().getHeader(ScsbConstants.ITEM_EXPORTED_COUNT));
    }
}
