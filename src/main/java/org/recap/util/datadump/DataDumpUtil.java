package org.recap.util.datadump;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.export.DataDumpRequest;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.model.jparw.ETLRequestLogEntity;
import org.recap.model.jparw.ExportStatusEntity;
import org.recap.model.jpa.ImsLocationEntity;
import org.recap.repository.CollectionGroupDetailsRepository;
import org.recap.repositoryrw.ETLRequestLogDetailsRepository;
import org.recap.repositoryrw.ExportStatusDetailsRepository;
import org.recap.repository.ImsLocationDetailsRepository;
import org.recap.service.DataExportDBService;
import org.recap.service.preprocessor.DataDumpExportService;
import org.recap.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Slf4j
@Component
public class DataDumpUtil {



    @Autowired CollectionGroupDetailsRepository collectionGroupDetailsRepository;
    @Autowired ETLRequestLogDetailsRepository etlRequestLogDetailsRepository;
    @Autowired ExportStatusDetailsRepository exportStatusDetailsRepository;
    @Autowired DataDumpExportService dataDumpExportService;
    @Autowired ImsLocationDetailsRepository imsLocationDetailsRepository;
    @Autowired DataExportDBService dataExportDBService;

    public List<String> getCollectionGroupCodes(List<Integer> collectionGroupIds){
        List<CollectionGroupEntity> collectionGroupEntityList = collectionGroupDetailsRepository.findAllByIds(collectionGroupIds);
        return collectionGroupEntityList.stream()
                .map(CollectionGroupEntity::getCollectionGroupCode)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public String getFetchType(String fetchTypeNumber) {
        String fetchType ="";
        switch (fetchTypeNumber) {
            case ScsbConstants.DATADUMP_FETCHTYPE_FULL:
                fetchType= ScsbConstants.EXPORT_TYPE_FULL;
                break;
            case ScsbConstants.DATADUMP_FETCHTYPE_INCREMENTAL:
                fetchType= ScsbConstants.INCREMENTAL;
                break;
            case ScsbConstants.DATADUMP_FETCHTYPE_DELETED:
                fetchType= ScsbConstants.DELETED;
                break;
            default:
                fetchType= "Export";
        }
        return fetchType;
    }

    public String getOutputformat(String outputFileFormat) {
        String format ="";
        switch (outputFileFormat) {
            case ScsbConstants.DATADUMP_XML_FORMAT_MARC:
                format= ScsbConstants.MARC;
                break;
            case ScsbConstants.DATADUMP_XML_FORMAT_SCSB:
                format= ScsbConstants.SCSB;
                break;
            case ScsbConstants.DATADUMP_DELETED_JSON_FORMAT:
                format= ScsbConstants.JSON;
                break;
        }
        return format;
    }

    public String getTransmissionType(String transmissionType) {
        String type ="";
        switch (transmissionType) {
            case ScsbConstants.DATADUMP_TRANSMISSION_TYPE_S3:
                type= "S3";
                break;
            case ScsbConstants.DATADUMP_TRANSMISSION_TYPE_HTTP:
                type= "HTTP";
                break;
            case ScsbConstants.DATADUMP_TRANSMISSION_TYPE_FILESYSTEM:
                type= "Filesystem";
                break;
        }
        return type;
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
                                   String transmissionType, String requestingInstitutionCode, String toEmailAddress, String outputFormat,String imsDepositoryCodes,String userName) {
        if (fetchType != null) {
            dataDumpRequest.setFetchType(fetchType);
        }
        if (institutionCodes != null) {
            List<String> institutionCodeList = splitStringAndGetList(institutionCodes);
            dataDumpRequest.setInstitutionCodes(institutionCodeList);
        }
        if (imsDepositoryCodes != null && !"".equals(imsDepositoryCodes)) {
            List<String> imsDepositoryCodesList = splitStringAndGetList(imsDepositoryCodes);
            dataDumpRequest.setImsDepositoryCodes(imsDepositoryCodesList);
        }
        else {
            ImsLocationEntity imsLocationEntity = imsLocationDetailsRepository.findByImsLocationCode(ScsbConstants.IMS_DEPOSITORY_RECAP);
            dataDumpRequest.setImsDepositoryCodes(Arrays.asList(imsLocationEntity.getImsLocationCode()));
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
            CollectionGroupEntity collectionGroupEntityShared = collectionGroupDetailsRepository.findByCollectionGroupCode(ScsbConstants.COLLECTION_GROUP_SHARED);
            collectionGroupIdList.add(collectionGroupEntityShared.getId());
            CollectionGroupEntity collectionGroupEntityOpen = collectionGroupDetailsRepository.findByCollectionGroupCode(ScsbConstants.COLLECTION_GROUP_OPEN);
            collectionGroupIdList.add(collectionGroupEntityOpen.getId());
            dataDumpRequest.setCollectionGroupIds(collectionGroupIdList);
        }
        if (transmissionType != null && !"".equals(transmissionType)) {
            dataDumpRequest.setTransmissionType(transmissionType);
        } else {
            dataDumpRequest.setTransmissionType(ScsbConstants.DATADUMP_TRANSMISSION_TYPE_S3);
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

        dataDumpRequest.setUserName(!userName.isEmpty()?userName: ScsbConstants.SWAGGER);

        dataDumpRequest.setDateTimeString(DateUtil.getDateTimeString());

        dataDumpRequest.setRequestId(new SimpleDateFormat(ScsbCommonConstants.DATE_FORMAT_YYYYMMDDHHMM).format(new Date())+
                "-"+dataDumpRequest.getInstitutionCodes()+"-"+dataDumpRequest.getRequestingInstitutionCode()+"-"+dataDumpRequest.getFetchType());
    }

    /**
     * Splits the given string by comma and prepares a list.
     * @param inputString
     * @return
     */
    private static List<String> splitStringAndGetList(String inputString) {
        String[] splittedString = inputString.split(",");
        return Arrays.asList(splittedString);
    }

    /**
     * Splits the string by comma and gets integer type list from string type list.
     * @param inputString
     * @return
     */
    private static List<Integer> splitStringAndGetIntegerList(String inputString) {
        return getIntegerListFromStringList(splitStringAndGetList(inputString));
    }

    /**
     * Convert string type list to integer type list.
     * @param stringList
     * @return
     */
    private static List<Integer> getIntegerListFromStringList(List<String> stringList) {
        List<Integer> integerList = new ArrayList<>();
        for (String stringValue : stringList) {
            integerList.add(Integer.parseInt(stringValue));
        }
        return integerList;
    }

    public ETLRequestLogEntity prepareRequestForAwaiting(DataDumpRequest dataDumpRequest, String status) {
        ETLRequestLogEntity etlRequestLogEntity =new ETLRequestLogEntity();
        ExportStatusEntity exportStatusEntity = exportStatusDetailsRepository.findByExportStatusCode(status);
        etlRequestLogEntity.setExportStatusId(exportStatusEntity.getId());
        etlRequestLogEntity.setExportStatusEntity(exportStatusEntity);
        String collectionGroupIds = dataDumpRequest.getCollectionGroupIds().stream().map(String::valueOf)
                .collect(Collectors.joining(","));
        String defaultCgds = Arrays.asList(ScsbConstants.DATADUMP_CGD_SHARED, ScsbConstants.DATADUMP_CGD_OPEN).stream().map(String::valueOf).collect(Collectors.joining(","));
        etlRequestLogEntity.setCollectionGroupIds(collectionGroupIds!=null?collectionGroupIds:defaultCgds );
        etlRequestLogEntity.setEmailIds(dataDumpRequest.getToEmailAddress());
        etlRequestLogEntity.setRequestedTime(new Date());
        etlRequestLogEntity.setFetchType(dataDumpRequest.getFetchType());
        etlRequestLogEntity.setOutputFormat(dataDumpRequest.getOutputFileFormat());
        etlRequestLogEntity.setRequestingInstCode(dataDumpRequest.getRequestingInstitutionCode());
        etlRequestLogEntity.setInstCodeToExport(String.join(",",dataDumpRequest.getInstitutionCodes()));
        etlRequestLogEntity.setTransmissionType(dataDumpRequest.getTransmissionType()!=null? dataDumpRequest.getTransmissionType() : "0");
        etlRequestLogEntity.setImsRepositoryCodes(dataDumpRequest.getImsDepositoryCodes()!=null?String.join(",",dataDumpRequest.getImsDepositoryCodes()): ScsbConstants.IMS_DEPOSITORY_RECAP);
        etlRequestLogEntity.setUserName(dataDumpRequest.getUserName());
        etlRequestLogEntity.setProvidedDate(dataDumpRequest.getDate()!=null?DateUtil.getDateFromString(dataDumpRequest.getDate(),ScsbCommonConstants.DATE_FORMAT_YYYYMMDDHHMM):null);
        return etlRequestLogEntity;
    }

    @Transactional
    public void updateStatusInETLRequestLog(DataDumpRequest dataDumpRequest, String outputString) {
        log.info("ETL Request ID to update: {}",dataDumpRequest.getEtlRequestId());
        Optional<ETLRequestLogEntity> etlRequestLogEntity = etlRequestLogDetailsRepository.findById(dataDumpRequest.getEtlRequestId());
        etlRequestLogEntity.ifPresent(exportLog ->{
            if(outputString.contains(ScsbConstants.DATADUMP_EXPORT_FAILURE) ){
                ExportStatusEntity exportStatusEntity = exportStatusDetailsRepository.findByExportStatusCode(ScsbConstants.INVALID);
                exportLog.setExportStatusId(exportStatusEntity.getId());
                exportLog.setExportStatusEntity(exportStatusEntity);
                exportLog.setMessage(outputString);
                exportLog.setCompleteTime(new Date());
            }
            else if(outputString.contains("100")){
                ExportStatusEntity exportStatusEntity = exportStatusDetailsRepository.findByExportStatusCode(ScsbConstants.COMPLETED);
                exportLog.setExportStatusId(exportStatusEntity.getId());
                exportLog.setExportStatusEntity(exportStatusEntity);
                exportLog.setMessage("Diplayed the result in the response");
                exportLog.setCompleteTime(new Date());
                DataDumpRequest awaitingRequest = checkAndPrepareAwaitingReqIfAny();
                if(awaitingRequest!=null){
                    dataDumpExportService.startDataDumpProcess(awaitingRequest);
                }
            }
            else if(outputString.contains(ScsbConstants.IN_PROGRESS)){
                ExportStatusEntity exportStatusEntity = exportStatusDetailsRepository.findByExportStatusCode(ScsbConstants.IN_PROGRESS);
                exportLog.setExportStatusId(exportStatusEntity.getId());
                exportLog.setExportStatusEntity(exportStatusEntity);
            }
            else{
                ExportStatusEntity exportStatusEntity = exportStatusDetailsRepository.findByExportStatusCode(outputString);
                exportLog.setExportStatusId(exportStatusEntity.getId());
                exportLog.setExportStatusEntity(exportStatusEntity);
                exportLog.setCompleteTime(new Date());
            }
            etlRequestLogDetailsRepository.saveAndFlush(exportLog);
        });
    }

    private static DataDumpRequest prepareDataDumpReq(ETLRequestLogEntity etlRequestLogEntity) {
        DataDumpRequest dataDumpRequestForAwaiting=new DataDumpRequest();
        dataDumpRequestForAwaiting.setFetchType(etlRequestLogEntity.getFetchType());
        dataDumpRequestForAwaiting.setOutputFileFormat(etlRequestLogEntity.getOutputFormat());
        dataDumpRequestForAwaiting.setTransmissionType(etlRequestLogEntity.getTransmissionType());
        List<Integer> collectionGroupIds = Arrays.stream(etlRequestLogEntity.getCollectionGroupIds()
                .split(",")).map(Integer::parseInt)
                .collect(Collectors.toCollection(ArrayList::new));
        dataDumpRequestForAwaiting.setCollectionGroupIds(collectionGroupIds);
        List<String> imsRepositoryList = List.of(etlRequestLogEntity.getImsRepositoryCodes().split(","));
        dataDumpRequestForAwaiting.setImsDepositoryCodes(imsRepositoryList);
        dataDumpRequestForAwaiting.setRequestingInstitutionCode(etlRequestLogEntity.getRequestingInstCode());
        List<String> institutionList = List.of(etlRequestLogEntity.getInstCodeToExport().split(","));
        dataDumpRequestForAwaiting.setInstitutionCodes(institutionList);
        dataDumpRequestForAwaiting.setDate(etlRequestLogEntity.getProvidedDate()!=null?String.valueOf(etlRequestLogEntity.getProvidedDate()):null);
        dataDumpRequestForAwaiting.setDateTimeString(DateUtil.getDateTimeString());
        dataDumpRequestForAwaiting.setRequestFromSwagger(true);
        dataDumpRequestForAwaiting.setEtlRequestId(etlRequestLogEntity.getId());
        dataDumpRequestForAwaiting.setToEmailAddress(etlRequestLogEntity.getEmailIds());
        dataDumpRequestForAwaiting.setUserName(etlRequestLogEntity.getUserName());
        return dataDumpRequestForAwaiting;
    }

    public DataDumpRequest checkAndPrepareAwaitingReqIfAny() {
        ExportStatusEntity awaitingStatusEntity = dataExportDBService.findByExportStatusCode(ScsbConstants.AWAITING);
        List<ETLRequestLogEntity> etlRequestsAwaitingForExport = dataExportDBService.findAllStatusById(awaitingStatusEntity.getId());
        if(!etlRequestsAwaitingForExport.isEmpty()){
            return prepareRequestForExistingAwaiting();
        }
        return null;
    }

    public DataDumpRequest prepareRequestForExistingAwaiting() {
        ExportStatusEntity exportStatusEntity = dataExportDBService.findByExportStatusCode(ScsbConstants.AWAITING);
        List<ETLRequestLogEntity> allByStatusOrderByRequestedTime = dataExportDBService.findAllStatusById(exportStatusEntity.getId());
        return prepareDataDumpReq(allByStatusOrderByRequestedTime.get(0));
    }



}
