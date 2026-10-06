package org.recap.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.recap.BaseTestCaseUT;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class RecentDataExportsInfoServiceUT extends BaseTestCaseUT {

    @InjectMocks
    @Spy
    RecentDataExportsInfoService recentDataExportsInfoService;

    @Mock
    AmazonS3 s3client;

    @Mock
    ObjectListing objectListing;

    @Mock
    S3Object s3Object;

    @Mock
    S3ObjectInputStream s3ObjectInputStream;

    private final String scsbBucketName = "testName";

    private final String s3DataExportBasePath = "testExportPath";

    private final String recentDataExportInfoLimit = "10";

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(recentDataExportsInfoService, "scsbBucketName", scsbBucketName);
        ReflectionTestUtils.setField(recentDataExportsInfoService, "s3DataExportBasePath", s3DataExportBasePath);
        ReflectionTestUtils.setField(recentDataExportsInfoService, "recentDataExportInfoLimit", recentDataExportInfoLimit);
    }


    @Test
    public void generateRecentDataExportsInfo() {
        String institution = "PUL";
        String bibDataFormat = "XML";
        S3ObjectSummary s3ObjectSummary = getS3ObjectSummary();
        Map<String, String> records = new HashMap<>();
        records.put("Collection Group Id(s)","1,2,3");
        Mockito.when(objectListing.getObjectSummaries()).thenReturn(Arrays.asList(s3ObjectSummary));
        Mockito.when(s3client.listObjects(any(ListObjectsRequest.class))).thenReturn(objectListing);
        Mockito.when(s3client.getObject(anyString(), any())).thenReturn(s3Object);
        Mockito.when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);
        Mockito.doReturn(records).when(recentDataExportsInfoService).getObjectContent(any());
        recentDataExportsInfoService.generateRecentDataExportsInfo(Arrays.asList("PUL,CUL,NYPL"), institution, bibDataFormat);
    }

    @Test
    public void generateRecentDataExportsInfoException() {
        String institution = "PUL";
        String bibDataFormat = "XML";
        S3ObjectSummary s3ObjectSummary = getS3ObjectSummary();
        Mockito.when(objectListing.getObjectSummaries()).thenReturn(Arrays.asList(s3ObjectSummary));
        Mockito.when(s3client.listObjects(any(ListObjectsRequest.class))).thenReturn(objectListing);
        Mockito.when(s3client.getObject(anyString(), any())).thenReturn(s3Object);
        Mockito.when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);
        recentDataExportsInfoService.generateRecentDataExportsInfo(Arrays.asList("PUL,CUL,NYPL"), institution, bibDataFormat);
    }

    @Test
    public void mapResult(){
        String[] headers = {"test","headers"};
        String[] records = {"test","records"};
        ReflectionTestUtils.invokeMethod(recentDataExportsInfoService,"mapResult",headers,records);
    }

    private S3ObjectSummary getS3ObjectSummary() {
        S3ObjectSummary s3ObjectSummary = new S3ObjectSummary();
        s3ObjectSummary.setKey("t/es/t,Ke/y.csv");
        s3ObjectSummary.setSize(10);
        s3ObjectSummary.setLastModified(new Date());
        return s3ObjectSummary;
    }
}
