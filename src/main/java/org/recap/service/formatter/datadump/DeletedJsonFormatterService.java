package org.recap.service.formatter.datadump;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.export.Bib;
import org.recap.model.export.DeletedRecord;
import org.recap.model.export.Item;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.BibliographicDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by premkb on 29/9/16.
 */
@Service
public class DeletedJsonFormatterService implements DataDumpFormatterInterface {

    private static final Logger logger = LoggerFactory.getLogger(DeletedJsonFormatterService.class);

    @Autowired
    private BibliographicDetailsRepository bibliographicDetailsRepository;
    /**
     * Returns true if selected file format is Json for deleted records data dump.
     *
     * @param formatType the format type
     * @return
     */
    @Override
    public boolean isInterested(String formatType) {
        return formatType.equals(RecapConstants.DATADUMP_DELETED_JSON_FORMAT);
    }

    /**
     * Prepare a map with deleted records and failures.
     *
     * @param bibliographicEntityList the bibliographic entity list
     * @return the map
     */
    public Map<String, Object> prepareDeletedRecords(List<BibliographicEntity> bibliographicEntityList){
        int itemExportedCount = 0;
        Map<String, Object> resultsMap = new HashMap();
        List<DeletedRecord> deletedRecords = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        updateBibDeletedFlagDependingOnPrivateCGDAndDeletedItem(bibliographicEntityList);
        for (BibliographicEntity bibliographicEntity : bibliographicEntityList) {
            try {
                DeletedRecord deletedRecord = new DeletedRecord();
                List<String> itemBarcodes = new ArrayList<>();
                Bib bib = new Bib();
                bib.setBibId(bibliographicEntity.getBibliographicId().toString());
                bib.setOwningInstitutionBibId(bibliographicEntity.getOwningInstitutionBibId());
                bib.setOwningInstitutionCode(bibliographicEntity.getInstitutionEntity().getInstitutionCode());
                if(bibliographicEntity.isDeleted()) {//This if else condition not to print all items are deleted or all the items are changed to private
                    bib.setDeleteAllItems(true);
                } else {
                    List<Item> items = new ArrayList<>();
                    for (ItemEntity itemEntity : bibliographicEntity.getItemEntities()) {
                        itemBarcodes.add(itemEntity.getBarcode());
                        Item item = new Item();
                        item.setItemId(itemEntity.getItemId().toString());
                        item.setOwningInstitutionItemId(itemEntity.getOwningInstitutionItemId());
                        item.setBarcode(itemEntity.getBarcode());
                        items.add(item);
                    }
                    bib.setItems(items);
                }
                deletedRecord.setBib(bib);
                deletedRecords.add(deletedRecord);
                itemExportedCount = itemExportedCount + bibliographicEntity.getItemEntities().size();
            } catch (Exception e) {
                logger.error(RecapConstants.ERROR,e);
                errors.add(bibliographicEntity.getOwningInstitutionBibId()+" * "+String.valueOf(e));
            }
        }

        resultsMap.put(RecapCommonConstants.SUCCESS, deletedRecords);
        resultsMap.put(RecapCommonConstants.FAILURE, errors);
        resultsMap.put(RecapConstants.ITEM_EXPORTED_COUNT, itemExportedCount);

        return resultsMap;
    }

    private void updateBibDeletedFlagDependingOnPrivateCGDAndDeletedItem(List<BibliographicEntity> bibliographicEntityList){
        for(BibliographicEntity bibliographicEntity:bibliographicEntityList){
            if(!bibliographicEntity.isDeleted()){
                BibliographicEntity fetchedBibliographicEntity = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(bibliographicEntity.getOwningInstitutionId(),bibliographicEntity.getOwningInstitutionBibId());
                boolean isDeleted = false;
                for(ItemEntity fetchedItemEntity : fetchedBibliographicEntity.getItemEntities()){
                    if(fetchedItemEntity.isDeleted() || isChangedToPrivateCGD(fetchedItemEntity)){
                        isDeleted = true;
                    } else {
                        isDeleted = false;
                        break;
                    }
                }
                if(isDeleted){
                    bibliographicEntity.setDeleted(true);
                }
            }
        }
    }

    private boolean isChangedToPrivateCGD(ItemEntity fetchedItemEntity){
        return ((fetchedItemEntity.getCgdChangeLog()!=null) &&
            (fetchedItemEntity.getCgdChangeLog().equals(RecapCommonConstants.CGD_CHANGE_LOG_SHARED_TO_PRIVATE) ||
                    fetchedItemEntity.getCgdChangeLog().equals(RecapCommonConstants.CGD_CHANGE_LOG_OPEN_TO_PRIVATE)));
    }

    /**
     * Converts deleted records list to Json string.
     *
     * @param deletedRecordList the deleted record list
     * @return the json for deleted records
     * @throws Exception the exception
     */
    public String getJsonForDeletedRecords(List<DeletedRecord> deletedRecordList) throws Exception{
        String formattedString;
        ObjectMapper mapper = new ObjectMapper();
        formattedString = mapper.writeValueAsString(deletedRecordList);
        return formattedString;
    }
}
