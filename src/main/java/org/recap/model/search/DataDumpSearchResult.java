package org.recap.model.search;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angelind on 26/10/16.
 */
@Data
@ApiModel(value="DataDumpSearchResult", description="Model for Displaying Search Result")
public class DataDumpSearchResult {
    @ApiModelProperty(name= "bibId", value= "Bibliographic Id",position = 0)
    private Integer bibId;
    @ApiModelProperty(name= "itemIds", value= "Item Ids",position = 1)
    private List<Integer> itemIds = new ArrayList<>();
}
