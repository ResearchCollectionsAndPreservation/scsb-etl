package org.recap.model.jpa;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.model.jparw.ExportStatusEntity;

import static org.junit.Assert.assertNotNull;

public class ExportStatusEntityUT extends BaseTestCaseUT {

    @Test
    public void testExportStatusEntity() {

        ExportStatusEntity exportStatusEntity = new ExportStatusEntity();

        exportStatusEntity.setId(1);
        exportStatusEntity.setExportStatusCode("SC003");
        exportStatusEntity.setExportStatusDesc("SC003");

        assertNotNull(exportStatusEntity.getId());
        assertNotNull(exportStatusEntity.getExportStatusCode());
        assertNotNull(exportStatusEntity.getExportStatusDesc());

    }
}
