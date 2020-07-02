package org.recap.model.search;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 18/4/17.
 */
public class SearchRecordsRequestUT extends BaseTestCase{

    @Test
    public void testSearchRecordsRequest(){
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        SearchResultRow searchResultRow = getSearchResultRow();
        SearchItemResultRow searchItemResultRow = getSearchItemResultRow();
        searchRecordsRequest.setFieldValue("test");
        searchRecordsRequest.setFieldName("test");
        searchRecordsRequest.setOwningInstitutions(Arrays.asList("PUL"));
        searchRecordsRequest.setCollectionGroupDesignations(Arrays.asList("Open"));
        searchRecordsRequest.setAvailability(Arrays.asList("Available"));
        searchRecordsRequest.setMaterialTypes(Arrays.asList("Monograph"));
        searchRecordsRequest.setUseRestrictions(Arrays.asList("Others"));
        searchRecordsRequest.setSearchResultRows(Arrays.asList(searchResultRow));
        searchRecordsRequest.setTotalPageCount(1);
        searchRecordsRequest.setTotalBibRecordsCount("1");
        searchRecordsRequest.setTotalItemRecordsCount("1");
        searchRecordsRequest.setTotalRecordsCount("1");
        searchRecordsRequest.setPageNumber(10);
        searchRecordsRequest.setPageSize(1);
        searchRecordsRequest.setShowResults(true);
        searchRecordsRequest.setSelectAll(true);
        searchRecordsRequest.setSelectAllFacets(true);
        searchRecordsRequest.setShowTotalCount(true);
        searchRecordsRequest.setIndex(1);
        searchRecordsRequest.setDeleted(false);
        searchRecordsRequest.setErrorMessage("test");
        assertNotNull(searchRecordsRequest.getFieldValue());
        assertNotNull(searchRecordsRequest.getFieldName());
        assertNotNull(searchRecordsRequest.getOwningInstitutions());
        assertNotNull(searchRecordsRequest.getCollectionGroupDesignations());
        assertNotNull(searchRecordsRequest.getAvailability());
        assertNotNull(searchRecordsRequest.getMaterialTypes());
        assertNotNull(searchRecordsRequest.getUseRestrictions());
        assertNotNull(searchRecordsRequest.getSearchResultRows());
        assertNotNull(searchRecordsRequest.getTotalPageCount());
        assertNotNull(searchRecordsRequest.getPageNumber());
        assertNotNull(searchRecordsRequest.getPageSize());
        assertNotNull(searchRecordsRequest.getTotalBibRecordsCount());
        assertNotNull(searchRecordsRequest.getTotalItemRecordsCount());
        assertNotNull(searchRecordsRequest.getTotalRecordsCount());
        assertNotNull(searchRecordsRequest.isShowResults());
        assertNotNull(searchRecordsRequest.isSelectAll());
        assertNotNull(searchRecordsRequest.isSelectAllFacets());
        assertNotNull(searchRecordsRequest.isShowTotalCount());
        assertNotNull(searchRecordsRequest.getIndex());
        assertNotNull(searchRecordsRequest.getErrorMessage());
        assertNotNull(searchRecordsRequest.isDeleted());
        assertNotNull(searchItemResultRow.getCallNumber());
        assertNotNull(searchItemResultRow.getChronologyAndEnum());
        assertNotNull(searchItemResultRow.getCustomerCode());
        assertNotNull(searchItemResultRow.getBarcode());
        assertNotNull(searchItemResultRow.getUseRestriction());
        assertNotNull(searchItemResultRow.getAvailability());
        assertNotNull(searchItemResultRow.isSelectedItem());
        assertNotNull(searchItemResultRow.getCollectionGroupDesignation());
        assertNotNull(searchResultRow.getBibId());
        assertNotNull(searchResultRow.getTitle());
        assertNotNull(searchResultRow.getAuthor());
        assertNotNull(searchResultRow.getPublisher());
        assertNotNull(searchResultRow.getPublisherDate());
        assertNotNull(searchResultRow.getOwningInstitution());
        assertNotNull(searchResultRow.getCustomerCode());
        assertNotNull(searchResultRow.getCollectionGroupDesignation());
        assertNotNull(searchResultRow.getUseRestriction());
        assertNotNull(searchResultRow.getBarcode());
        assertNotNull(searchResultRow.getSummaryHoldings());
        assertNotNull(searchResultRow.getAvailability());
        assertNotNull(searchResultRow.getLeaderMaterialType());
        assertNotNull(searchResultRow.isSelected());
        assertNotNull(searchResultRow.isShowItems());
        assertNotNull(searchResultRow.getSearchItemResultRows());
        assertNotNull(searchResultRow.getSearchItemResultRows());

    }


    public SearchResultRow getSearchResultRow(){
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setBibId(1);
        searchResultRow.setTitle("test");
        searchResultRow.setAuthor("John");
        searchResultRow.setPublisher("test");
        searchResultRow.setPublisherDate(new Date().toString());
        searchResultRow.setOwningInstitution("PUL");
        searchResultRow.setCustomerCode("PB");
        searchResultRow.setCollectionGroupDesignation("Open");
        searchResultRow.setUseRestriction("Others");
        searchResultRow.setBarcode("33566566654564");
        searchResultRow.setSummaryHoldings("test");
        searchResultRow.setAvailability("Available");
        searchResultRow.setLeaderMaterialType("Monograph");
        searchResultRow.setSelected(false);
        searchResultRow.setShowItems(true);
        searchResultRow.setSelectAllItems(true);
        SearchItemResultRow searchItemResultRow = getSearchItemResultRow();
        searchResultRow.setSearchItemResultRows(Arrays.asList(searchItemResultRow));
        return searchResultRow;
    }


    public SearchItemResultRow getSearchItemResultRow(){
        SearchItemResultRow searchItemResultRow = new SearchItemResultRow();
        searchItemResultRow.setCallNumber("X");
        searchItemResultRow.setChronologyAndEnum("test");
        searchItemResultRow.setCustomerCode("PUL");
        searchItemResultRow.setBarcode("3356987156568235");
        searchItemResultRow.setUseRestriction("Supervised use");
        searchItemResultRow.setCollectionGroupDesignation("Open");
        searchItemResultRow.setAvailability("Available");
        searchItemResultRow.setSelectedItem(true);
        return searchItemResultRow;
    }

}