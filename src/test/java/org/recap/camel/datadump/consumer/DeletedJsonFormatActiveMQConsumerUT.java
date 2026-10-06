package org.recap.camel.datadump.consumer;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.recap.BaseTestCaseUT;
import org.recap.model.export.Bib;
import org.recap.model.export.DeletedRecord;
import org.recap.model.export.Item;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.service.formatter.datadump.DeletedJsonFormatterService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;

public class DeletedJsonFormatActiveMQConsumerUT extends BaseTestCaseUT {

    @InjectMocks
    @Spy
    DeletedJsonFormatActiveMQConsumer deletedJsonFormatActiveMQConsumer;

    @Mock
    DeletedJsonFormatterService deletedJsonFormatterService;

    @Test
    public void testprocessDeleteJsonString() throws Exception {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        ItemEntity itemEntity = getItemEntity();
        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(itemEntity);
        bibliographicEntity.setItemEntities(itemEntities);
        DeletedRecord deletedRecord = new DeletedRecord();
        List<String> itemBarcodes = new ArrayList<>();
        Bib bib = new Bib();
        bib.setBibId("1");
        bib.setOwningInstitutionBibId("1");
        bib.setOwningInstitutionCode("CUL");
        if (bibliographicEntity.isDeleted()) {
            bib.setDeleteAllItems(true);
        } else {
            List<Item> items = new ArrayList<>();
            for (ItemEntity itemEntityNew : bibliographicEntity.getItemEntities()) {
                itemBarcodes.add(itemEntityNew.getBarcode());
                Item item = new Item();
                item.setItemId(itemEntityNew.getId().toString());
                item.setOwningInstitutionItemId(itemEntityNew.getOwningInstitutionItemId());
                item.setBarcode(itemEntityNew.getBarcode());
                items.add(item);
            }
            bib.setItems(items);
        }
        deletedRecord.setBib(bib);
        List<DeletedRecord> deletedRecordList = new ArrayList<>();
        deletedRecordList.add(deletedRecord);
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        in.setBody(deletedRecordList);
        ex.setIn(in);
        Map<String, Object> mapData = new HashMap<>();
        mapData.put("batchHeaders", ";requestId#1");
        in.setHeaders(mapData);
        Mockito.when(deletedJsonFormatterService.getJsonForDeletedRecords(deletedRecordList)).thenReturn("test");
        Mockito.doNothing().when(deletedJsonFormatActiveMQConsumer).processSuccessReport(any(), any(), any(), any(), any());
        try {
            deletedJsonFormatActiveMQConsumer.processDeleteJsonString(ex);
        } catch (Exception e) {
        }
        Mockito.doThrow(new NullPointerException()).when(deletedJsonFormatterService).getJsonForDeletedRecords(any());
        try {
            deletedJsonFormatActiveMQConsumer.processDeleteJsonString(ex);
        } catch (Exception e) {
        }
    }

    private ItemEntity getItemEntity() {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setBarcode("1234");
        itemEntity.setId(1);
        itemEntity.setCustomerCode("1234");
        itemEntity.setCallNumber("1234");
        itemEntity.setCallNumberType("land");
        itemEntity.setItemAvailabilityStatusId(123);
        return itemEntity;
    }
}
