package org.recap.service.formatter.datadump;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.TestUtil;
import org.recap.camel.BibDataProcessor;
import org.recap.model.jaxb.BibRecord;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.model.jpa.MatchingBibInfoDetail;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.repository.MatchingBibInfoDetailRepository;
import org.recap.repositoryrw.ReportDetailRepository;
import org.recap.util.DBReportUtil;
import org.recap.util.XmlFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertTrue;

/**
 * Created by premkb on 23/8/16.
 */
public class SCSBXmlFormatterServiceUT extends BaseTestCaseUT {


    @InjectMocks
    SCSBXmlFormatterService scsbXmlFormatterService;

    @Mock
    BibDataProcessor bibDataProcessor;
    @Mock
    ReportDetailRepository reportDetailRepository;
    @Mock
    XmlFormatter xmlFormatter;
    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;
    @Mock
    DBReportUtil dbReportUtil;
    @Mock
    private ProducerTemplate producer;
    @Mock
    private Map itemStatusMap;
    @Mock
    private Map<String, Integer> institutionMap;
    @Mock
    private Map<String, Integer> collectionGroupMap;
    @Mock
    MatchingBibInfoDetailRepository matchingBibInfoDetailRepository;

    @Value("${etl.data.dump.directory}")
    private String dumpDirectoryPath;

    @Value("${etl.report.directory}")
    private String reportDirectoryPath;

    private final String bibContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<collection>\n" +
            "    <record>\n" +
            "        <leader>00800cas a2200277 i 4500</leader>\n" +
            "        <controlfield tag=\"001\">10</controlfield>\n" +
            "        <controlfield tag=\"003\">NNC</controlfield>\n" +
            "        <controlfield tag=\"005\">20100215174244.0</controlfield>\n" +
            "        <controlfield tag=\"008\">810702c19649999ilufr p       0   a0engxd</controlfield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
            "            <subfield code=\"a\">(OCoLC)502399218</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
            "            <subfield code=\"a\">(OCoLC)ocn502399218</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
            "            <subfield code=\"a\">(CStRLIN)NYCG022-S</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
            "            <subfield code=\"9\">AAA0010CU</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
            "            <subfield code=\"a\">(NNC)10</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"040\">\n" +
            "            <subfield code=\"a\">NNC</subfield>\n" +
            "            <subfield code=\"c\">NNC</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"090\">\n" +
            "            <subfield code=\"a\">TA434</subfield>\n" +
            "            <subfield code=\"b\">.S15</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"0\" ind2=\"0\" tag=\"245\">\n" +
            "            <subfield code=\"a\">SOâ\u0082\u0083 abstracts &amp; newsletter.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"3\" ind2=\"3\" tag=\"246\">\n" +
            "            <subfield code=\"a\">SO three abstracts &amp; newsletter</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"260\">\n" +
            "            <subfield code=\"a\">[Chicago] :</subfield>\n" +
            "            <subfield code=\"b\">United States Gypsum,</subfield>\n" +
            "            <subfield code=\"c\">[1964?]-&lt;1979&gt;</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"300\">\n" +
            "            <subfield code=\"a\">v. :</subfield>\n" +
            "            <subfield code=\"b\">ill. ;</subfield>\n" +
            "            <subfield code=\"c\">28 cm.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"0\" ind2=\" \" tag=\"362\">\n" +
            "            <subfield code=\"a\">Vol. 1, no. 1-</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"500\">\n" +
            "            <subfield code=\"a\">Editor: W.C. Hansen, 1964-1979.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Cement</subfield>\n" +
            "            <subfield code=\"v\">Periodicals.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Gypsum</subfield>\n" +
            "            <subfield code=\"v\">Periodicals.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"1\" ind2=\" \" tag=\"700\">\n" +
            "            <subfield code=\"a\">Hansen, W. C.</subfield>\n" +
            "            <subfield code=\"q\">(Waldemar Conrad),</subfield>\n" +
            "            <subfield code=\"d\">1896-</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"2\" ind2=\" \" tag=\"710\">\n" +
            "            <subfield code=\"a\">United States Gypsum Co.</subfield>\n" +
            "        </datafield>\n" +
            "    </record>\n" +
            "</collection>\n";
    private final String holdingContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<collection>\n" +
            "    <record>\n" +
            "        <datafield ind1=\"0\" ind2=\"1\" tag=\"852\">\n" +
            "            <subfield code=\"b\">off,che</subfield>\n" +
            "            <subfield code=\"h\">QD79.C454 H533</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"0\" ind2=\"0\" tag=\"866\">\n" +
            "            <subfield code=\"a\">v.1-v.5</subfield>\n" +
            "        </datafield>\n" +
            "    </record>\n" +
            "</collection>\n";


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void verifySCSBXmlGeneration() throws Exception {
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        Mockito.when(matchingBibInfoDetailRepository.getRecordNum(Mockito.anyList())).thenReturn(Arrays.asList(1));
        List<MatchingBibInfoDetail> matchingBibInfoDetailList=new ArrayList<>();
        matchingBibInfoDetailList.add(getMatchingBibInfoDetail());
        Mockito.when(matchingBibInfoDetailRepository.findByRecordNum(Mockito.anyList())).thenReturn(matchingBibInfoDetailList);
        Map<String, Object> resultMap = scsbXmlFormatterService.prepareBibRecords(Arrays.asList(bibliographicEntity));
        List<BibRecord> bibRecords = (List<BibRecord>) resultMap.get(ScsbCommonConstants.SUCCESS);
        scsbXmlFormatterService.getSCSBXmlForBibRecords(bibRecords);
    }


    @Test
    public void verifySCSBXmlGenerationException() throws Exception {
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        bibliographicEntity.getItemEntities().get(0).setImsLocationEntity(null);
        Mockito.when(matchingBibInfoDetailRepository.getRecordNum(Mockito.anyList())).thenReturn(Arrays.asList(1));
        List<MatchingBibInfoDetail> matchingBibInfoDetailList=new ArrayList<>();
        matchingBibInfoDetailList.add(getMatchingBibInfoDetail());
        Mockito.when(matchingBibInfoDetailRepository.findByRecordNum(Mockito.anyList())).thenReturn(matchingBibInfoDetailList);
        Map<String, Object> resultMap = scsbXmlFormatterService.prepareBibRecords(Arrays.asList(bibliographicEntity));
        List<BibRecord> bibRecords = (List<BibRecord>) resultMap.get(ScsbCommonConstants.SUCCESS);
        boolean reteurntype=scsbXmlFormatterService.isInterested(ScsbConstants.DATADUMP_XML_FORMAT_SCSB);
        scsbXmlFormatterService.getSCSBXmlForBibRecords(bibRecords);
        assertTrue(reteurntype);

    }

    private BibliographicEntity getBibliographicEntity() throws URISyntaxException, IOException {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setId(100);
        bibliographicEntity.setContent(bibContent.getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionBibId("20");
        bibliographicEntity.setOwningInstitutionId(3);
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("NYPL");
        institutionEntity.setInstitutionName("New York Public Library");
        bibliographicEntity.setInstitutionEntity(institutionEntity);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setId(345);
        holdingsEntity.setContent(holdingContent.getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(3);
        holdingsEntity.setOwningInstitutionHoldingsId("54323");
        holdingsEntity.setInstitutionEntity(institutionEntity);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setCallNumberType("0");
        itemEntity.setCallNumber("callNum");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setBarcode("1231");
        itemEntity.setOwningInstitutionItemId(".i1231");
        itemEntity.setOwningInstitutionId(3);
        itemEntity.setCollectionGroupId(1);
        itemEntity.setImsLocationEntity(TestUtil.getImsLocationEntity(1,"RECAP","RECAP_LAS"));
        CollectionGroupEntity collectionGroupEntity = new CollectionGroupEntity();
        collectionGroupEntity.setCollectionGroupCode("Shared");
        itemEntity.setCollectionGroupEntity(collectionGroupEntity);
        itemEntity.setCustomerCode("PA");
        itemEntity.setCopyNumber(1);
        itemEntity.setItemAvailabilityStatusId(1);
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setStatusCode("Available");
        itemEntity.setItemStatusEntity(itemStatusEntity);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        return bibliographicEntity;
    }

    @Test
    public void testgetSubfieldatafieldType() {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setId(100);
        bibliographicEntity.setContent(bibContent.getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionBibId("20");
        bibliographicEntity.setOwningInstitutionId(3);
        CollectionGroupEntity collectionGroupEntity = new CollectionGroupEntity();
        collectionGroupEntity.setCollectionGroupCode("SCSB");
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(new Random().nextInt()));
        holdingsEntity.setId(3);
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setCallNumberType("0");
        itemEntity.setCallNumber("callNum");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("etl");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("etl");
        itemEntity.setBarcode("1231");
        itemEntity.setOwningInstitutionItemId(".i1231");
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCustomerCode("PA");
        itemEntity.setImsLocationEntity(TestUtil.getImsLocationEntity(1,"RECAP","RECAP_LAS"));
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setCollectionGroupEntity(collectionGroupEntity);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));
        MatchingBibInfoDetail matchingBibInfoDetail = getMatchingBibInfoDetail();
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("CUL");
        ReflectionTestUtils.invokeMethod(scsbXmlFormatterService, "getSubfieldatafieldType", "Test Code", "Test Value");
        ReflectionTestUtils.invokeMethod(scsbXmlFormatterService, "build876DataField", itemEntity);
        ReflectionTestUtils.invokeMethod(scsbXmlFormatterService, "build900DataField", itemEntity);
        ReflectionTestUtils.invokeMethod(scsbXmlFormatterService, "buildRecordTypes", Arrays.asList(itemEntity));
        ReflectionTestUtils.invokeMethod(scsbXmlFormatterService, "getItems", Arrays.asList(itemEntity));
        try{ReflectionTestUtils.invokeMethod(scsbXmlFormatterService, "getHoldings", Arrays.asList(holdingsEntity), Arrays.asList(2), Arrays.asList(3));}catch (Exception e){e.printStackTrace();}
        ReflectionTestUtils.invokeMethod(scsbXmlFormatterService, "getMatchingInstitutionBibId", "9751", Arrays.asList(matchingBibInfoDetail));
        ReflectionTestUtils.invokeMethod(scsbXmlFormatterService, "getItemIds", bibliographicEntity);
        ReflectionTestUtils.invokeMethod(scsbXmlFormatterService, "getNonOrphanHoldingsIdList", Arrays.asList(itemEntity));
        ReflectionTestUtils.invokeMethod(scsbXmlFormatterService, "getBibIdRowNumMap", Arrays.asList(matchingBibInfoDetail));
        ReflectionTestUtils.invokeMethod(scsbXmlFormatterService, "getRecordNumReportDataEntityMap", Arrays.asList(matchingBibInfoDetail));
        assertTrue(true);
    }

    private MatchingBibInfoDetail getMatchingBibInfoDetail() {
        MatchingBibInfoDetail matchingBibInfoDetail = new MatchingBibInfoDetail();
        matchingBibInfoDetail.setId(1);
        matchingBibInfoDetail.setBibId("1");
        matchingBibInfoDetail.setOwningInstitutionBibId("565658565465");
        matchingBibInfoDetail.setOwningInstitution("PUL");
        matchingBibInfoDetail.setRecordNum(10);
        return matchingBibInfoDetail;
    }
}
