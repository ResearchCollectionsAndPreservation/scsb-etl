package org.recap.model.csv;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.OneToMany;

import java.io.Serializable;
import java.util.List;

/**
 * Created by angelind on 18/8/16.
 */
@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX", skipFirstLine = true)
public class ReCAPCSVSuccessRecord implements Serializable{

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
    List<SuccessReportReCAPCSVRecord> successReportReCAPCSVRecordList;

    /**
     * Gets report file name.
     *
     * @return the report file name
     */
    public String getReportFileName() {
        return reportFileName;
    }

    /**
     * Sets report file name.
     *
     * @param reportFileName the report file name
     */
    public void setReportFileName(String reportFileName) {
        this.reportFileName = reportFileName;
    }

    /**
     * Gets report type.
     *
     * @return the report type
     */
    public String getReportType() {
        return reportType;
    }

    /**
     * Sets report type.
     *
     * @param reportType the report type
     */
    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    /**
     * Gets institution name.
     *
     * @return the institution name
     */
    public String getInstitutionName() {
        return institutionName;
    }

    /**
     * Sets institution name.
     *
     * @param institutionName the institution name
     */
    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    /**
     * Gets success report re capcsv record list.
     *
     * @return the success report re capcsv record list
     */
    public List<SuccessReportReCAPCSVRecord> getSuccessReportReCAPCSVRecordList() {
        return successReportReCAPCSVRecordList;
    }

    /**
     * Sets success report re capcsv record list.
     *
     * @param successReportReCAPCSVRecordList the success report re capcsv record list
     */
    public void setSuccessReportReCAPCSVRecordList(List<SuccessReportReCAPCSVRecord> successReportReCAPCSVRecordList) {
        this.successReportReCAPCSVRecordList = successReportReCAPCSVRecordList;
    }
}
