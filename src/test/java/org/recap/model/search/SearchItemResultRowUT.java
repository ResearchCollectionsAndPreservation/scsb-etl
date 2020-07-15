package org.recap.model.search;

import org.junit.*;
import org.recap.*;

import static org.junit.Assert.*;

public class SearchItemResultRowUT extends BaseTestCase {

    SearchItemResultRow searchItemResultRow;

    @Test
    public void testSearchItemResultRow() {
        searchItemResultRow = new SearchItemResultRow();
        searchItemResultRow.setChronologyAndEnum("test");
        String data  = "Test data";
        searchItemResultRow.equals(data);
        searchItemResultRow.hashCode();
        searchItemResultRow.compareTo(searchItemResultRow);
        assertTrue(true);
    }
}
