package org.recap.util.datadump;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;
import org.recap.model.export.DataDumpRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by AnithaV on 12/12/20.
 */

public class DataExportHeaderUtilUT extends BaseTestCaseUT {

    @InjectMocks
    DataExportHeaderUtil dataExportHeaderUtil;

    @Test
    public void getBatchHeaderString() {
        DataDumpRequest dataDumpRequest=new DataDumpRequest();
        dataDumpRequest.setOutputFileFormat("s3");
        dataDumpRequest.setFetchType("s3");
        dataDumpRequest.setDate(new Date().toString());
        List<Integer> collectionGroupIds=new ArrayList<>();
        collectionGroupIds.add(1);
        collectionGroupIds.add(2);
        dataDumpRequest.setCollectionGroupIds(collectionGroupIds);
        dataDumpRequest.setTransmissionType("TransmissionType");
        dataDumpRequest.setToEmailAddress("test@htcindia.com");
        dataDumpRequest.setDateTimeString(new Date().toString());
        dataDumpRequest.setRequestingInstitutionCode("1");
        dataDumpRequest.setRequestId("1");
        List<String> institutionCodes=new ArrayList<>();
        institutionCodes.add("1");
        institutionCodes.add("2");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setImsDepositoryCodes(Arrays.asList("RECAP","HD"));
        String headerString=dataExportHeaderUtil.getBatchHeaderString(1,1,"folderName","fileName",dataDumpRequest);
        assertNotNull(headerString);
    }
}
