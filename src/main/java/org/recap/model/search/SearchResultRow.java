package org.recap.model.search;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 11/7/16.
 */
@Getter
@Setter
@ApiModel(value="SearchResultRow", description="Model for Displaying Search Result")
public class SearchResultRow extends AbstractSearchResultRow {
    @ApiModelProperty(name= "searchItemResultRows", value= "Item Results",position = 16)
    private List<SearchItemResultRow> searchItemResultRows = new ArrayList<>();
}
