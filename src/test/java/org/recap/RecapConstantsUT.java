package org.recap;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RecapConstantsUT extends BaseTestCase{
    RecapConstants outerObject = new RecapConstants();
    RecapConstants.MarcFields innerObject = outerObject.new MarcFields();
    @Test
    public void testConstants() {
        String CF_001= innerObject.CF_001;

        String DATA_DUMP_FILE_NAME = RecapConstants.DATA_DUMP_FILE_NAME;
        String FILENAME = RecapConstants.FILENAME;
        String DATETIME_FOLDER = RecapConstants.DATETIME_FOLDER;
        String REQUESTING_INST_CODE = RecapConstants.REQUESTING_INST_CODE;
        String INSTITUTION_CODES = RecapConstants.INSTITUTION_CODES;
        String TRANSMISSION_TYPE = RecapConstants.TRANSMISSION_TYPE;
        String EXPORT_FORMAT = RecapConstants.EXPORT_FORMAT;
        String TO_EMAIL_ID = RecapConstants.TO_EMAIL_ID;
        String XML_FILE_FORMAT = RecapConstants.XML_FILE_FORMAT;
        String ZIP_FILE_FORMAT = RecapConstants.ZIP_FILE_FORMAT;
        String JSON_FILE_FORMAT = RecapConstants.JSON_FILE_FORMAT;
        String FILE_FORMAT = RecapConstants.FILE_FORMAT;

        String SCSB = "SCSB";


        String INST_NAME = "institutionName";


        String FILE_LOAD_STATUS = "FileLoadStatus";
        String FILE_LOADED = "Loaded";
        String FILE_LOAD_EXCEPTION = "Exception";
        String XML_LOAD = "XMLLoad";
        String CAMEL_EXCHANGE_FILE = "CamelFileExchangeFile";


        String CSV_SUCCESS_Q = "scsbactivemq:queue:csvSuccessQ";
        String CSV_FAILURE_Q = "scsbactivemq:queue:csvFailureQ";
        String FTP_SUCCESS_Q = "scsbactivemq:queue:ftpFailureQ";
        String FTP_FAILURE_Q = "scsbactivemq:queue:ftpSuccessQ";
        String DATADUMP_SUCCESS_REPORT_Q = "scsbactivemq:queue:dataDumpSuccessReportQ";
        String DATADUMP_SUCCESS_REPORT_CSV_Q = "scsbactivemq:queue:dataDumpSuccessReportCsvQ";
        String DATADUMP_FAILURE_REPORT_Q = "scsbactivemq:queue:dataDumpFailureReportQ";
        String DATADUMP_FAILURE_REPORT_CSV_Q = "scsbactivemq:queue:dataDumpFailureReportCsvQ";
        String DATADUMP_SUCCESS_REPORT_FTP_Q = "scsbactivemq:queue:dataDumpSuccessReportFtpQ";
        String DATADUMP_FAILURE_REPORT_FTP_Q = "scsbactivemq:queue:dataDumpFailureReportFtpQ";
        String DATAEXPORT_WITH_SUCCESS_REPORT_FTP_Q = "scsbactivemq:queue:dataExportWithSuccessReportFtpQ";
        String DATAEXPORT_WITH_FAILURE_REPORT_FTP_Q = "scsbactivemq:queue:dataExportWithFailureReportFtpQ";
        String DATADUMP_FILE_SYSTEM_Q = "scsbactivemq:queue:dataDumpFileSystemQ";
        String EMAIL_Q = "scsbactivemq:queue:emailQ";
        String DATADUMP_ZIPFILE_FTP_Q = "scsbactivemq:queue:zipFileFtpQ";
        String DATADUMP_HTTP_Q = "scsbactivemq:queue:dataExportHttpQ";
        String DATADUMP_IS_RECORD_AVAILABLE_Q = "scsbactivemq:queue:dataExportIsRecordAvailableQ";


        String DATA_DUMP_COMPLETION_ROUTE_ID = "dataDumpCompletionFromQRoute";
        String DATA_DUMP_COMPLETION_TOPIC_STATUS_PUL_ROUTE_ID = "pulExportProcessCompletionRouteId";
        String DATA_DUMP_COMPLETION_TOPIC_STATUS_CUL_ROUTE_ID = "culExportProcessCompletionRouteId";
        String DATA_DUMP_COMPLETION_TOPIC_STATUS_NYPL_ROUTE_ID = "nyplExportProcessCompletionRouteId";

        String DATA_DUMP_COMPLETION_FROM = "scsbactivemq:queue:dataDumpCompletionFromQ";
        String DATA_DUMP_COMPLETION_TO = "scsbactivemq:queue:dataDumpCompletionToQ";
        String DATA_DUMP_COMPLETION_LOG = "Scheduled ongoing data export queue consumed";
        String DATA_DUMP_COMPLETION_TOPIC_STATUS_CUL = "scsbactivemq:topic:CUL.ExportProcessCompletion";
        String DATA_DUMP_COMPLETION_TOPIC_STATUS_PUL = "scsbactivemq:topic:PUL.ExportProcessCompletion";
        String DATA_DUMP_COMPLETION_TOPIC_STATUS_NYPL = "scsbactivemq:topic:NYPL.ExportProcessCompletion";

        String DATA_DUMP_COMPLETION_TOPIC_MESSAGE = "Incremental datadump is completed";


        String CSV_SUCCESS_ROUTE_ID = "csvSuccessQ";
        String CSV_FAILURE_ROUTE_ID = "csvFailureQ";
        String FTP_SUCCESS_ROUTE_ID = "ftpFailureQ";
        String FTP_FAILURE_ROUTE_ID = "ftpSuccessQ";
        String EMAIL_ROUTE_ID = "emailQ";
        String DATADUMP_ZIP_FILESYSTEM_ROUTE_ID = "zipDataDumpQ";
        String DATADUMP_ZIPFTP_ROUTE_ID = "zipDataDumpRoute";

        String DATE_FORMAT_FOR_REPORT_NAME = "yyyyMMdd_HHmmss";
        String DATE_FORMAT_FROM_API = "ddMMMyyyyHHmm";


        String OPERATION_TYPE_ETL = "ETL";


        String EXCEPTION_MESSAGE = "ExceptionMessage";


        String TOTAL_RECORDS_IN_FILE = "TotalRecordsInFile";
        String TOTAL_BIBS_LOADED = "TotalBibsLoaded";
        String TOTAL_HOLDINGS_LOADED = "TotalHoldingsLoaded";
        String TOTAL_BIB_HOLDINGS_LOADED = "TotalBibHoldingsLoaded";
        String TOTAL_ITEMS_LOADED = "TotalItemsLoaded";
        String TOTAL_BIB_ITEMS_LOADED = "TotalBibItemsLoaded";


        String REPORT_TYPE = "reportType";
        String DIRECTORY_NAME = "directoryName";


        String DATE_FORMAT_MMDDYYY = "MM-dd-yyyy";
        String DATE_FORMAT_YYYYMMDD = "yyyy-MM-dd";
        String DATE_FORMAT_DDMMMYYYYHHMM = "ddMMMyyyyHHmm";


        String DATADUMP_EXPORT_FAILURE = "Data dump export failed, please check with the support team.";
        String DATADUMP_NO_RECORD = "There is no data to export.";
        String DATADUMP_PROCESS_STARTED = "Export process has started and we will send an email notification upon completion";
        String DATADUMP_RECORDS_AVAILABLE_FOR_PROCESS = "Data available to export";
        String DATADUMP_INSTITUTIONCODE_ERR_MSG = "Please enter the value for InstitutionCode parameter.";
        String DATADUMP_VALID_INST_CODES_ERR_MSG = "Please enter a valid InstitutionCode: PUL, CUL or NYPL.";
        String DATADUMP_MULTIPLE_INST_CODES_ERR_MSG = "Please enter only one InstitutionCode: PUL, CUL or NYPL";
        String DATADUMP_DATE_ERR_MSG = "Please enter the date";
        String DATADUMP_TRANS_TYPE_ERR_MSG = "Please enter valid transmission type.";
        String DATADUMP_VALID_FETCHTYPE_ERR_MSG = "Please enter valid fetchType either 1 or 2 or configured value for full dump.";
        String DATADUMP_VALID_REQ_INST_CODE_ERR_MSG = "Please enter valid institution code CUL or PUL or NYPL for requestingInstitutionCode .";
        String DATADUMP_FULL_VALID_TRANS_TYPE = "Transmission type 1 is not valid for full dump. Use transmission type 0 or 2";
        String DATADUMP_HTTP_REPONSE_RECORD_LIMIT_ERR_MSG = "There are more than 100 records. Use transmission type ftp to dump the data";
        String DATADUMP_EMAIL_TO_ADDRESS_REQUIRED = "Please enter a valid email address";
        String INVALID_EMAIL_ADDRESS = "Email address is invalid.";
        String INVALID_DATE_FORMAT = "Please enter the date in \"{0}\" format.";
        String DATADUMP_FETCHTYPE_FULL = "0";
        String DATADUMP_FETCHTYPE_INCREMENTAL = "1";
        String DATADUMP_FETCHTYPE_DELETED = "2";
        String DATADUMP_TRANSMISSION_TYPE_FTP = "0";
        String DATADUMP_TRANSMISSION_TYPE_HTTP = "1";
        String DATADUMP_TRANSMISSION_TYPE_FILESYSTEM = "2";
        String DATADUMP_XML_FORMAT_MARC = "0";
        String DATADUMP_XML_FORMAT_SCSB = "1";
        String DATADUMP_DELETED_JSON_FORMAT = "2";
        Integer IS_NOT_DELETED = 0;
        String DATADUMP_SUCCESSLIST = "successList";
        String DATADUMP_FAILURELIST = "failureList";
        String DATADUMP_FORMATTEDSTRING = "formattedString";
        String DATADUMP_FORMATERROR = "formatError";
        String DATADUMP_EMAILBODY_FOR = "emailBodyFor";
        String DATADUMP_DATA_AVAILABLE = "dataAvailable";
        String DATADUMP_NO_DATA_AVAILABLE = "dataNotAvailable";
        String COMPLETED = "Completed";
        String IN_PROGRESS = "InProgress";
        String FULLDUMP_INPROGRESS_ERR_MSG = "Can't run data export now, already full data export in progress, wait until it completes.";
        String INCREMENTAL_DATE_LIMIT_EMPTY_ERR_MSG = "The incremental Date limit is missing. Please contact HTC Support {0} for assistance.";
        String INITIAL_DATA_LOAD_DATE_MISSING_ERR_MSG = "The initial data load Date is missing for the institution {0}. Please contact HTC Support {1} for assistance.";
        String DATADUMP_DAYS_LIMIT_EXCEEDED_ERROR_MSG = "The date used for incremental data dump cannot be older than {0} days from the date of request. Please contact HTC Support {1} for assistance.";
        String RESTRICT_FULLDUMP_VIA_INCREMENTAL_ERROR_MSG = "The date used for incremental data dump precedes (or) is the date on which records for the institution {0} were created. Kindly use a later date or contact HTC Support {1} for assistance.";

        String COLLECTION_GROUP_SHARED = "Shared";
        String COLLECTION_GROUP_OPEN = "Open";
        String COLLECTION_GROUP_PRIVATE = "Private";

        String SOLR_INPUT_FOR_DATA_EXPORT_Q = "scsbactivemq:queue:SolrInputForDataExportQ";
        String BIB_ENTITY_FOR_DATA_EXPORT_Q = "scsbactivemq:queue:BibEntityForDataExportQ";
        String MARC_RECORD_FOR_DATA_EXPORT_Q = "scsbactivemq:queue:MarcRecordForDataExportQ";
        String SCSB_RECORD_FOR_DATA_EXPORT_Q = "scsbactivemq:queue:SCSBRecordForDataExportQ";
        String DELETED_JSON_RECORD_FOR_DATA_EXPORT_Q = "scsbactivemq:queue:DeletedJsonRecordForDataExportQ";
        String DATADUMP_STAGING_Q = "scsbactivemq:queue:dataExportStagingQ";

        String SOLR_INPUT_DATA_EXPORT_ROUTE_ID = "solrInputDataExportRouteId";
        String BIB_ENTITY_DATA_EXPORT_ROUTE_ID = "bibEntityDataExportRouteId";
        String MARC_RECORD_DATA_EXPORT_ROUTE_ID = "marcRecordDataExportRouteId";
        String SCSB_RECORD_DATA_EXPORT_ROUTE_ID = "scsbRecordDataExportRouteId";
        String DELETED_JSON_RECORD_DATA_EXPORT_ROUTE_ID = "deletedJsonRecordDataExportRouteId";
        String DATADUMP_STAGING_ROUTE_ID = "dataExportStagingRouteId";

        String NUM_RECORDS = "Num Records";
        String NUM_BIBS_EXPORTED = "NoOfBibsExported";
        String EXPORTED_ITEM_COUNT = "ExportedItemCount";
        String BATCH_EXPORT = "BatchExport";
        String BATCH_EXPORT_SUCCESS = "BatchExportSuccess";
        String BATCH_EXPORT_FAILURE = "BatchExportFailure";
        String FAILURE_CAUSE = "FailureCause";
        String FAILED_BIBS = "FailedBibs";
        String COLLECTION_GROUP_IDS = "collectionGroupIds";
        String FETCH_TYPE = "fetchType";
        String HEADER_FETCH_TYPE = "FetchType";
        String EXPORT_DATA_DUMP_INCREMENTAL = "ExportDataDump_Incremental_";
        String EXPORT_DATA_DUMP_DELETIONS = "ExportDataDump_Deletions_";
        String EXPORT_DATA_DUMP_FULL = "ExportDataDump_Full_";
        String EXPORT_FROM_DATE = "exportFromDate";

        String DATADUMP_SUCCESS_REPORT_CSV_ROUTE_ID = "dataDumpSucccessReportCsvRouteId";
        String DATADUMP_FAILURE_REPORT_CSV_ROUTE_ID = "dataDumpFailureReportCsvRouteId";
        String DATADUMP_SUCCESS_REPORT_FTP_ROUTE_ID = "dataDumpSuccessReportFtpRouteId";
        String DATADUMP_FAILURE_REPORT_FTP_ROUTE_ID = "dataDumpFailureReportFtpRouteId";
        String DATAEXPORT_WITH_SUCCESS_REPORT_FTP_ROUTE_ID = "dataExportWithSuccessReportFtpRouteId";
        String DATAEXPORT_WITH_FAILURE_REPORT_FTP_ROUTE_ID = "dataExportWithFailureReportFtpRouteId";
        String DATADUMP_SUCCESS_REPORT_ROUTE_ID = "dataExportSuccessReportRouteId";
        String DATADUMP_FAILURE_REPORT_ROUTE_ID = "dataExportFailureReportRouteId";

        String COMPLETE_STATUS = "Complete";

        String EXCEPTION = "Exception->";

        String BATCH_HEADERS = "batchHeaders";
        String PROCESS_RECORDS = "processRecords";
        String FTP_ROUTE = "ftpRoute";


        String EMAIL_INCREMENTAL_DATA_DUMP = "Incremental_Data_Dump";
        String EMAIL_DELETION_DATA_DUMP = "Deletion_Data_Dump";
        String SUBJECT_INCREMENTAL_DATA_DUMP = "Export Data Dump Report - Incremental";
        String SUBJECT_DELETION_DATA_DUMP = "Deleted Records Report";


        String ERROR = "error-->";
        String ITEM_EXPORTED_COUNT = "itemExportedCount";


        String API_KEY = "api_key";
        String DELETED_DATA_DUMP_COMPLETION_TOPIC_MESSAGE = "Deleted datadump is completed";
        String FULL_DATA_DUMP_COMPLETION_TOPIC_MESSAGE = "Full datadump is completed";
        String DATA_DUMP_TYPE = "dataDumpType";
        String MESSAGE = "message";
        String EXPORTED_DATE = "exportedDate";


        boolean  EXPORT_SCHEDULER_CALL =RecapConstants.EXPORT_SCHEDULER_CALL;
        String EXPORT_DATE_SCHEDULER = RecapConstants.EXPORT_DATE_SCHEDULER;
        String EXPORT_FETCH_TYPE_INSTITUTION = RecapConstants.EXPORT_FETCH_TYPE_INSTITUTION;

        String INCREMENTAL = RecapConstants.INCREMENTAL;
        String DELETED = RecapConstants.DELETED;
        String EXPORT_INCREMENTAL_PUL = RecapConstants.EXPORT_INCREMENTAL_PUL;
        String EXPORT_INCREMENTAL_CUL = RecapConstants.EXPORT_INCREMENTAL_CUL;
        String EXPORT_INCREMENTAL_NYPL = RecapConstants.EXPORT_INCREMENTAL_NYPL;
        String EXPORT_DELETED_PUL = RecapConstants.EXPORT_DELETED_PUL;
        String EXPORT_DELETED_CUL = RecapConstants.EXPORT_DELETED_CUL;
        String EXPORT_DELETED_NYPL = RecapConstants.EXPORT_DELETED_NYPL;

        String EXPORT_TYPE_FULL = RecapConstants.EXPORT_TYPE_FULL;

        String ETL_DATA_LOAD_NAMESPACE = RecapConstants.ETL_DATA_LOAD_NAMESPACE;
        assertTrue(true);
    }
}
