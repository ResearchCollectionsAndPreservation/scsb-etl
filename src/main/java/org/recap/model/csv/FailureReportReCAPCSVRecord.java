package org.recap.model.csv;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.io.Serializable;

/**
 * Created by peris on 7/21/16.
 */
@Data
@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX")
public class FailureReportReCAPCSVRecord implements Serializable {
    @DataField(pos = 1)
    private String owningInstitution;
    @DataField(pos = 2)
    private String owningInstitutionBibId;
    @DataField(pos = 3)
    private String owningInstitutionHoldingsId;
    @DataField(pos = 4)
    private String localItemId;
    @DataField(pos = 5)
    private String itemBarcode;
    @DataField(pos = 6)
    private String customerCode;
    @DataField(pos = 7)
    private String title;
    @DataField(pos = 8)
    private String collectionGroupDesignation;
    @DataField(pos = 9)
    private String createDateItem;
    @DataField(pos = 10)
    private String lastUpdatedDateItem;
    @DataField(pos = 11)
    private String exceptionMessage;
    @DataField(pos = 12)
    private String errorDescription;
}
