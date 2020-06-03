package org.recap.model.csv;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.io.Serializable;

/**
 * Created by angelind on 22/7/16.
 */
@Data
@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX")
public class SuccessReportReCAPCSVRecord implements Serializable {
    @DataField(pos = 1)
    private String fileName;
    @DataField(pos = 2)
    private String totalRecordsInFile;
    @DataField(pos = 3)
    private String totalBibsLoaded;
    @DataField(pos = 4)
    private String totalHoldingsLoaded;
    @DataField(pos = 5)
    private String totalBibHoldingsLoaded;
    @DataField(pos = 6)
    private String totalItemsLoaded;
    @DataField(pos = 7)
    private String totalBibItemsLoaded;
}
