package org.recap.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.model.export.S3RecentDataExportInfo;
import org.recap.model.export.S3RecentDataExportInfoList;
import org.recap.service.RecentDataExportsInfoService;
import org.recap.util.CommonUtil;
import org.recap.util.PropertyUtil;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith({SpringExtension.class})
public class RecentDataExportsInfoControllerUT {


    @InjectMocks
    DataExportsRecentInfoController recentDataExportsInfoControllerMock;

    @Mock
    RecentDataExportsInfoService recentDataExportsInfoServiceMock;

    @Mock
    CommonUtil commonUtil;

    @Mock
    PropertyUtil propertyUtilMock;

    List<String> institutions;

    String bibDataFormat;

    List<S3RecentDataExportInfo> recentDataExportInfoListPUL = new ArrayList<>();
    List<S3RecentDataExportInfo> recentDataExportInfoListCUL = new ArrayList<>();
    List<S3RecentDataExportInfo> recentDataExportInfoFinalList = new ArrayList<>();
    S3RecentDataExportInfoList s3RecentDataExportInfoList = new S3RecentDataExportInfoList();
    S3RecentDataExportInfoList s3RecentDataExportActualInfoList = new S3RecentDataExportInfoList();

    @BeforeEach
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
        Mockito.when(commonUtil.findAllInstitutionCodesExceptSupportInstitution()).thenReturn(institutions);
        Mockito.when(propertyUtilMock.getPropertyByInstitutionAndKey(Mockito.anyString(), Mockito.anyString())).thenReturn(bibDataFormat);
        Mockito.when(recentDataExportsInfoServiceMock.generateRecentDataExportsInfo(any(), anyString(), anyString())).thenReturn(recentDataExportInfoListCUL);
        s3RecentDataExportActualInfoList = recentDataExportsInfoControllerMock.getRecentDataExportsInfo();
        assertNotNull(s3RecentDataExportInfoList);
    }

    @Test
    public void getRecentDataExportsInfoException() throws Exception {
        Mockito.when(commonUtil.findAllInstitutionCodesExceptSupportInstitution()).thenThrow(new NullPointerException());
        s3RecentDataExportActualInfoList = recentDataExportsInfoControllerMock.getRecentDataExportsInfo();
        assertNotNull(s3RecentDataExportInfoList);
    }
}
