package org.recap.model.search;

import io.swagger.annotations.ApiModel;

/**
 * Created by rajesh on 18-Jul-16.
 */
@ApiModel(value="SearchItemResultRow", description="Model for Displaying Item Result")
public class SearchItemResultRow extends AbstractSearchItemResultRow implements Comparable<SearchItemResultRow> {

    @Override
    public int compareTo(SearchItemResultRow searchItemResultRow) {
        return this.getChronologyAndEnum().compareTo(searchItemResultRow.getChronologyAndEnum());
    }
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof SearchItemResultRow))
            return false;
        SearchItemResultRow searchItemResultRow = (SearchItemResultRow) o;
        return getChronologyAndEnum().equals(searchItemResultRow.getChronologyAndEnum());
    }

    @Override
    public int hashCode() {
        return getChronologyAndEnum().hashCode();
    }
}
