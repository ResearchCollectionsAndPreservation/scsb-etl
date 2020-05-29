package org.recap.model.csv;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.OneToMany;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by peris on 7/21/16.
 */
@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX", skipFirstLine = true)
public class ReCAPCSVFailureRecord implements Serializable{
    @DataField(pos = 1, columnName = "Owning Institution")
    private String owningInstitution;
    @DataField(pos = 2, columnName = "Owning Institution Bib Id")
    private String owningInstitutionBibId;
    @DataField(pos = 3, columnName = "Owning Institution Holdings Id")
    private String owningInstitutionHoldingsId;
    @DataField(pos = 4, columnName = "Local Item Id")
    private String localItemId;
    @DataField(pos = 5, columnName = "Item Barcode")
    private String itemBarcode;
    @DataField(pos = 6, columnName = "Customer Code")
    private String customerCode;
    @DataField(pos = 7, columnName = "Title")
    private String title;
    @DataField(pos = 8, columnName = "Collection Group Designation")
    private String collectionGroupDesignation;
    @DataField(pos = 9, pattern="MM/dd/yyyy hh:mm:ss a", columnName = "Create Date Item")
    private Date createDateItem;
    @DataField(pos = 10, pattern="MM/dd/yyyy hh:mm:ss a", columnName = "Last Updated Date Item")
    private Date lastUpdatedDateItem;
    @DataField(pos = 11, columnName = "Exception Message")
    private String exceptionMessage;
    @DataField(pos = 12, columnName = "Error Description")
    private String errorDescription;

    private String fileName;

    private String reportType;

    private String institutionName;

    /**
     * The Failure report re capcsv record list.
     */
    @OneToMany(mappedTo = "org.recap.model.csv.FailureReportReCAPCSVRecord")
    List<FailureReportReCAPCSVRecord> failureReportReCAPCSVRecordList;

    /**
     * Gets failure report re capcsv record list.
     *
     * @return the failure report re capcsv record list
     */
    public List<FailureReportReCAPCSVRecord> getFailureReportReCAPCSVRecordList() {
        return failureReportReCAPCSVRecordList;
    }

    /**
     * Sets failure report re capcsv record list.
     *
     * @param failureReportReCAPCSVRecordList the failure report re capcsv record list
     */
    public void setFailureReportReCAPCSVRecordList(List<FailureReportReCAPCSVRecord> failureReportReCAPCSVRecordList) {
        this.failureReportReCAPCSVRecordList = failureReportReCAPCSVRecordList;
    }

    /**
     * Gets file name.
     *
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets file name.
     *
     * @param fileName the file name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
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
}
