package org.recap.model.export;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.BibliographicDetailsRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by premkb on 2/10/16.
 */
public class FullDataDumpCallableUT extends BaseTestCaseUT {

    @Mock
    ApplicationContext appContext;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    DataDumpCallableHelperService dataDumpCallableHelperService;

    @Test
    public void call() throws Exception {
        int pageNum = 0;
        int batchSize = 1000;
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setFetchType("1");
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        dataDumpRequest.setCollectionGroupIds(cgIds);
        Mockito.when(appContext.getBean(DataDumpCallableHelperService.class)).thenReturn(dataDumpCallableHelperService);
        FullDataDumpCallable fullDataDumpCallable=new FullDataDumpCallable(pageNum,batchSize,dataDumpRequest,bibliographicDetailsRepository);
        ReflectionTestUtils.setField(fullDataDumpCallable,"appContext",appContext);
        Mockito.when(dataDumpCallableHelperService.getIncrementalDataDumpRecords(pageNum,batchSize,dataDumpRequest,bibliographicDetailsRepository)).thenReturn(saveAndGetBibliographicEntities());
        Object fullDataDumpCallable1=fullDataDumpCallable.call();
        assertNotNull(fullDataDumpCallable1);
    }

    private List<BibliographicEntity> saveAndGetBibliographicEntities() throws URISyntaxException, IOException {
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("bib content".getBytes());
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("2");
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setLastUpdatedBy("tst");

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("holding content".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId("3");

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId("4");
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("tst");
        String barcode = "0306";
        itemEntity.setBarcode(barcode);
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("1");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setCopyNumber(123);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));
        itemEntity.setBibliographicEntities(bibliographicEntities);
        bibliographicEntities.add(bibliographicEntity);
        return bibliographicEntities;
    }
}
