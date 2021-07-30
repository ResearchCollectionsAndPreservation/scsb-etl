package org.recap.model.jpa;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.model.jparw.ETLRequestLogEntity;
import org.recap.model.jparw.ExportStatusEntity;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class ETLRequestLogEntityUT extends BaseTestCaseUT {

    @Test
    public void testETLRequestLogEntity() {
        ETLRequestLogEntity etlRequestLogEntity = new ETLRequestLogEntity();

        etlRequestLogEntity.setId(1);
        etlRequestLogEntity.setExportStatusEntity(new ExportStatusEntity());
        etlRequestLogEntity.setCompleteTime(new Date());
        etlRequestLogEntity.setCollectionGroupIds("CGI");
        etlRequestLogEntity.setEmailIds("emailids");
        etlRequestLogEntity.setRequestedTime(new Date());
        etlRequestLogEntity.setFetchType("Pull");
        etlRequestLogEntity.setExportStatusId(1);
        etlRequestLogEntity.setImsRepositoryCodes("IMRC");
        etlRequestLogEntity.setMessage("msg");
        etlRequestLogEntity.setInstCodeToExport("ECExport");
        etlRequestLogEntity.setOutputFormat("Format");
        etlRequestLogEntity.setRequestingInstCode("22");
        etlRequestLogEntity.setUserName("test");
        etlRequestLogEntity.setTransmissionType("transmission");
        etlRequestLogEntity.setProvidedDate(new Date());

        assertNotNull(etlRequestLogEntity.getMessage());
        assertNotNull(etlRequestLogEntity.getExportStatusEntity());
        assertNotNull(etlRequestLogEntity.getCollectionGroupIds());
        assertNotNull(etlRequestLogEntity.getRequestedTime());
        assertNotNull(etlRequestLogEntity.getCompleteTime());
        assertNotNull(etlRequestLogEntity.getEmailIds());
        assertNotNull(etlRequestLogEntity.getExportStatusId());
        assertNotNull(etlRequestLogEntity.getFetchType());
        assertNotNull(etlRequestLogEntity.getImsRepositoryCodes());
        assertNotNull(etlRequestLogEntity.getInstCodeToExport());
        assertNotNull(etlRequestLogEntity.getOutputFormat());
        assertNotNull(etlRequestLogEntity.getProvidedDate());
        assertNotNull(etlRequestLogEntity.getRequestingInstCode());
        assertNotNull(etlRequestLogEntity.getTransmissionType());
        assertNotNull(etlRequestLogEntity.getUserName());

    }
}
