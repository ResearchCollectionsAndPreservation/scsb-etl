package org.recap.report;

import org.apache.camel.component.file.GenericFile;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.engine.DefaultFluentProducerTemplate;
import org.apache.commons.io.FilenameUtils;
import org.recap.RecapCommonConstants;
import org.recap.model.csv.DataDumpFailureReport;
import org.recap.model.csv.ReCAPCSVFailureRecord;
import org.recap.model.csv.ReCAPCSVSuccessRecord;
import org.recap.model.csv.SuccessReportReCAPCSVRecord;
import org.recap.model.csv.FailureReportReCAPCSVRecord;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.ReportDetailRepository;
import org.recap.util.ReCAPCSVFailureRecordGenerator;
import org.recap.util.ReCAPCSVSuccessRecordGenerator;
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
            ReCAPCSVSuccessRecord reCAPCSVSuccessRecord = new ReCAPCSVSuccessRecord();
            List<SuccessReportReCAPCSVRecord> successReportReCAPCSVRecords = new ArrayList<>();
            for (ReportEntity reportEntity : reportEntities) {
                SuccessReportReCAPCSVRecord successReportReCAPCSVRecord = new ReCAPCSVSuccessRecordGenerator().prepareSuccessReportReCAPCSVRecord(reportEntity);
                successReportReCAPCSVRecords.add(successReportReCAPCSVRecord);
            }
            ReportEntity reportEntity = reportEntities.get(0);
            reCAPCSVSuccessRecord.setReportType(reportEntity.getType());
            reCAPCSVSuccessRecord.setInstitutionName(reportEntity.getInstitutionName());
            reCAPCSVSuccessRecord.setReportFileName(fileName);
            reCAPCSVSuccessRecord.setSuccessReportReCAPCSVRecordList(successReportReCAPCSVRecords);
            producerTemplate.sendBody(reportQueue, reCAPCSVSuccessRecord);
            DateFormat df = new SimpleDateFormat(RecapCommonConstants.DATE_FORMAT_FOR_FILE_NAME);
            return FilenameUtils.removeExtension(reCAPCSVSuccessRecord.getReportFileName()) + "-" + reCAPCSVSuccessRecord.getReportType() + "-" + df.format(new Date()) + ".csv";
        }
        return null;
    }

    public void processRecordFailures(Exchange exchange, List failures, String batchHeaders, String requestId, DataExportHeaderUtil dataExportHeaderUtil) {
        Map values = getValues(batchHeaders, dataExportHeaderUtil);
        values.put(RecapConstants.NUM_RECORDS, String.valueOf(failures.size()));
        values.put(RecapConstants.FAILURE_CAUSE, (String) failures.get(0));
        values.put(RecapConstants.FAILED_BIBS, RecapConstants.FAILED_BIBS);
        values.put(RecapConstants.BATCH_EXPORT, RecapConstants.BATCH_EXPORT_FAILURE);
        values.put(RecapCommonConstants.REQUEST_ID, requestId);
        values.put(RecapConstants.FAILURE_LIST, failures);

        FluentProducerTemplate fluentProducerTemplate = generateFluentProducerTemplate(exchange, values, RecapConstants.DATADUMP_FAILURE_REPORT_Q);
        fluentProducerTemplate.send();
    }

    public void processSuccessReport(Exchange exchange, Integer size, String batchHeaders, String requestId, DataExportHeaderUtil dataExportHeaderUtil) {
        HashMap<String, String> values = getValues(batchHeaders, dataExportHeaderUtil);
        values.put(RecapConstants.NUM_RECORDS, String.valueOf(size));
        setValues(values, exchange, requestId);
        FluentProducerTemplate fluentProducerTemplate = generateFluentProducerTemplate(exchange, values, RecapConstants.DATADUMP_SUCCESS_REPORT_Q);
        fluentProducerTemplate.send();
    }

    private HashMap<String, String> getValues(String batchHeaders, DataExportHeaderUtil dataExportHeaderUtil) {
        HashMap<String, String> values = new HashMap<>();
        values.put(RecapConstants.REQUESTING_INST_CODE, dataExportHeaderUtil.getValueFor(batchHeaders, RecapConstants.REQUESTING_INST_CODE));
        values.put(RecapConstants.INSTITUTION_CODES, dataExportHeaderUtil.getValueFor(batchHeaders, RecapConstants.INSTITUTION_CODES));
        values.put(RecapConstants.FETCH_TYPE, dataExportHeaderUtil.getValueFor(batchHeaders, RecapConstants.FETCH_TYPE));
        values.put(RecapConstants.COLLECTION_GROUP_IDS, dataExportHeaderUtil.getValueFor(batchHeaders, RecapConstants.COLLECTION_GROUP_IDS));
        values.put(RecapConstants.TRANSMISSION_TYPE, dataExportHeaderUtil.getValueFor(batchHeaders, RecapConstants.TRANSMISSION_TYPE));
        values.put(RecapConstants.EXPORT_FROM_DATE, dataExportHeaderUtil.getValueFor(batchHeaders, RecapConstants.EXPORT_FROM_DATE));
        values.put(RecapConstants.EXPORT_FORMAT, dataExportHeaderUtil.getValueFor(batchHeaders, RecapConstants.EXPORT_FORMAT));
        values.put(RecapConstants.TO_EMAIL_ID, dataExportHeaderUtil.getValueFor(batchHeaders, RecapConstants.TO_EMAIL_ID));
        return values;
    }

    public String generateFailureReport(List<ReportEntity> reportEntities, String fileName, String reportQueue) {
        if (!CollectionUtils.isEmpty(reportEntities)) {
            ReCAPCSVFailureRecord reCAPCSVFailureRecord = new ReCAPCSVFailureRecord();
            List<FailureReportReCAPCSVRecord> failureReportReCAPCSVRecords = new ArrayList<>();
            for (ReportEntity reportEntity : reportEntities) {
                FailureReportReCAPCSVRecord failureReportReCAPCSVRecord = new ReCAPCSVFailureRecordGenerator().prepareFailureReportReCAPCSVRecord(reportEntity);
                failureReportReCAPCSVRecords.add(failureReportReCAPCSVRecord);
            }
            ReportEntity reportEntity = reportEntities.get(0);
            reCAPCSVFailureRecord.setReportType(reportEntity.getType());
            reCAPCSVFailureRecord.setInstitutionName(reportEntity.getInstitutionName());
            reCAPCSVFailureRecord.setFileName(fileName);
            reCAPCSVFailureRecord.setFailureReportReCAPCSVRecordList(failureReportReCAPCSVRecords);
            producerTemplate.sendBody(reportQueue, reCAPCSVFailureRecord);
            DateFormat df = new SimpleDateFormat(RecapCommonConstants.DATE_FORMAT_FOR_FILE_NAME);
            return FilenameUtils.removeExtension(reCAPCSVFailureRecord.getFileName()) + "-" + reCAPCSVFailureRecord.getReportType() + "-" + df.format(new Date()) + ".csv";
        }
        return null;
    }

    public HashMap<String, String> processReport(String batchHeaders, String requestId, DataExportHeaderUtil dataExportHeaderUtil) {
        HashMap<String, String> values = getValues(batchHeaders, dataExportHeaderUtil);
        values.put(RecapConstants.FAILED_BIBS, RecapConstants.FAILED_BIBS);
        values.put(RecapConstants.BATCH_EXPORT, RecapConstants.BATCH_EXPORT_FAILURE);
        values.put(RecapCommonConstants.REQUEST_ID, requestId);
        return values;
    }

    public FluentProducerTemplate generateFluentProducerTemplate(Exchange exchange, Map values, String reportQ) {
        FluentProducerTemplate fluentProducerTemplate = DefaultFluentProducerTemplate.on(exchange.getContext());
        fluentProducerTemplate
                .to(reportQ)
                .withBody(values)
                .withHeader(RecapConstants.BATCH_HEADERS, exchange.getIn().getHeader(RecapConstants.BATCH_HEADERS))
                .withHeader(RecapConstants.EXPORT_FORMAT, exchange.getIn().getHeader(RecapConstants.EXPORT_FORMAT))
                .withHeader(RecapConstants.TRANSMISSION_TYPE, exchange.getIn().getHeader(RecapConstants.TRANSMISSION_TYPE));
        return fluentProducerTemplate;
    }

    public void process(Exchange exchange, String headerValue, ReportDetailRepository reportDetailRepository) {
        String institutionName = (String) exchange.getProperty(RecapConstants.INST_NAME);
        if (StringUtils.isNotEmpty(institutionName)) {
            ReportEntity reportEntity = new ReportEntity();
            reportEntity.setCreatedDate(new Date());
            GenericFile<Object> camelFileExchangeFile = (GenericFile) exchange.getProperty(RecapConstants.CAMEL_EXCHANGE_FILE);
            reportEntity.setFileName(camelFileExchangeFile.getFileName());
            reportEntity.setType(RecapConstants.XML_LOAD);
            reportEntity.setInstitutionName(institutionName);

            ReportDataEntity reportDataEntity = new ReportDataEntity();
            reportDataEntity.setHeaderName(RecapConstants.FILE_LOAD_STATUS);
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
        values.put(RecapConstants.NUM_BIBS_EXPORTED, RecapConstants.NUM_BIBS_EXPORTED);
        values.put(RecapConstants.BATCH_EXPORT, RecapConstants.BATCH_EXPORT_SUCCESS);
        values.put(RecapCommonConstants.REQUEST_ID, requestId);
        values.put(RecapConstants.ITEM_EXPORTED_COUNT,exchange.getIn().getHeader(RecapConstants.ITEM_EXPORTED_COUNT));
    }
}
