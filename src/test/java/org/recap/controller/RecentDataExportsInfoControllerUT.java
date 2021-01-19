package org.recap.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.model.export.S3RecentDataExportInfo;
import org.recap.model.export.S3RecentDataExportInfoList;
import org.recap.repository.InstitutionDetailsRepository;
import org.recap.service.RecentDataExportsInfoService;
import org.recap.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class RecentDataExportsInfoControllerUT {
    private static final Logger logger = LoggerFactory.getLogger(RecentDataExportsInfoControllerUT.class);

    @InjectMocks
    DataExportsRecentInfoController recentDataExportsInfoControllerMock;

    @Mock
    RecentDataExportsInfoService recentDataExportsInfoServiceMock;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepositoryMock;

    @Mock
    PropertyUtil propertyUtilMock;

    List<String> institutions;

    String bibDataFormat;

    List<S3RecentDataExportInfo> recentDataExportInfoListPUL = new ArrayList<>();
    List<S3RecentDataExportInfo> recentDataExportInfoListCUL = new ArrayList<>();
    List<S3RecentDataExportInfo> recentDataExportInfoFinalList = new ArrayList<>();
    S3RecentDataExportInfoList s3RecentDataExportInfoList = new S3RecentDataExportInfoList();
    S3RecentDataExportInfoList s3RecentDataExportActualInfoList = new S3RecentDataExportInfoList();

    @Before
    public void setup() {
        institutions = Arrays.asList("PUL", "CUL");
        bibDataFormat = "MARC";
        S3RecentDataExportInfo s3RecentDataExportInfoPUL = new S3RecentDataExportInfo();
        s3RecentDataExportInfoPUL.setKeyName("data-exports/PUL/MARCXml/Full/PUL_20201207_170500.zip");
        s3RecentDataExportInfoPUL.setInstitution("PUL");
        s3RecentDataExportInfoPUL.setBibDataFormat("MARC");
        s3RecentDataExportInfoPUL.setGcd("1*2*3");
        s3RecentDataExportInfoPUL.setBibCount("10");
        s3RecentDataExportInfoPUL.setItemCount("20");
        s3RecentDataExportInfoPUL.setKeySize(863);
        s3RecentDataExportInfoPUL.setKeyLastModified(new Date());
        recentDataExportInfoListPUL.add(s3RecentDataExportInfoPUL);
        S3RecentDataExportInfo s3RecentDataExportInfoCUL = new S3RecentDataExportInfo();
        s3RecentDataExportInfoCUL.setKeyName("data-exports/CUL/MARCXml/Full/CUL_20201208_121900.zip");
        s3RecentDataExportInfoCUL.setInstitution("CUL");
        s3RecentDataExportInfoCUL.setBibDataFormat("MARC");
        s3RecentDataExportInfoCUL.setGcd("1*2*3");
        s3RecentDataExportInfoCUL.setBibCount("13");
        s3RecentDataExportInfoCUL.setItemCount("127");
        s3RecentDataExportInfoCUL.setKeySize(863);
        s3RecentDataExportInfoCUL.setKeyLastModified(new Date());
        recentDataExportInfoListCUL.add(s3RecentDataExportInfoCUL);
        recentDataExportInfoFinalList.addAll(recentDataExportInfoListPUL);
        recentDataExportInfoFinalList.addAll(recentDataExportInfoListCUL);
        s3RecentDataExportInfoList.setRecentDataExportInfoList(recentDataExportInfoFinalList);
    }

    @Test
    public void getRecentDataExportsInfoTest() throws Exception {
        List<S3RecentDataExportInfo> recentDataExportInfoActualFinalList = null;
        Mockito.when(institutionDetailsRepositoryMock.findAllInstitutionCodeExceptHTC()).thenReturn(institutions);
        Mockito.when(propertyUtilMock.getPropertyByInstitutionAndKey(Mockito.anyString(), Mockito.anyString())).thenReturn(bibDataFormat);
        Mockito.when(recentDataExportsInfoServiceMock.generateRecentDataExportsInfo("PUL", "MARC")).thenReturn(recentDataExportInfoListPUL);
        Mockito.when(recentDataExportsInfoServiceMock.generateRecentDataExportsInfo("CUL", "MARC")).thenReturn(recentDataExportInfoListCUL);
        s3RecentDataExportActualInfoList = recentDataExportsInfoControllerMock.getRecentDataExportsInfo();
        assertEquals(s3RecentDataExportInfoList.getRecentDataExportInfoList(), s3RecentDataExportActualInfoList.getRecentDataExportInfoList());
    }
}
