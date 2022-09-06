package org.recap.camel;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.repository.ItemDetailsRepository;
import org.recap.service.BibliographicRepositoryDAO;
import org.recap.util.DBReportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by pvsubrah on 6/26/16.
 */
@Slf4j
@Component
public class BibDataProcessor {



    private String xmlFileName;

    private String institutionName;

    @Autowired
    private ProducerTemplate producer;

    @Autowired
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    private ItemDetailsRepository itemDetailsRepository;

    @Autowired
    private DBReportUtil dbReportUtil;

    @Autowired
    private EtlDataLoadDAOService etlDataLoadDAOService;

    @Autowired
    BibliographicRepositoryDAO bibliographicRepositoryDAO;


    /**
     * This method persists the bibliographic entities. If errors encountered prepares report entities and sends to report queue for persisting.
     *
     * @param etlExchange
     */
    public void processETLExchagneAndPersistToDB(ETLExchange etlExchange) {
        ReportEntity reportEntity = null;
        if (etlExchange != null) {
            List<BibliographicEntity> bibliographicEntityList = etlExchange.getBibliographicEntities();
            try {
                bibliographicRepositoryDAO.saveOrUpdateList(bibliographicEntityList);
            } catch (PersistenceException pe){
                try {
                    processRecordWhenDuplicateBarcodeException(bibliographicEntityList, pe);
                } catch(Exception e) {
                    log.info("exception while eliminating..");
                    log.error(ScsbConstants.ERROR,e);
                }
            } catch (Exception e) {
                log.error(ScsbConstants.ERROR,e);
                etlDataLoadDAOService.clearSession();
                dbReportUtil.setCollectionGroupMap(etlExchange.getCollectionGroupMap());
                dbReportUtil.setInstitutionEntitiesMap(etlExchange.getInstitutionEntityMap());
                for (BibliographicEntity bibliographicEntity : bibliographicEntityList) {
                    try {
                        etlDataLoadDAOService.saveBibliographicEntity(bibliographicEntity);
                    } catch (Exception ex) {
                        log.error(ScsbConstants.ERROR,ex);
                        etlDataLoadDAOService.clearSession();
                        reportEntity = processBibHoldingsItems(dbReportUtil, bibliographicEntity);
                    }
                }
            }
            if (null != reportEntity) {
                producer.sendBody(ScsbConstants.ETL_REPORT_Q, reportEntity);
            }
        }
    }

    private void processRecordWhenDuplicateBarcodeException(List<BibliographicEntity> bibliographicEntityList, PersistenceException pe) {
        log.error("persistence exe-->",pe);
        etlDataLoadDAOService.clearSession();
        for(BibliographicEntity bibliographicEntity:bibliographicEntityList){
            List<ReportEntity> reportEntityList = processDuplicatedRecord(bibliographicEntity);
            for(ReportEntity reportEntity1:reportEntityList){
                producer.sendBody(ScsbConstants.ETL_REPORT_Q, reportEntity1);
            }
        }
    }

    private List<ReportEntity> processDuplicatedRecord(BibliographicEntity bibliographicEntity) {
        List<ReportEntity> reportEntityList = new ArrayList<>();
        bibliographicEntity.setItemEntities(new ArrayList<>());
        List<HoldingsEntity> updatedHoldingEntityList = new ArrayList<>();
        List<ItemEntity> nonDuplicateItemsForABib = new ArrayList<>();//This list is used to eliminate the duplicate barcode if found in the same bib
        for (HoldingsEntity holdingsEntity:bibliographicEntity.getHoldingsEntities()){
            List<ItemEntity> itemEntityListWithNoDuplicatedBarcode = new ArrayList<>();
            for (ItemEntity itemEntity:holdingsEntity.getItemEntities()){
                List<ItemEntity> existingItemEntityList = itemDetailsRepository.findByBarcode(itemEntity.getBarcode());
                log.info("Processing own bib {}, own hold id {}, own item id {}",bibliographicEntity.getOwningInstitutionBibId(),
                        holdingsEntity.getOwningInstitutionHoldingsId(),itemEntity.getOwningInstitutionItemId());
                if(isDuplicateItem(existingItemEntityList,itemEntity))   {//Add to report if duplicate barcode found
                    log.info("existing barcode--->{}",existingItemEntityList.get(0).getBarcode());
                    ItemEntity existingItemEntity = existingItemEntityList.get(0);
                    ReportEntity reportEntity = setDuplicateBarcodeReportInfo(itemEntity.getBarcode(),existingItemEntity,dbReportUtil,bibliographicEntity, holdingsEntity, itemEntity);
                    reportEntityList.add(reportEntity);
                } else {
                    log.info("Duplicate not found for the barcode-->{}",itemEntity.getBarcode());
                    ItemEntity existingItemEntity = getExistingBarcodeItemWithinSameBib(nonDuplicateItemsForABib,itemEntity);//If the item is not available in db but barcode is duplicated in the same bib,so this is to find the barcode already added to the bib to save, if the barcode is already there the current item which is having same barcode will be eliminated by below conditions
                    if (existingItemEntity == null) {
                        itemEntityListWithNoDuplicatedBarcode.add(itemEntity);
                        nonDuplicateItemsForABib.add(itemEntity);
                    } else {
                        ReportEntity reportEntity = setDuplicateBarcodeReportInfoForItemsinSameBib(existingItemEntity.getBarcode(),existingItemEntity,dbReportUtil,bibliographicEntity, holdingsEntity, itemEntity);
                        reportEntityList.add(reportEntity);
                    }
                }
            }
            if (!itemEntityListWithNoDuplicatedBarcode.isEmpty()) {
                holdingsEntity.setItemEntities(itemEntityListWithNoDuplicatedBarcode);
                updatedHoldingEntityList.add(holdingsEntity);
                bibliographicEntity.getItemEntities().addAll(itemEntityListWithNoDuplicatedBarcode);
            }
        }
        if (!updatedHoldingEntityList.isEmpty()) {
            bibliographicEntity.setHoldingsEntities(updatedHoldingEntityList);
            log.info("saving bib id--->{} item ids - barcodes-->{}",bibliographicEntity.getOwningInstitutionBibId(),getBarcodeList(bibliographicEntity.getItemEntities()));
            etlDataLoadDAOService.saveBibliographicEntity(bibliographicEntity);
        }
        return reportEntityList;
    }

    private static boolean isDuplicateItem(List<ItemEntity> existingItemEntityList,ItemEntity itemEntity ){
        boolean isDuplicate = true;
        if(!existingItemEntityList.isEmpty()){
            ItemEntity existingItemEntity = existingItemEntityList.get(0);
            if(existingItemEntity.getOwningInstitutionItemId().equals(itemEntity.getOwningInstitutionItemId())){//verifies it is a bound-with item if bib comes in a separate batch
                isDuplicate = false;
            }
        } else {
            isDuplicate = false;
        }
        return isDuplicate;
    }

    private static String getBarcodeList(List<ItemEntity> itemEntityList){
        StringBuilder stringBuilder= new StringBuilder();
        for(ItemEntity itemEntity:itemEntityList){
            stringBuilder.append(itemEntity.getOwningInstitutionItemId()).append("-").append(itemEntity.getBarcode()).append(",");
        }
        return stringBuilder.toString();
    }

    private static ItemEntity getExistingBarcodeItemWithinSameBib(List<ItemEntity> existingItemList,ItemEntity itemEntity){
        for(ItemEntity existingItem:existingItemList){
            if(existingItem.getBarcode().equals(itemEntity.getBarcode())){
                return existingItem;
            }
        }
        return null;
    }

    private ReportEntity setDuplicateBarcodeReportInfo(String barcode, ItemEntity existingItemEntity,DBReportUtil dbReportUtil, BibliographicEntity bibliographicEntity, HoldingsEntity holdingsEntity, ItemEntity itemEntity){
        String failureMessage = "Item barcode "+barcode+" is duplicated, existing record info owning inst bib id "
                +existingItemEntity.getBibliographicEntities().get(0).getOwningInstitutionBibId()
                +", owning inst holding id "+existingItemEntity.getHoldingsEntities().get(0).getOwningInstitutionHoldingsId()
                +", owning inst item id "+existingItemEntity.getOwningInstitutionItemId();
        ReportEntity reportEntity = new ReportEntity();
        setItemFailureReportInfo(dbReportUtil, bibliographicEntity, reportEntity, holdingsEntity, itemEntity, null,failureMessage);
        return reportEntity;
    }

    private ReportEntity setDuplicateBarcodeReportInfoForItemsinSameBib(String barcode, ItemEntity existingItemEntity,DBReportUtil dbReportUtil, BibliographicEntity bibliographicEntity, HoldingsEntity holdingsEntity, ItemEntity itemEntity){
        String failureMessage = "Item barcode "+barcode+" is duplicated, existing record info owning inst bib id "
                +bibliographicEntity.getOwningInstitutionBibId()
                +", owning inst holding id "+holdingsEntity.getOwningInstitutionHoldingsId()
                +", owning inst item id "+existingItemEntity.getOwningInstitutionItemId();
        ReportEntity reportEntity = new ReportEntity();
        setItemFailureReportInfo(dbReportUtil, bibliographicEntity, reportEntity, holdingsEntity, itemEntity, null,failureMessage);
        return reportEntity;
    }
    /**
     * Persists bibliographic entities and returns a report entity if an exception is encountered.
     *
     * @param dbReportUtil
     * @param bibliographicEntity
     * @return
     */
    @Transactional
    public ReportEntity processBibHoldingsItems(DBReportUtil dbReportUtil, BibliographicEntity bibliographicEntity) {
        ReportEntity reportEntity = new ReportEntity();

        List<HoldingsEntity> savedHoldingsEntities = new ArrayList<>();
        List<ItemEntity> savedItemEntities = new ArrayList<>();

        try {
            List<HoldingsEntity> holdingsEntities = bibliographicEntity.getHoldingsEntities();
            bibliographicEntity.setHoldingsEntities(null);
            bibliographicEntity.setItemEntities(null);

            etlDataLoadDAOService.saveBibliographicEntity(bibliographicEntity);
            for (HoldingsEntity holdingsEntity : holdingsEntities) {
                List<ItemEntity> itemEntities = holdingsEntity.getItemEntities();
                holdingsEntity.setItemEntities(null);
                try {
                    HoldingsEntity savedHoldingsEntity = bibliographicRepositoryDAO.saveOrUpdateHoldingsEntity(holdingsEntity);
                    savedHoldingsEntities.add(savedHoldingsEntity);
                    for (ItemEntity itemEntity : itemEntities) {
                        try {
                            itemEntity.setHoldingsEntities(Arrays.asList(savedHoldingsEntity));
                            ItemEntity savedItemEntity = bibliographicRepositoryDAO.saveOrUpdateItemEntity(itemEntity);
                            savedItemEntities.add(savedItemEntity);
                        } catch (Exception itemEx) {
                            log.error(ScsbConstants.ERROR,itemEx);
                            etlDataLoadDAOService.clearSession();
                            setItemFailureReportInfo(dbReportUtil, bibliographicEntity, reportEntity, holdingsEntity, itemEntity, itemEx,null);
                        }
                    }
                } catch (Exception holdingsEx) {
                    log.error(ScsbConstants.ERROR,holdingsEx);
                    etlDataLoadDAOService.clearSession();
                    List<ReportDataEntity> reportDataEntities = dbReportUtil.generateBibHoldingsFailureReportEntity(bibliographicEntity, holdingsEntity);
                    reportEntity.setReportDataEntities(reportDataEntities);
                    ReportDataEntity exceptionReportDataEntity = new ReportDataEntity();
                    exceptionReportDataEntity.setHeaderName(ScsbConstants.EXCEPTION_MESSAGE);
                    exceptionReportDataEntity.setHeaderValue(ExceptionUtils.getMessage(holdingsEx));
                    reportDataEntities.add(exceptionReportDataEntity);
                    reportEntity.setFileName(xmlFileName);
                    reportEntity.setCreatedDate(new Date());
                    reportEntity.setType(ScsbCommonConstants.FAILURE);
                    reportEntity.setInstitutionName(institutionName);
                }
            }
            bibliographicEntity.setHoldingsEntities(savedHoldingsEntities);
            bibliographicEntity.setItemEntities(savedItemEntities);
            etlDataLoadDAOService.saveBibliographicEntity(bibliographicEntity);
        } catch (Exception bibEx) {
            log.error(ScsbConstants.ERROR,bibEx);
            etlDataLoadDAOService.clearSession();
            List<ReportDataEntity> reportDataEntities = dbReportUtil.generateBibFailureReportEntity(bibliographicEntity);

            ReportDataEntity exceptionReportDataEntity = new ReportDataEntity();
            exceptionReportDataEntity.setHeaderName(ScsbConstants.EXCEPTION_MESSAGE);

            if(bibEx.getCause() != null && bibEx.getCause().getCause() != null) {
                exceptionReportDataEntity.setHeaderValue(bibEx.getCause().getCause().getMessage());
            } else {
                exceptionReportDataEntity.setHeaderValue(bibEx.getMessage());
            }
            reportDataEntities.add(exceptionReportDataEntity);
            reportEntity.setFileName(xmlFileName);
            reportEntity.setCreatedDate(new Date());
            reportEntity.setType(ScsbCommonConstants.FAILURE);
            reportEntity.setInstitutionName(institutionName);
        }
        return reportEntity;
    }

    private void setItemFailureReportInfo(DBReportUtil dbReportUtil, BibliographicEntity bibliographicEntity, ReportEntity reportEntity, HoldingsEntity holdingsEntity, ItemEntity itemEntity,
                                          Exception itemEx,String failureMessage) {
        List<ReportDataEntity> reportDataEntities = dbReportUtil.generateBibHoldingsAndItemsFailureReportEntities(bibliographicEntity, holdingsEntity, itemEntity);
        ReportDataEntity exceptionReportDataEntity = new ReportDataEntity();
        if (failureMessage!=null) {
            exceptionReportDataEntity.setHeaderName(ScsbCommonConstants.ERROR_DESCRIPTION);
            exceptionReportDataEntity.setHeaderValue(failureMessage);
        } else {
            exceptionReportDataEntity.setHeaderName(ScsbConstants.EXCEPTION_MESSAGE);
            exceptionReportDataEntity.setHeaderValue(itemEx.getCause().getCause().getMessage());
        }
        reportDataEntities.add(exceptionReportDataEntity);
        reportEntity.setReportDataEntities(reportDataEntities);
        reportEntity.setFileName(xmlFileName);
        reportEntity.setCreatedDate(new Date());
        reportEntity.setType(ScsbCommonConstants.FAILURE);
        reportEntity.setInstitutionName(institutionName);
    }

    public String getXmlFileName() {
        return xmlFileName;
    }

    public void setXmlFileName(String xmlFileName) {
        this.xmlFileName = xmlFileName;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }
}
