package org.recap.model.export;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class S3RecentDataExportInfoUT extends BaseTestCaseUT {

    @Test
    public void testS3RecentDataExportInfo() {

        S3RecentDataExportInfo s3RecentDataExportInfo = new S3RecentDataExportInfo();

        s3RecentDataExportInfo.setBibDataFormat("XML");
        s3RecentDataExportInfo.setBibCount("23456");
        s3RecentDataExportInfo.setInstitution("PUL");
        s3RecentDataExportInfo.setGcd("gcd");
        s3RecentDataExportInfo.setItemCount("4367");
        s3RecentDataExportInfo.setKeyName("TestKey");
        s3RecentDataExportInfo.setKeySize(1L);
        s3RecentDataExportInfo.setKeyLastModified(new Date());

        assertNotNull(s3RecentDataExportInfo.getBibCount());
        assertNotNull(s3RecentDataExportInfo.getBibDataFormat());
        assertNotNull(s3RecentDataExportInfo.getGcd());
        assertNotNull(s3RecentDataExportInfo.getInstitution());
        assertNotNull(s3RecentDataExportInfo.getItemCount());
        assertNotNull(s3RecentDataExportInfo.getKeyLastModified());
        assertNotNull(s3RecentDataExportInfo.getKeyName());
        assertNotNull(s3RecentDataExportInfo.getKeySize());
    }
}
