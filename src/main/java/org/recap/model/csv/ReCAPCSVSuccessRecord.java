package org.recap.model.csv;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.OneToMany;

import java.io.Serializable;
import java.util.List;

/**
 * Created by angelind on 18/8/16.
 */
@Data
@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX", skipFirstLine = true)
public class ReCAPCSVSuccessRecord implements Serializable {
    @DataField(pos = 1, columnName = "File Name")
    private String fileName;
    @DataField(pos = 2, columnName = "Total Records In File")
    private Integer totalRecordsInFile;
    @DataField(pos = 3, columnName = "Total Bibs Loaded")
    private Integer totalBibsLoaded;
    @DataField(pos = 4, columnName = "Total Holdings Loaded")
    private Integer totalHoldingsLoaded;
    @DataField(pos = 5, columnName = "Total Bib-Holdings Loaded")
    private Integer totalBibHoldingsLoaded;
    @DataField(pos = 6, columnName = "Total Items Loaded")
    private Integer totalItemsLoaded;
    @DataField(pos = 7, columnName = "Total Bib-Items Loaded")
    private Integer totalBibItemsLoaded;

    private String reportFileName;
    private String reportType;
    private String institutionName;

    /**
     * The Success report re capcsv record list.
     */
    @OneToMany(mappedTo = "org.recap.model.csv.SuccessReportReCAPCSVRecord")
    private List<SuccessReportReCAPCSVRecord> successReportReCAPCSVRecordList;
}
