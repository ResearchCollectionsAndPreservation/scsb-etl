package org.recap.service;

import org.recap.model.jparw.ETLRequestLogEntity;
import org.recap.model.jparw.ExportStatusEntity;
import org.recap.repositoryrw.ETLRequestLogDetailsRepository;
import org.recap.repositoryrw.ExportStatusDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DataExportDBService {

    @Autowired ETLRequestLogDetailsRepository etlRequestLogDetailsRepository;
    @Autowired ExportStatusDetailsRepository exportStatusDetailsRepository;

    @Transactional
    public ETLRequestLogEntity saveETLRequestToDB(ETLRequestLogEntity etlRequestLogEntity) {
         return etlRequestLogDetailsRepository.saveAndFlush(etlRequestLogEntity);
    }

    public ExportStatusEntity findByExportStatusCode(String statusCode) {
        return exportStatusDetailsRepository.findByExportStatusCode(statusCode);
    }

    public List<ETLRequestLogEntity> findAllStatusById(Integer id) {
        return etlRequestLogDetailsRepository.findAllByExportStatusId(id);
    }

    public List<ETLRequestLogEntity> findAllStatusForS3OrderByRequestedTime(Integer statusId, String fetchTypeId) {
       return etlRequestLogDetailsRepository.findByExportStatusIdAndTransmissionTypeOrderByRequestedTime(statusId,fetchTypeId);
    }

    public List<ETLRequestLogEntity> findByExportStatusIdAndTransmissionType(Integer statusId, String fetchTypeId) {
        return etlRequestLogDetailsRepository.findByExportStatusIdAndTransmissionType(statusId,fetchTypeId);
    }
}
