package org.recap.util.datadump;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCase;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.model.export.DataDumpRequest;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jparw.ReportDataEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.junit.Assert.assertNotNull;

public class DataDumpFailureReportUtilUT extends BaseTestCaseUT {

    @InjectMocks
    DataDumpFailureReportUtil dataDumpFailureReportUtil;

    @Test
    public void testgenerateDataDumpFailureReport(){
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setRequestingInstitutionCode("PUL");
        dataDumpRequest.setDate(new Date().toString());
        dataDumpRequest.setTransmissionType("0");
        dataDumpRequest.setFetchType("1");
        dataDumpRequest.setCollectionGroupIds(Arrays.asList(1,2,3));
        dataDumpRequest.setInstitutionCodes(Arrays.asList("PUL","CUL"));
        List<Map<String,Object>> successAndFailureFormattedFullList = new ArrayList<Map<String,Object>>();
        Map<String,Object> successAndFailureFormattedList=new HashMap<>();
        List<BibliographicEntity> failureList=new ArrayList<>();
        BibliographicEntity bibliographicEntity=new BibliographicEntity();
        failureList.add(bibliographicEntity);
        successAndFailureFormattedList.put(RecapConstants.DATADUMP_SUCCESSLIST,failureList);
        successAndFailureFormattedList.put(RecapConstants.DATADUMP_FORMATERROR,"formatError");
        successAndFailureFormattedFullList.add(successAndFailureFormattedList);
        successAndFailureFormattedFullList.add(successAndFailureFormattedList);
        List<ReportDataEntity> entity = dataDumpFailureReportUtil.generateDataDumpFailureReport(successAndFailureFormattedFullList,dataDumpRequest);
        assertNotNull(entity);

    }

}
