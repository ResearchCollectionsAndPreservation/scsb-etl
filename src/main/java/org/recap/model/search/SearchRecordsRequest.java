package org.recap.model.search;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 6/7/16.
 */
@Getter
@Setter
@ApiModel(value="SearchRecordsRequest", description="Model for showing user details")
public class SearchRecordsRequest implements Serializable {


    @ApiModelProperty(name= "fieldValue", value= "Search Value",  position = 0)
    private String fieldValue = "";

    @ApiModelProperty(name ="fieldName", value= "Select a field name",position = 1)
    private String fieldName;

    @ApiModelProperty(name= "owningInstitutions", value= "Publications Owning Instutions", position = 2, allowableValues="PUL, NYPL, CUL")
    private List<String> owningInstitutions = null;

    @ApiModelProperty(name= "collectionGroupDesignations", value= "Collection Group Designations",position = 3)
    private List<String> collectionGroupDesignations = null;

    @ApiModelProperty(name= "availability", value= "Availability of books in ReCAP",position = 4)
    private List<String> availability = null;

    @ApiModelProperty(name= "materialTypes", value= "Material Types",position = 5)
    private List<String> materialTypes = null;

    @ApiModelProperty(name= "useRestrictions", value= "Book Use Restrictions",position = 6)
    private List<String> useRestrictions = null;

    @ApiModelProperty(name= "searchResultRows", value= "Search Response",position = 7)
    private List<SearchResultRow> searchResultRows = new ArrayList<>();

    @ApiModelProperty(name= "totalPageCount", value= "Total Page Count",position = 8)
    private Integer totalPageCount = 0;

    @ApiModelProperty(name= "totalBibRecordsCount", value= "Total Bibliograph Records Count",position = 9)
    private String totalBibRecordsCount = "0";

    @ApiModelProperty(name= "totalItemRecordsCount", value= "Total Item Count",position = 10)
    private String totalItemRecordsCount = "0";

    @ApiModelProperty(name= "totalRecordsCount", value= "Total Records Count",position = 11)
    private String totalRecordsCount = "0";

    @ApiModelProperty(name= "pageNumber", value= "Current Page Number",position = 12)
    private Integer pageNumber = 0;

    @ApiModelProperty(name= "pageSize", value= "Total records to show is page",position = 13)
    private Integer pageSize = 10;

    @ApiModelProperty(name= "showResults", value= "Show Results",position = 14)
    private boolean showResults = false;

    @ApiModelProperty(name= "selectAll", value= "select All Fields",position = 15)
    private boolean selectAll = false;

    @ApiModelProperty(name= "selectAllFacets", value= "Select All Facets",position = 16)
    private boolean selectAllFacets = false;

    @ApiModelProperty(name= "showTotalCount", value= "Show Total Count",position = 17)
    private boolean showTotalCount = false;

    @ApiModelProperty(name= "index", value= "index",position = 18)
    private Integer index;

    @ApiModelProperty(name= "errorMessage", value= "Error Message",position = 19)
    private String errorMessage;

    @ApiModelProperty(name= "isDeleted", value= "Is Deleted",position = 20)
    private boolean isDeleted = false;

    @ApiModelProperty(name= "requestingInstitution", value= "Requesting Institution",position = 21)
    private String requestingInstitution = "";


    /**
     * Reset.
     */
    public void reset() {
        this.totalBibRecordsCount = String.valueOf(0);
        this.totalItemRecordsCount = String.valueOf(0);
        this.totalRecordsCount = String.valueOf(0);
        this.showTotalCount = false;
        this.errorMessage = null;
    }
}
