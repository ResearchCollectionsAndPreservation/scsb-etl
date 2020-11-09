package org.recap.util;


import org.apache.commons.lang3.StringUtils;
import org.recap.RecapCommonConstants;
import org.recap.model.jaxb.marc.CollectionType;
import org.recap.model.jaxb.marc.RecordType;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jparw.ReportDataEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by peris on 8/10/16.
 */
@Component
public class DBReportUtil {

    private Map<String, Integer> institutionEntitiesMap;
    private Map<String, Integer> collectionGroupMap;

    /**
     * Gets institution entities map.
     *
     * @return the institution entities map
     */
    public Map<String, Integer> getInstitutionEntitiesMap() {
        return institutionEntitiesMap;
    }

    /**
     * Sets institution entities map.
     *
     * @param institutionEntitiesMap the institution entities map
     */
    public void setInstitutionEntitiesMap(Map<String, Integer> institutionEntitiesMap) {
        this.institutionEntitiesMap = institutionEntitiesMap;
    }

    /**
     * Gets collection group map.
     *
     * @return the collection group map
     */
    public Map<String, Integer> getCollectionGroupMap() {
        return collectionGroupMap;
    }

    /**
     * Sets collection group map.
     *
     * @param collectionGroupMap the collection group map
     */
    public void setCollectionGroupMap(Map<String, Integer> collectionGroupMap) {
        this.collectionGroupMap = collectionGroupMap;
    }

    /**
     * Generate bib holdings and items failure report entities list from bibliographic, holdings and item entity.
     *
     * @param bibliographicEntity the bibliographic entity
     * @param holdingsEntity      the holdings entity
     * @param itemEntity          the item entity
     * @return the list
     */
    public List<ReportDataEntity> generateBibHoldingsAndItemsFailureReportEntities(BibliographicEntity bibliographicEntity, HoldingsEntity holdingsEntity, ItemEntity itemEntity) {
        List<ReportDataEntity> reportEntities = new ArrayList<>();
        reportEntities.addAll(generateBibHoldingsFailureReportEntity(bibliographicEntity, holdingsEntity));

        if (itemEntity != null) {
            if(StringUtils.isNotBlank(itemEntity.getOwningInstitutionItemId())) {
                ReportDataEntity localItemIdReportDataEntity = new ReportDataEntity();
                localItemIdReportDataEntity.setHeaderName(RecapCommonConstants.LOCAL_ITEM_ID);
                localItemIdReportDataEntity.setHeaderValue(itemEntity.getOwningInstitutionItemId());
                reportEntities.add(localItemIdReportDataEntity);
            }

            if(StringUtils.isNotBlank(itemEntity.getBarcode())) {
                ReportDataEntity itemBarcodeReportDataEntity = new ReportDataEntity();
                itemBarcodeReportDataEntity.setHeaderName(RecapCommonConstants.ITEM_BARCODE);
                itemBarcodeReportDataEntity.setHeaderValue(itemEntity.getBarcode());
                reportEntities.add(itemBarcodeReportDataEntity);
            }

            if(StringUtils.isNotBlank(itemEntity.getCustomerCode())) {
                ReportDataEntity customerCodeReportDataEntity = new ReportDataEntity();
                customerCodeReportDataEntity.setHeaderName(RecapCommonConstants.CUSTOMER_CODE);
                customerCodeReportDataEntity.setHeaderValue(itemEntity.getCustomerCode());
                reportEntities.add(customerCodeReportDataEntity);
            }

            if (itemEntity.getCollectionGroupId() != null) {
                for (Map.Entry<String, Integer> entry : collectionGroupMap.entrySet()) {
                    if (entry.getValue().equals(itemEntity.getCollectionGroupId())) {
                        ReportDataEntity collectionGroupDesignationEntity = new ReportDataEntity();
                        collectionGroupDesignationEntity.setHeaderName(RecapCommonConstants.COLLECTION_GROUP_DESIGNATION);
                        collectionGroupDesignationEntity.setHeaderValue(entry.getKey());
                        reportEntities.add(collectionGroupDesignationEntity);
                        break;
                    }
                }
            }

            if(itemEntity.getCreatedDate() != null) {
                ReportDataEntity createDateItemEntity = new ReportDataEntity();
                createDateItemEntity.setHeaderName(RecapCommonConstants.CREATE_DATE_ITEM);
                createDateItemEntity.setHeaderValue(new SimpleDateFormat("MM-dd-yyyy").format(itemEntity.getCreatedDate()));
                reportEntities.add(createDateItemEntity);
            }

            if(itemEntity.getLastUpdatedDate() != null) {
                ReportDataEntity lastUpdateItemEntity = new ReportDataEntity();
                lastUpdateItemEntity.setHeaderName(RecapCommonConstants.LAST_UPDATED_DATE_ITEM);
                lastUpdateItemEntity.setHeaderValue(new SimpleDateFormat("MM-dd-yyyy").format(itemEntity.getLastUpdatedDate()));
                reportEntities.add(lastUpdateItemEntity);
            }

        }
        return reportEntities;
    }


    /**
     * Generate bib holdings failure report entity list from bibliographic and holdings entity.
     *
     * @param bibliographicEntity the bibliographic entity
     * @param holdingsEntity      the holdings entity
     * @return the list
     */
    public List<ReportDataEntity> generateBibHoldingsFailureReportEntity(BibliographicEntity bibliographicEntity, HoldingsEntity holdingsEntity) {
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        reportDataEntities.addAll(generateBibFailureReportEntity(bibliographicEntity));
        if (holdingsEntity != null && StringUtils.isNotBlank(holdingsEntity.getOwningInstitutionHoldingsId())) {
                ReportDataEntity owningInstitutionHoldingsIdReportDataEntity = new ReportDataEntity();
                owningInstitutionHoldingsIdReportDataEntity.setHeaderName(RecapCommonConstants.OWNING_INSTITUTION_HOLDINGS_ID);
                owningInstitutionHoldingsIdReportDataEntity.setHeaderValue(holdingsEntity.getOwningInstitutionHoldingsId());
                reportDataEntities.add(owningInstitutionHoldingsIdReportDataEntity);
            }
        return reportDataEntities;
    }

    /**
     * Generate bib failure report entity list from bibliographic entity.
     *
     * @param bibliographicEntity the bibliographic entity
     * @return the list
     */
    public List<ReportDataEntity> generateBibFailureReportEntity(BibliographicEntity bibliographicEntity) {
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();

        ReportDataEntity owningInstitutionReportDataEntity = new ReportDataEntity();

        if (bibliographicEntity.getOwningInstitutionId() != null) {
            for (Map.Entry<String, Integer> entry : institutionEntitiesMap.entrySet()) {
                if (entry.getValue().equals(bibliographicEntity.getOwningInstitutionId())) {
                    owningInstitutionReportDataEntity.setHeaderName(RecapCommonConstants.OWNING_INSTITUTION);
                    owningInstitutionReportDataEntity.setHeaderValue(entry.getKey());
                    reportDataEntities.add(owningInstitutionReportDataEntity);
                    break;
                }
            }
        }

        if(StringUtils.isNotBlank(bibliographicEntity.getOwningInstitutionBibId())) {
            ReportDataEntity owningInstitutionBibIdReportDataEntity = new ReportDataEntity();
            owningInstitutionBibIdReportDataEntity.setHeaderName(RecapCommonConstants.OWNING_INSTITUTION_BIB_ID);
            owningInstitutionBibIdReportDataEntity.setHeaderValue(bibliographicEntity.getOwningInstitutionBibId());
            reportDataEntities.add(owningInstitutionBibIdReportDataEntity);
        }

        String content = new String(bibliographicEntity.getContent());
        if (StringUtils.isNotBlank(content)) {
            CollectionType collectionType = new CollectionType();
            collectionType = (CollectionType) collectionType.deserialize(content);
            if (collectionType != null && !CollectionUtils.isEmpty(collectionType.getRecord())) {
                RecordType recordType = collectionType.getRecord().get(0);
                if (recordType != null) {
                    String title = new MarcUtil().getDataFieldValue(recordType, "245", null, null, "a");
                    if(StringUtils.isNotBlank(title)) {
                        ReportDataEntity titleReportDataEntity = new ReportDataEntity();
                        titleReportDataEntity.setHeaderName(RecapCommonConstants.TITLE);
                        titleReportDataEntity.setHeaderValue(title.trim());
                        reportDataEntities.add(titleReportDataEntity);
                    }
                }
            }
        }
        return reportDataEntities;
    }
}
