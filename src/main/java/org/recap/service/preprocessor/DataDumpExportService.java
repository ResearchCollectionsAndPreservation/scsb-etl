package org.recap.service.preprocessor;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.export.DataDumpRequest;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.repository.CollectionGroupDetailsRepository;
import org.recap.service.email.datadump.DataDumpEmailService;
import org.recap.service.executor.datadump.DataDumpExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by premkb on 27/9/16.
 */
@Service
public class DataDumpExportService {

    private static final Logger logger = LoggerFactory.getLogger(DataDumpExportService.class);

    /**
     * The App context.
     */
    @Autowired
    ApplicationContext appContext;

    @Autowired
    private CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Autowired
    private DataDumpExecutorService dataDumpExecutorService;

    @Autowired
    private DataDumpEmailService dataDumpEmailService;

    @Autowired
    private ConsumerTemplate consumerTemplate;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Value("${datadump.status.file.name}")
    private String dataDumpStatusFileName;

    @Value("${datadump.fetchtype.full}")
    private String fetchTypeFull;

    @Value("${datadump.incremental.date.limit}")
    private String incrementalDateLimit;

    @Value("${recap.assist.email.to}")
    private String recapAssistEmailAddress;

    @Value("${etl.pul.data.loaded.date}")
    private String pulInitialDataLoadedDate;

    @Value("${etl.cul.data.loaded.date}")
    private String culInitialDataLoadedDate;

    @Value("${etl.nypl.data.loaded.date}")
    private String nyplInitialDataLoadedDate;

    /**
     * Start the data dump process.
     *
     * @param dataDumpRequest the data dump request
     * @return the response message
     */
    public String startDataDumpProcess(DataDumpRequest dataDumpRequest) {
        String outputString = null;
        String responseMessage = null;
        try {
            new Thread(() -> {
                try {
                    dataDumpExecutorService.generateDataDump(dataDumpRequest);
                } catch (Exception e) {
                    logger.error(RecapConstants.ERROR,e);
                }
            }).start();

            if(dataDumpRequest.getTransmissionType().equals(RecapConstants.DATADUMP_TRANSMISSION_TYPE_HTTP)){
                String message = getMessageFromIsRecordAvailableQ();
                if (message.equals(RecapConstants.DATADUMP_RECORDS_AVAILABLE_FOR_PROCESS)) {
                    outputString = getMessageFromHttpQ();
                } else{
                    outputString = message;
                }
            }else{
                outputString = getMessageFromIsRecordAvailableQ();
                if(!outputString.equals(RecapConstants.DATADUMP_RECORDS_AVAILABLE_FOR_PROCESS)){
                    dataDumpEmailService.sendEmail(dataDumpRequest.getInstitutionCodes(),
                            Integer.valueOf(0),
                            Integer.valueOf(0),
                            dataDumpRequest.getTransmissionType(),
                            null,
                            dataDumpRequest.getToEmailAddress(),
                            RecapConstants.DATADUMP_NO_DATA_AVAILABLE,
                            Integer.valueOf(0),
                            dataDumpRequest.getFetchType(), dataDumpRequest.getRequestingInstitutionCode());
                    if (RecapConstants.EXPORT_SCHEDULER_CALL) {
                        producerTemplate.sendBody(RecapConstants.DATA_DUMP_COMPLETION_FROM, dataDumpRequest.getRequestingInstitutionCode());
                    }
                }
                else {
                    dataDumpEmailService.sendEmailForDumpNotification(dataDumpRequest);
                }
            }
            responseMessage = getResponseMessage(outputString, dataDumpRequest);
        } catch (Exception e) {
            logger.error(RecapConstants.ERROR,e);
            responseMessage = RecapConstants.DATADUMP_EXPORT_FAILURE;
        }
        return responseMessage;
    }

    /**
     * Gets the message from HTTP queue for the status of the data dump process.
     * @return
     */
    private String getMessageFromHttpQ(){
        return getMessageFrom(consumerTemplate, RecapConstants.DATADUMP_HTTP_Q);
    }

    /**
     * Gets the message from record available queue to identity if the records are available for data dump processing.
     * @return
     */
    private String getMessageFromIsRecordAvailableQ(){
        return getMessageFrom(consumerTemplate, RecapConstants.DATADUMP_IS_RECORD_AVAILABLE_Q);
    }

    /**
     * Splits the given string by comma and prepares a list.
     * @param inputString
     * @return
     */
    private List<String> splitStringAndGetList(String inputString) {
        String[] splittedString = inputString.split(",");
        return Arrays.asList(splittedString);
    }

    /**
     * Convert string type list to integer type list.
     * @param stringList
     * @return
     */
    private List<Integer> getIntegerListFromStringList(List<String> stringList) {
        List<Integer> integerList = new ArrayList<>();
        for (String stringValue : stringList) {
            integerList.add(Integer.parseInt(stringValue));
        }
        return integerList;
    }

    /**
     * Splits the string by comma and gets integer type list from string type list.
     * @param inputString
     * @return
     */
    private List<Integer> splitStringAndGetIntegerList(String inputString) {
        return getIntegerListFromStringList(splitStringAndGetList(inputString));
    }

    /**
     * Sets the request values to data dump request object.
     *
     * @param dataDumpRequest           the data dump request
     * @param fetchType                 the fetch type
     * @param institutionCodes          the institution codes
     * @param date                      the date
     * @param collectionGroupIds        the collection group ids
     * @param transmissionType          the transmission type
     * @param requestingInstitutionCode the requesting institution code
     * @param toEmailAddress            the to email address
     * @param outputFormat              the output format
     */
    public void setDataDumpRequest(DataDumpRequest dataDumpRequest, String fetchType, String institutionCodes, String date, String toDate, String collectionGroupIds,
                                   String transmissionType, String requestingInstitutionCode, String toEmailAddress, String outputFormat) {
        if (fetchType != null) {
            dataDumpRequest.setFetchType(fetchType);
        }
        if (institutionCodes != null) {
            List<String> institutionCodeList = splitStringAndGetList(institutionCodes);
            dataDumpRequest.setInstitutionCodes(institutionCodeList);
        }
        if (date != null && !"".equals(date)) {
            dataDumpRequest.setDate(date);
        }
        if (toDate != null && !"".equals(toDate)) {
            dataDumpRequest.setToDate(toDate);
        }
        if (collectionGroupIds != null && !"".equals(collectionGroupIds)) {
            List<Integer> collectionGroupIdList = splitStringAndGetIntegerList(collectionGroupIds);
            dataDumpRequest.setCollectionGroupIds(collectionGroupIdList);
        } else {
            List<Integer> collectionGroupIdList = new ArrayList<>();
            CollectionGroupEntity collectionGroupEntityShared = collectionGroupDetailsRepository.findByCollectionGroupCode(RecapConstants.COLLECTION_GROUP_SHARED);
            collectionGroupIdList.add(collectionGroupEntityShared.getId());
            CollectionGroupEntity collectionGroupEntityOpen = collectionGroupDetailsRepository.findByCollectionGroupCode(RecapConstants.COLLECTION_GROUP_OPEN);
            collectionGroupIdList.add(collectionGroupEntityOpen.getId());
            dataDumpRequest.setCollectionGroupIds(collectionGroupIdList);
        }
        if (transmissionType != null && !"".equals(transmissionType)) {
            dataDumpRequest.setTransmissionType(transmissionType);
        } else {
            dataDumpRequest.setTransmissionType(RecapConstants.DATADUMP_TRANSMISSION_TYPE_FTP);
        }
        if (requestingInstitutionCode != null) {
            dataDumpRequest.setRequestingInstitutionCode(requestingInstitutionCode);
        }
        if (!StringUtils.isEmpty(toEmailAddress)) {
            dataDumpRequest.setToEmailAddress(toEmailAddress);
        }

        if (!StringUtils.isEmpty(outputFormat)) {
            dataDumpRequest.setOutputFileFormat(outputFormat);
        }

        dataDumpRequest.setDateTimeString(getDateTimeString());

        dataDumpRequest.setRequestId(new SimpleDateFormat(RecapCommonConstants.DATE_FORMAT_YYYYMMDDHHMM).format(new Date())+
                "-"+dataDumpRequest.getInstitutionCodes()+"-"+dataDumpRequest.getRequestingInstitutionCode()+"-"+dataDumpRequest.getFetchType());
    }

    /**
     * Validate incoming data dump request.
     *
     * @param dataDumpRequest the data dump request
     * @return the string
     */
    public String validateIncomingRequest(DataDumpRequest dataDumpRequest) {
        String validationMessage = null;
        Date currentDate = new Date();
        Map<Integer, String> errorMessageMap = new HashMap<>();
        Integer errorcount = 1;
        if (!dataDumpRequest.getInstitutionCodes().isEmpty()) {
            for (String institutionCode : dataDumpRequest.getInstitutionCodes()) {
                if (!institutionCode.equals(RecapCommonConstants.COLUMBIA) && !institutionCode.equals(RecapCommonConstants.PRINCETON)
                        && !institutionCode.equals(RecapCommonConstants.NYPL)) {
                    errorMessageMap.put(errorcount, RecapConstants.DATADUMP_VALID_INST_CODES_ERR_MSG);
                    errorcount++;
                }
            }
            if(dataDumpRequest.getInstitutionCodes().size() != 1 && dataDumpRequest.getFetchType().equals(fetchTypeFull)) {
                errorMessageMap.put(errorcount, RecapConstants.DATADUMP_MULTIPLE_INST_CODES_ERR_MSG);
                errorcount++;
            }
        }
        if (dataDumpRequest.getRequestingInstitutionCode() != null && !dataDumpRequest.getRequestingInstitutionCode().equals(RecapCommonConstants.COLUMBIA) && !dataDumpRequest.getRequestingInstitutionCode().equals(RecapCommonConstants.PRINCETON)
                && !dataDumpRequest.getRequestingInstitutionCode().equals(RecapCommonConstants.NYPL) ) {
                errorMessageMap.put(errorcount, RecapConstants.DATADUMP_VALID_REQ_INST_CODE_ERR_MSG);
                errorcount++;
        }
        if (!dataDumpRequest.getFetchType().equals(fetchTypeFull) &&
                !dataDumpRequest.getFetchType().equals(RecapConstants.DATADUMP_FETCHTYPE_INCREMENTAL)
                && !dataDumpRequest.getFetchType().equals(RecapConstants.DATADUMP_FETCHTYPE_DELETED)) {
            errorMessageMap.put(errorcount, RecapConstants.DATADUMP_VALID_FETCHTYPE_ERR_MSG);
            errorcount++;
        }
        if (!dataDumpRequest.getTransmissionType().equals(RecapConstants.DATADUMP_TRANSMISSION_TYPE_FTP)
                && !dataDumpRequest.getTransmissionType().equals(RecapConstants.DATADUMP_TRANSMISSION_TYPE_HTTP)
                ) {
            errorMessageMap.put(errorcount, RecapConstants.DATADUMP_TRANS_TYPE_ERR_MSG);
            errorcount++;
        }
        if (dataDumpRequest.getFetchType().equals(fetchTypeFull) && dataDumpRequest.getInstitutionCodes() == null) {
                errorMessageMap.put(errorcount, RecapConstants.DATADUMP_INSTITUTIONCODE_ERR_MSG);
                errorcount++;
        }
        if (dataDumpRequest.getFetchType().equals(RecapConstants.DATADUMP_FETCHTYPE_INCREMENTAL) && dataDumpRequest.getDate() == null || "".equals(dataDumpRequest.getDate())) {
                errorMessageMap.put(errorcount, RecapConstants.DATADUMP_DATE_ERR_MSG);
                errorcount++;
        }
        if(dataDumpRequest.getFetchType().equals(RecapConstants.DATADUMP_FETCHTYPE_INCREMENTAL) || dataDumpRequest.getFetchType().equals(RecapConstants.DATADUMP_FETCHTYPE_DELETED)) {
            String dataDumpRequestDateString = dataDumpRequest.getDate();
            List<String> institutionCodes = dataDumpRequest.getInstitutionCodes();
            if(StringUtils.isNotBlank(dataDumpRequestDateString)) {
                try {
                    boolean isValidDate = validateDate(dataDumpRequestDateString);
                    if(isValidDate) {
                        if(institutionCodes.contains(RecapCommonConstants.PRINCETON)) {
                            errorcount = checkToRestrictFullDumpViaIncremental(errorMessageMap, errorcount, dataDumpRequestDateString, pulInitialDataLoadedDate, RecapCommonConstants.PRINCETON);
                        }
                        if(institutionCodes.contains(RecapCommonConstants.COLUMBIA)) {
                            errorcount = checkToRestrictFullDumpViaIncremental(errorMessageMap, errorcount, dataDumpRequestDateString, culInitialDataLoadedDate, RecapCommonConstants.COLUMBIA);
                        }
                        if(institutionCodes.contains(RecapCommonConstants.NYPL)) {
                            errorcount = checkToRestrictFullDumpViaIncremental(errorMessageMap, errorcount, dataDumpRequestDateString, nyplInitialDataLoadedDate, RecapCommonConstants.NYPL);
                        }
                        errorcount = checkForIncrementalDateLimit(currentDate, errorMessageMap, errorcount, dataDumpRequestDateString);
                    } else {
                        errorMessageMap.put(errorcount, MessageFormat.format(RecapConstants.INVALID_DATE_FORMAT, RecapCommonConstants.DATE_FORMAT_YYYYMMDDHHMM));
                        errorcount++;
                    }
                } catch (Exception e) {
                    logger.error("Exception : ", e);
                }
            }
        }
        if (dataDumpRequest.getTransmissionType().equals(RecapConstants.DATADUMP_TRANSMISSION_TYPE_FTP)) {
            if (StringUtils.isEmpty(dataDumpRequest.getToEmailAddress())) {
                errorMessageMap.put(errorcount, RecapConstants.DATADUMP_EMAIL_TO_ADDRESS_REQUIRED);
                errorcount++;
            } else {
                boolean isValid = validateEmailAddress(dataDumpRequest.getToEmailAddress());
                if (!isValid) {
                    errorMessageMap.put(errorcount, RecapConstants.INVALID_EMAIL_ADDRESS);
                    errorcount++;
                }
            }
        }

        if(RecapConstants.DATADUMP_TYPES.contains(dataDumpRequest.getFetchType())&& dataDumpRequest.getTransmissionType().equals(RecapConstants.DATADUMP_TRANSMISSION_TYPE_FTP)) {
            String dataExportStatus = getDataExportCurrentStatus();
            String status = Optional.ofNullable(dataExportStatus).orElse("No file created");
            logger.info("Validating datadump status file for requested Dump-Type {} by {} . Status : {}",dataDumpRequest.getFetchType(),dataDumpRequest.getRequestingInstitutionCode(),status);
            if(dataExportStatus != null && dataExportStatus.equals(RecapConstants.IN_PROGRESS)){
                switch (dataDumpRequest.getFetchType()){
                    case RecapConstants.DATADUMP_FETCHTYPE_FULL:
                        errorMessageMap.put(errorcount, RecapConstants.FULLDUMP_INPROGRESS_ERR_MSG);
                        errorcount++;
                        break;
                    case RecapConstants.DATADUMP_FETCHTYPE_INCREMENTAL:
                        errorMessageMap.put(errorcount, RecapConstants.INCREMENTAL_INPROGRESS_ERR_MSG);
                        errorcount++;
                        break;
                    case RecapConstants.DATADUMP_FETCHTYPE_DELETED:
                        errorMessageMap.put(errorcount, RecapConstants.DELETED_INPROGRESS_ERR_MSG);
                        errorcount++;
                        break;
                    default:
                        errorMessageMap.put(errorcount, RecapConstants.INPROGRESS_ERR_MSG);
                        errorcount++;
                }
            }
        }

        if (errorMessageMap.size() > 0) {
            validationMessage = buildErrorMessage(errorMessageMap);
        }
        return validationMessage;
    }

    private boolean validateDate(String dataDumpRequestDateString) {
        String[] dateStringArray = dataDumpRequestDateString.split(" ");
        if(dateStringArray.length == 1) {
            return false;
        } else {
            Date formattedDate = getFormattedDate(RecapCommonConstants.DATE_FORMAT_YYYYMMDDHHMM, dataDumpRequestDateString);
            if(formattedDate == null) {
                return false;
            }
        }
        return true;
    }

    private Integer checkForIncrementalDateLimit(Date currentDate, Map<Integer, String> errorMessageMap, Integer errorcount, String dataDumpRequestDateString) {
        Date dataDumpRequestDateTime = getFormattedDate(RecapCommonConstants.DATE_FORMAT_YYYYMMDDHHMM, dataDumpRequestDateString);
        long dateDifference = currentDate.getTime() - dataDumpRequestDateTime.getTime();
        long days = TimeUnit.DAYS.convert(dateDifference, TimeUnit.MILLISECONDS);
        if(StringUtils.isBlank(incrementalDateLimit)) {
            errorMessageMap.put(errorcount, MessageFormat.format(RecapConstants.INCREMENTAL_DATE_LIMIT_EMPTY_ERR_MSG, recapAssistEmailAddress));
            errorcount++;
        } else {
            if(Math.toIntExact(days) > Integer.valueOf(incrementalDateLimit)) {
                errorMessageMap.put(errorcount, MessageFormat.format(RecapConstants.DATADUMP_DAYS_LIMIT_EXCEEDED_ERROR_MSG, incrementalDateLimit, recapAssistEmailAddress));
                errorcount++;
            }
        }
        return errorcount;
    }

    private Integer checkToRestrictFullDumpViaIncremental(Map<Integer, String> errorMessageMap, Integer errorcount, String dataDumpRequestDateString, String initialDataLoadDateString, String institutionCode) {
        if(StringUtils.isBlank(initialDataLoadDateString)) {
            errorMessageMap.put(errorcount, MessageFormat.format(RecapConstants.INITIAL_DATA_LOAD_DATE_MISSING_ERR_MSG, institutionCode, recapAssistEmailAddress));
            errorcount++;
        } else {
            Date dataDumpRequestDate = getFormattedDate(RecapConstants.DATE_FORMAT_YYYYMMDD, dataDumpRequestDateString);
            Date initialDataLoadDate = getFormattedDate(RecapConstants.DATE_FORMAT_YYYYMMDD, initialDataLoadDateString);
            if(initialDataLoadDate.after(dataDumpRequestDate) || initialDataLoadDate.equals(dataDumpRequestDate)) {
                errorMessageMap.put(errorcount, MessageFormat.format(RecapConstants.RESTRICT_FULLDUMP_VIA_INCREMENTAL_ERROR_MSG, institutionCode, recapAssistEmailAddress));
                errorcount++;
            }
        }
        return errorcount;
    }

    private Date getFormattedDate(String dateFormat, String dateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        try {
            return simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            logger.error("Exception while Parsing Date : ", e);
        }
        return null;
    }

    /**
     * Gets the data dump export status reading from status file.
     * @return
     */
    private String getDataExportCurrentStatus(){
        File file = new File(dataDumpStatusFileName);
        String dataDumpStatus = null;
        try {
            if (file.exists()) {
                dataDumpStatus = FileUtils.readFileToString(file, Charset.defaultCharset());
            }
        } catch (IOException e) {
            logger.error(RecapConstants.ERROR,e);
            logger.error("Exception while creating or updating the file : " + e.getMessage());
        }
        return dataDumpStatus;
    }

    /**
     * Sets the data dump export status to a file.
     */
    private void setDataExportCurrentStatus(){
        File file = new File(dataDumpStatusFileName);
        File parentFile = file.getParentFile();
        try {
            if (file.exists()) {
                String dataDumpStatus = FileUtils.readFileToString(file, Charset.defaultCharset());
                if (dataDumpStatus.contains(RecapConstants.COMPLETED)) {
                    writeStatusToFile(file, RecapConstants.IN_PROGRESS);
                }
            } else {
                parentFile.mkdirs();
                boolean newFile = file.createNewFile();
                if(newFile) {
                    writeStatusToFile(file, RecapConstants.IN_PROGRESS);
                }
            }
        } catch (IOException e) {
            logger.error(RecapConstants.ERROR,e);
            logger.error("Exception while creating or updating the file : " + e.getMessage());
        }
    }

    /**
     * Writes data dump status to a file.
     * @param file
     * @param status
     * @throws IOException
     */
    private void writeStatusToFile(File file, String status) throws IOException {
        try (FileWriter fileWriter = new FileWriter(file, false)) {
            fileWriter.append(status);
            fileWriter.flush();
        } catch (IOException e) {
            logger.error(RecapConstants.EXCEPTION, e);
        }
    }

    /**
     * Builds error message string from map.
     * @param erroMessageMap
     * @return
     */
    private String buildErrorMessage(Map<Integer, String> erroMessageMap) {
        StringBuilder errorMessageBuilder = new StringBuilder();
        erroMessageMap.forEach((key, value) -> errorMessageBuilder.append(key).append(". ").append(value).append("\n"));
        return errorMessageBuilder.toString();
    }

    /**
     * Validates email address.
     * @param toEmailAddress
     * @return
     */
    private boolean validateEmailAddress(String toEmailAddress) {
        String regex = RecapCommonConstants.REGEX_FOR_EMAIL_ADDRESS;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(toEmailAddress);
        return matcher.matches();
    }

    /**
     * Gets response message for the data dump process.
     * @param outputString
     * @param dataDumpRequest
     * @return
     * @throws Exception
     */
    private String getResponseMessage(String outputString, DataDumpRequest dataDumpRequest) throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        String date = new Date().toString();
        if (dataDumpRequest.getTransmissionType().equals(RecapConstants.DATADUMP_TRANSMISSION_TYPE_FTP)) {
            if (outputString.equals(RecapConstants.DATADUMP_RECORDS_AVAILABLE_FOR_PROCESS)) {
                logger.info("Writing to data-dump status file as 'In Progress' on Dump-Type:{} Requesting Inst : {}",dataDumpRequest.getFetchType(),dataDumpRequest.getRequestingInstitutionCode());
                setDataExportCurrentStatus();
                outputString = RecapConstants.DATADUMP_PROCESS_STARTED;
            }
            responseHeaders.add(RecapCommonConstants.RESPONSE_DATE, date);
            return outputString;
        }else if (dataDumpRequest.getTransmissionType().equals(RecapConstants.DATADUMP_TRANSMISSION_TYPE_HTTP) && outputString != null) {
            responseHeaders.add(RecapCommonConstants.RESPONSE_DATE, date);
            return outputString;
        } else {
            responseHeaders.add(RecapCommonConstants.RESPONSE_DATE, date);
            return RecapConstants.DATADUMP_EXPORT_FAILURE;
        }
    }

    private String getDateTimeString() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(RecapConstants.DATE_FORMAT_DDMMMYYYYHHMM);
        return sdf.format(date);
    }

    public String getMessageFrom(ConsumerTemplate consumerTemplate, String queue)
    {
        String outputString;
        Exchange receive = consumerTemplate.receive(queue);
        Object body = receive.getIn().getBody();
        while (null == body) {
            receive = consumerTemplate.receive(queue);
            body = receive.getIn().getBody();
        }
        outputString = (String) receive.getIn().getBody();
        return outputString;
    }

}
