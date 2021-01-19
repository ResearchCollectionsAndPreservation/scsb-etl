package org.recap.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jparw.ReportDataEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by AnithaV on 12/12/20.
 */

public class DBReportUtilUT extends BaseTestCaseUT {

    @InjectMocks
    DBReportUtil mockDBReportUtil;

    @Before
    public void setUp() {
        Map<String, Integer> institutionEntitiesMap=new HashMap<>();
        institutionEntitiesMap.put("OwningInstitutionId",1);
        Map<String, Integer> collectionGroupMap=new HashMap<>();
        collectionGroupMap.put(RecapCommonConstants.COLLECTION_GROUP_DESIGNATION,1);
        ReflectionTestUtils.setField(mockDBReportUtil,"institutionEntitiesMap",institutionEntitiesMap);
        ReflectionTestUtils.setField(mockDBReportUtil,"collectionGroupMap",collectionGroupMap);
        MockitoAnnotations.initMocks(this);
    }

    String content = "<collection>\n" +
            "        <record>\n" +
            "        <controlfield tag=\"001\">47764496</controlfield>\n" +
            "        <controlfield tag=\"003\">OCoLC</controlfield>\n" +
            "        <controlfield tag=\"005\">20021018083242.7</controlfield>\n" +
            "        <controlfield tag=\"008\">010604s2000 it a bde 000 0cita</controlfield>\n" +
            "        <datafield ind1=\"0\" ind2=\"0\" tag=\"245\">\n" +
            "        <subfield code=\"a\">Dizionario biografico enciclopedico di un secolo del calcio italiano /\n" +
            "        </subfield>\n" +
            "        <subfield code=\"c\">a cura di Marco Sappino.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"1\" ind2=\"4\" tag=\"246\">\n" +
            "        <subfield code=\"a\">Dizionario del calcio italiano</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"260\">\n" +
            "        <subfield code=\"a\">Milano :</subfield>\n" +
            "        <subfield code=\"b\">Baldini &amp; Castoldi,</subfield>\n" +
            "        <subfield code=\"c\">c2000.</subfield>\n" +
            "        </datafield>\n" +
            "        <leader>01184nam a22003494a 4500</leader>\n" +
            "        </record>\n" +
            "        </collection>";

    @Test
    public void generateBibHoldingsAndItemsFailureReportEntities(){
        List<ReportDataEntity> reportEntities = mockDBReportUtil.generateBibHoldingsAndItemsFailureReportEntities(getBibliographicEntity(),getHoldingsEntity(),getItemEntity());
        assertNotNull(reportEntities);
    }

    @Test
    public void institutionEntitiesMap(){
        Map<String, Integer> institutionEntitiesMap=new HashMap<>();
        institutionEntitiesMap.put("OwningInstitutionId",1);
        mockDBReportUtil.setInstitutionEntitiesMap(institutionEntitiesMap);
        assertEquals(institutionEntitiesMap,mockDBReportUtil.getInstitutionEntitiesMap());
    }

    @Test
    public void collectionGroupMap(){
        Map<String, Integer> collectionGroupMap=new HashMap<>();
        collectionGroupMap.put(RecapCommonConstants.COLLECTION_GROUP_DESIGNATION,1);
        mockDBReportUtil.setCollectionGroupMap(collectionGroupMap);
        assertEquals(collectionGroupMap,mockDBReportUtil.getCollectionGroupMap());
    }

    private ItemEntity getItemEntity() {
        ItemEntity itemEntity=new ItemEntity();
        itemEntity.setOwningInstitutionItemId("4");
        itemEntity.setBarcode("123456");
        itemEntity.setCustomerCode("PA");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCreatedDate(new Date());
        itemEntity.setLastUpdatedDate(new Date());
        return itemEntity;
    }

    private HoldingsEntity getHoldingsEntity() {
        HoldingsEntity holdingsEntity=new HoldingsEntity();
        holdingsEntity.setOwningInstitutionHoldingsId("3");
        return holdingsEntity;
    }

    private BibliographicEntity getBibliographicEntity() {
        BibliographicEntity bibliographicEntity=new BibliographicEntity();
        bibliographicEntity.setId(1);
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("2");
        bibliographicEntity.setContent(content.getBytes());
        return bibliographicEntity;
    }
}
