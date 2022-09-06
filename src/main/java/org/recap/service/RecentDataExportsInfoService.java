package org.recap.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.ArrayStack;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbCommonConstants;
import org.recap.model.export.S3RecentDataExportInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecentDataExportsInfoService {


    @Autowired
    AmazonS3 s3client;

    @Value("${" + PropertyKeyConstants.SCSB_BUCKET_NAME + "}")
    private String scsbBucketName;

    @Value("${" + PropertyKeyConstants.S3_DATA_DUMP_DIR + "}")
    private String s3DataExportBasePath;

    @Value("${" + PropertyKeyConstants.RECENT_DATA_EXPORT_LIMIT + "}")
    private String recentDataExportInfoLimit;

    public List<S3RecentDataExportInfo> generateRecentDataExportsInfo(List<String> allInstitutionCodeExceptSupportInstitution, String institution, String bibDataFormat) {

        List<S3RecentDataExportInfo> recentDataExportInfoList = new ArrayList<>();
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        try {
            listObjectsRequest.setBucketName(scsbBucketName);
            for (String institutionPrefix : allInstitutionCodeExceptSupportInstitution) {
                listObjectsRequest.setPrefix(s3DataExportBasePath + institution + "/" + bibDataFormat + "Xml/Full/" + institutionPrefix);
                ObjectListing objectListing = s3client.listObjects(listObjectsRequest);
                for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
                    Map<String, String> records = getObjectContent(os.getKey());
                    S3RecentDataExportInfo s3RecentDataExportInfo = new S3RecentDataExportInfo();
                    s3RecentDataExportInfo.setKeyName(os.getKey());
                    s3RecentDataExportInfo.setInstitution(institution);
                    s3RecentDataExportInfo.setBibDataFormat(bibDataFormat);
                    s3RecentDataExportInfo.setGcd(records.get("Collection Group Id(s)"));
                    s3RecentDataExportInfo.setBibCount(records.get("No of Bibs Exported"));
                    s3RecentDataExportInfo.setItemCount(records.get("No of Items Exported"));
                    s3RecentDataExportInfo.setKeySize(os.getSize());
                    s3RecentDataExportInfo.setKeyLastModified(os.getLastModified());
                    recentDataExportInfoList.add(s3RecentDataExportInfo);
                    log.info("File with the key -->" + os.getKey() + " " + os.getSize() + " " + os.getLastModified());
                }
            }
        } catch (Exception e) {
            log.error(ScsbCommonConstants.LOG_ERROR, e);
        }
        recentDataExportInfoList.sort(Comparator.comparing(S3RecentDataExportInfo::getKeyLastModified).reversed());
        return recentDataExportInfoList.stream().limit(Integer.parseInt(recentDataExportInfoLimit)).collect(Collectors.toCollection(ArrayList::new));
    }

    public Map<String, String> getObjectContent(String fileName) {
        List<String> str = new ArrayList<>();
        String[] records = null;
        String[] headers = null;
        try {
            String basepath = fileName.substring(0, fileName.lastIndexOf('/'));
            String csvFileName = fileName.substring(fileName.lastIndexOf('/'));
            csvFileName = csvFileName.substring(1, csvFileName.lastIndexOf('.'));
            S3Object s3Object = s3client.getObject(scsbBucketName, basepath + "/" + "ExportDataDump_Full_" + csvFileName + ".csv");
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            Scanner fileIn = new Scanner(inputStream);
            if (null != fileIn) {
                while (fileIn.hasNext()) {
                    str.add(fileIn.nextLine());
                }
                headers = str.get(0).replace("\"", "").split(",");
                records = str.get(1).replace("\"", "").split(",");
            }
        } catch (Exception e) {
            log.error(ScsbCommonConstants.LOG_ERROR, e);
        }
        return mapResult(headers, records);
    }
    private static Map<String, String> mapResult(String[] headers,
                                          String[] records) {
        Map<String, String> result = new HashMap<>();
        if(headers != null && headers.length > 0) {
            for (int i = 0; i < headers.length; i++) {
                result.put(headers[i], records[i]);
            }
        }
        return result;
    }
}
