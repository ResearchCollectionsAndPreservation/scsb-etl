package org.recap.util.datadump;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.export.DataDumpRequest;
import org.recap.model.jparw.ReportDataEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class DataDumpFailureReportUtilUT extends BaseTestCase {
    @Autowired
    DataDumpFailureReportUtil dataDumpFailureReportUtil;

    @Test
    public void testgenerateDataDumpFailureReport(){
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setFetchType("1");
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        dataDumpRequest.setCollectionGroupIds(cgIds);
        List<Map<String,Object>> obj = new ArrayList<Map<String,Object>>();

        List<ReportDataEntity> entity = new ArrayList<>();
        entity = dataDumpFailureReportUtil.generateDataDumpFailureReport(obj,dataDumpRequest);
        assertNotNull(entity);

    }

}
