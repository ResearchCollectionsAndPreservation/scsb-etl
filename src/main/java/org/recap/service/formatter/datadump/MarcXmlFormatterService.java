package org.recap.service.formatter.datadump;

import org.apache.commons.collections.CollectionUtils;
import org.marc4j.MarcReader;
import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlReader;
import org.marc4j.MarcXmlWriter;
import org.marc4j.marc.*;
import org.recap.RecapConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by premkb on 28/9/16.
 */
@Service
@Scope("prototype")
public class MarcXmlFormatterService implements DataDumpFormatterInterface {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(MarcXmlFormatterService.class);

    @Value("${datadump.marc.pul}")
    private String holdingPUL;

    @Value("${datadump.marc.cul}")
    private String holdingCUL;

    @Value("${datadump.marc.nypl}")
    private String holdingNYPL;

    private MarcFactory factory;

    /**
     * Returns true if selected file format is Marc Xml format for deleted records data dump.
     *
     * @param formatType the format type
     * @return
     */
    @Override
    public boolean isInterested(String formatType) {
        return formatType.equals(RecapConstants.DATADUMP_XML_FORMAT_MARC) ? true : false;
    }


    /**
     * Prepare a map with marc records and failures for list of bibliographic entities.
     *
     * @param bibliographicEntities the bibliographic entities
     * @return the map
     */
    public Map<String, Object> prepareMarcRecords(List<BibliographicEntity> bibliographicEntities) {
        Map resultsMap = new HashMap();
        List<Record> records = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int itemExportedCount = 0;
        for (Iterator<BibliographicEntity> iterator = bibliographicEntities.iterator(); iterator.hasNext(); ) {
            BibliographicEntity bibliographicEntity = iterator.next();
            if(CollectionUtils.isNotEmpty(bibliographicEntity.getItemEntities())) {
                Map<String, Object> stringObjectMap = prepareMarcRecord(bibliographicEntity);
                Record record = (Record) stringObjectMap.get(RecapConstants.SUCCESS);
                if (null != record) {
                    records.add(record);
                    itemExportedCount = itemExportedCount + bibliographicEntity.getItemEntities().size();
                }
                String failureMsg = (String) stringObjectMap.get(RecapConstants.FAILURE);
                if (null != failureMsg) {
                    errors.add(failureMsg);
                }
            }
        }

        resultsMap.put(RecapConstants.SUCCESS, records);
        resultsMap.put(RecapConstants.FAILURE, errors);
        resultsMap.put(RecapConstants.ITEM_EXPORTED_COUNT, itemExportedCount);

        return resultsMap;
    }

    /**
     * Prepare a map with marc record or failure for a bibliographic entity.
     *
     * @param bibliographicEntity the bibliographic entity
     * @return the map
     */
    public Map<String, Object> prepareMarcRecord(BibliographicEntity bibliographicEntity) {
        Record record = null;
        Map results = new HashMap();
        try {
            record = getRecordFromContent(bibliographicEntity.getContent());
            update001Field(record, bibliographicEntity);
            stripTagsFromBib(record,Arrays.asList(RecapConstants.MarcFields.DF_852,RecapConstants.MarcFields.DF_876));
            add009Field(record, bibliographicEntity);
            List<Integer> itemIds = getItemIds(bibliographicEntity);
            record = addHoldingInfo(record, bibliographicEntity.getHoldingsEntities(),itemIds,getNonOrphanHoldingsIdList(bibliographicEntity.getItemEntities()));
            results.put(RecapConstants.SUCCESS, record);
        } catch (Exception e) {
            logger.info("failed bib own ins bib id--->{}",bibliographicEntity.getOwningInstitutionBibId());
            logger.error(RecapConstants.ERROR,e);
            results.put(RecapConstants.FAILURE, String.valueOf(e.getCause()));

        }
        return results;
    }

    /**
     * Remove selected tags from marc record.
     * @param record
     * @param tagList
     */
    private void stripTagsFromBib(Record record,List<String> tagList){
        for(Iterator<DataField> dataFieldIterator = record.getDataFields().iterator();dataFieldIterator.hasNext();) {
            DataField dataField = dataFieldIterator.next();
            for (String tag : tagList) {
                if (tag.equals(dataField.getTag())) {
                    dataFieldIterator.remove();
                }
            }
        }
    }

    /**
     * Gets item ids from bibliographic entity
     * @param bibliographicEntity
     * @return
     */
    private List<Integer> getItemIds(BibliographicEntity bibliographicEntity){
        List<Integer> itemIds = new ArrayList<>();
        List<ItemEntity> itemEntityList = bibliographicEntity.getItemEntities();
        for(ItemEntity itemEntity : itemEntityList){
            itemIds.add(itemEntity.getItemId());
        }
        return itemIds;
    }

    private List<Integer> getNonOrphanHoldingsIdList(List<ItemEntity> itemEntityList){
        Set<Integer> holdingsIdSet = new HashSet<>();
        for(ItemEntity itemEntity:itemEntityList){
            holdingsIdSet.add(itemEntity.getHoldingsEntities().get(0).getHoldingsId());
        }
        return new ArrayList<>(holdingsIdSet);
    }
    /**
     * Build marc record from byte array marc content.
     * @param content
     * @return
     */
    private Record getRecordFromContent(byte[] content) {
        MarcReader reader;
        Record record = null;
        InputStream inputStream = new ByteArrayInputStream(content);
        reader = new MarcXmlReader(inputStream);
        while (reader.hasNext()) {
            record = reader.next();
        }
        return record;
    }

    /**
     * Set 00l control field value with SCSB bibliographic id in the marc record.
     * @param record
     * @param bibliographicEntity
     */
    private void update001Field(Record record, BibliographicEntity bibliographicEntity) {
        boolean is001Available = false;
        for (ControlField controlField : record.getControlFields()) {
            if (RecapConstants.MarcFields.CF_001.equals(controlField.getTag())) {
                controlField.setData(RecapConstants.SCSB + "-" + bibliographicEntity.getBibliographicId());
                is001Available = true;
            }
        }
        if(!is001Available) {
            ControlField controlField = getFactory().newControlField(RecapConstants.MarcFields.CF_001);
            controlField.setData(RecapConstants.SCSB + "-" + bibliographicEntity.getBibliographicId());
            record.addVariableField(controlField);
        }
    }

    private void add009Field(Record record, BibliographicEntity bibliographicEntity){
        ControlField controlField = getFactory().newControlField(RecapConstants.MarcFields.CF_009);
        controlField.setData(bibliographicEntity.getOwningInstitutionBibId());
        record.addVariableField(controlField);
    }

    /**
     * Adds holdings information tags to the marc record.
     * @param record
     * @param holdingsEntityList
     * @param nonOrphanHoldingsIdList
     * @return
     */
    private Record addHoldingInfo(Record record, List<HoldingsEntity> holdingsEntityList,List<Integer> itemIds,List<Integer> nonOrphanHoldingsIdList) {
        Record holdingRecord;
        for (HoldingsEntity holdingsEntity : holdingsEntityList) {
            if (nonOrphanHoldingsIdList !=null && nonOrphanHoldingsIdList.contains(holdingsEntity.getHoldingsId())) {
                holdingRecord = getRecordFromContent(holdingsEntity.getContent());
                for (DataField dataField : holdingRecord.getDataFields()) {
                    if (RecapConstants.MarcFields.DF_852.equals(dataField.getTag())) {
                        addOrUpdateDatafield852Subfield0(dataField,holdingsEntity);
                        update852bField(dataField, holdingsEntity);
                        record.addVariableField(dataField);
                    }
                    if (RecapConstants.MarcFields.DF_866.equals(dataField.getTag())) {
                        if(dataField.getSubfield('a')!=null && (dataField.getSubfield('a').getData()==null || "".equals(dataField.getSubfield('a').getData()))){
                            continue;
                        }else {
                            addOrUpdateDatafield852Subfield0(dataField,holdingsEntity);
                            record.addVariableField(dataField);
                        }
                    }
                }
                for(ItemEntity itemEntity : holdingsEntity.getItemEntities()){
                    if(itemIds.contains(itemEntity.getItemId())) {
                        record = addItemInfo(record, itemEntity, holdingsEntity);
                    }
                }
            }
        }
        return record;
    }

    /**
     * Adds a '0' subfield with SCSB holdings id to the given data field.
     * @param dataField
     * @param holdingEntity
     */
    private void add0SubField(DataField dataField, HoldingsEntity holdingEntity) {
        dataField.addSubfield(getFactory().newSubfield('0', holdingEntity.getHoldingsId().toString()));
    }

    /**
     * Updates 852 0 field value with the scsb holding id information.
     * @param dataField
     * @param holdingEntity
     */
    private void addOrUpdateDatafield852Subfield0(DataField dataField, HoldingsEntity holdingEntity){
        List<Subfield> subfields = dataField.getSubfields('0');
        if(CollectionUtils.isNotEmpty(subfields)){
            for(Subfield subfield:subfields){
                if(subfield.getCode()=='0'){
                    subfield.setData(holdingEntity.getHoldingsId().toString());
                }
            }
        } else {
            add0SubField(dataField,holdingEntity);
        }
    }

    /**
     * Updates 852 b field with the institution information.
     * @param dataField
     * @param holdingEntity
     */
    private void update852bField(DataField dataField, HoldingsEntity holdingEntity){
        String partnerInfo = "";
        List<Subfield> subfields = dataField.getSubfields('b');
        if(CollectionUtils.isNotEmpty(subfields)) {
            for (Iterator<Subfield> iterator = subfields.iterator(); iterator.hasNext(); ) {
                Subfield subfield = iterator.next();
                dataField.removeSubfield(subfield);
            }
        }
        if (holdingEntity.getInstitutionEntity().getInstitutionCode().equals(RecapConstants.PRINCETON)) {
            partnerInfo = holdingPUL;
        } else if (holdingEntity.getInstitutionEntity().getInstitutionCode().equals(RecapConstants.COLUMBIA)) {
            partnerInfo = holdingCUL;
        } else if (holdingEntity.getInstitutionEntity().getInstitutionCode().equals(RecapConstants.NYPL)) {
            partnerInfo = holdingNYPL;
        }
        Subfield subfield = factory.newSubfield('b', partnerInfo);
        dataField.addSubfield(subfield);
    }

    /**
     * Adds item information tags to the marc record.
     * @param record
     * @param itemEntity
     * @param holdingsEntity
     * @return
     */
    private Record addItemInfo(Record record, ItemEntity itemEntity,HoldingsEntity holdingsEntity) {
        DataField dataField = getFactory().newDataField(RecapConstants.MarcFields.DF_876, ' ', ' ');
        dataField.addSubfield(getFactory().newSubfield('0', String.valueOf(holdingsEntity.getHoldingsId())));
        dataField.addSubfield(getFactory().newSubfield('3', itemEntity.getVolumePartYear() != null ? itemEntity.getVolumePartYear() : ""));
        dataField.addSubfield(getFactory().newSubfield('a', String.valueOf(itemEntity.getItemId())));
        dataField.addSubfield(getFactory().newSubfield('h', itemEntity.getUseRestrictions() != null ? itemEntity.getUseRestrictions() : ""));
        dataField.addSubfield(getFactory().newSubfield('j', itemEntity.getItemStatusEntity().getStatusCode()));
        dataField.addSubfield(getFactory().newSubfield('p', itemEntity.getBarcode()));
        dataField.addSubfield(getFactory().newSubfield('t', itemEntity.getCopyNumber() != null ? String.valueOf(itemEntity.getCopyNumber()) : ""));
        dataField.addSubfield(getFactory().newSubfield('x', itemEntity.getCollectionGroupEntity().getCollectionGroupCode()));
        dataField.addSubfield(getFactory().newSubfield('z', itemEntity.getCustomerCode()));
        record.addVariableField(dataField);

        return record;
    }

    /**
     * Covert marc records to marc xml string.
     *
     * @param recordList the record list
     * @return the string
     * @throws Exception the exception
     */
    public String covertToMarcXmlString(List<Record> recordList) throws Exception {
        OutputStream out = new ByteArrayOutputStream();
        MarcWriter writer = new MarcXmlWriter(out, "UTF-8", true);

        recordList.forEach(writer::write);
        writer.close();

        return out.toString();
    }

    /**
     * Gets marc factory.
     *
     * @return the factory
     */
    public MarcFactory getFactory() {
        if (null == factory) {
            factory = MarcFactory.newInstance();
        }
        return factory;
    }
}