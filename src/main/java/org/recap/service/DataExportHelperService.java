package org.recap.service;


import lombok.extern.slf4j.Slf4j;
import org.recap.ScsbConstants;
import org.recap.camel.dynamicrouter.DynamicRouteBuilder;
import org.recap.model.export.DataDumpRequest;
import org.recap.model.jparw.ETLRequestLogEntity;
import org.recap.model.jparw.ExportStatusEntity;
import org.recap.service.preprocessor.DataDumpExportService;
import org.recap.util.datadump.DataDumpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DataExportHelperService {



    @Autowired DataDumpUtil dataDumpUtil;
    @Autowired private DynamicRouteBuilder dynamicRouteBuilder;
    @Autowired private DataExportDBService dataExportDBService;
    @Autowired private DataDumpExportService dataDumpExportService;

    public String checkForExistingRequestAndStart(DataDumpRequest dataDumpRequest, Boolean isTrigger) {
        if(isTrigger){
            dynamicRouteBuilder.addDataDumpExportRoutes();
            return dataDumpExportService.startDataDumpProcess(dataDumpRequest);
        }else {
            if (checkIfAnyExportIsInProgress()) {
                saveRequestToDB(dataDumpRequest, ScsbConstants.AWAITING);
                return ScsbConstants.EXPORT_MESSAGE;
            } else if (checkIfAnyExportIsInitiated()) {
                saveRequestToDB(dataDumpRequest, ScsbConstants.AWAITING);
                return ScsbConstants.EXPORT_MESSAGE;
            } else if (checkIfAnyExportIsAwaiting()) {
                saveRequestToDB(dataDumpRequest, ScsbConstants.AWAITING);
                dynamicRouteBuilder.addDataDumpExportRoutes();
                dataDumpExportService.startDataDumpProcess(dataDumpUtil.prepareRequestForExistingAwaiting());
                return ScsbConstants.EXPORT_MESSAGE;
            } else {
                saveRequestToDB(dataDumpRequest, ScsbConstants.INITIATED);
                dynamicRouteBuilder.addDataDumpExportRoutes();
                return dataDumpExportService.startDataDumpProcess(dataDumpRequest);
            }
        }
    }

    private void saveRequestToDB(DataDumpRequest dataDumpRequest,String status) {
        ETLRequestLogEntity savedETLRequestLogEntity = dataExportDBService.saveETLRequestToDB(dataDumpUtil.prepareRequestForAwaiting(dataDumpRequest,status));
        dataDumpRequest.setEtlRequestId(savedETLRequestLogEntity.getId());
        log.info("ETL Request ID - created : {}", savedETLRequestLogEntity.getId());
    }

    public boolean checkIfAnyExportIsInProgress() {
        ExportStatusEntity exportStatusEntity = dataExportDBService.findByExportStatusCode(ScsbConstants.IN_PROGRESS);
        List<ETLRequestLogEntity> etlRequestLogEntityList = dataExportDBService.findAllStatusById(exportStatusEntity.getId());
        return  !etlRequestLogEntityList.isEmpty();
    }

    public boolean checkIfAnyExportIsAwaiting() {
        ExportStatusEntity exportStatusEntity = dataExportDBService.findByExportStatusCode(ScsbConstants.AWAITING);
        List<ETLRequestLogEntity> allStatusOrderByRequestedTime = dataExportDBService.findAllStatusById(exportStatusEntity.getId());
        return !allStatusOrderByRequestedTime.isEmpty();
    }

    public boolean checkIfAnyExportIsInitiated() {
        ExportStatusEntity exportStatusEntity = dataExportDBService.findByExportStatusCode(ScsbConstants.INITIATED);
        List<ETLRequestLogEntity> allStatusOrderByRequestedTime = dataExportDBService.findAllStatusById(exportStatusEntity.getId());
        return !allStatusOrderByRequestedTime.isEmpty();
    }
}
