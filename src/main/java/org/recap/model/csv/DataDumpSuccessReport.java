package org.recap.model.csv;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.OneToMany;

import java.io.Serializable;
import java.util.List;

/**
 * Created by premkb on 30/9/16.
 */
@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX")
public class DataDumpSuccessReport implements Serializable{

    @DataField(pos = 1, columnName = "Institution Code")
    private String institutionCodes;
    @DataField(pos = 2, columnName = "Requesting Institution Code")
    private String requestingInstitution;
    @DataField(pos = 3, columnName = "Fetch Type")
    private String fetchType;
    @DataField(pos = 4, columnName = "Export From Date")
    private String exportFromDate;
    @DataField(pos = 5, columnName = "Collection Group Id(s)")
    private String collectionGroupIds;
    @DataField(pos = 6, columnName = "Transmission Type")
    private String transmissionType;
    @DataField(pos = 7, columnName = "Export Format")
    private String exportFormat;
    @DataField(pos = 8, columnName = "To Email Id(s)")
    private String toEmailId;
    @DataField(pos = 9, columnName = "No of Bibs Exported")
    private String noOfBibsExported;
    @DataField(pos = 10, columnName = "No of Items Exported")
    private String exportedItemCount;

    private String fileName;

    private String reportType;

    private String institutionName;

    /**
     * The Data dump success report list.
     */
    @OneToMany(mappedTo = "org.recap.model.csv.DataDumpSuccessReport")
    List<DataDumpSuccessReport> dataDumpSuccessReportList;

    /**
     * Gets institution codes.
     *
     * @return the institution codes
     */
    public String getInstitutionCodes() {
        return institutionCodes;
    }

    /**
     * Sets institution codes.
     *
     * @param institutionCodes the institution codes
     */
    public void setInstitutionCodes(String institutionCodes) {
        this.institutionCodes = institutionCodes;
    }

    /**
     * Gets requesting institution.
     *
     * @return the requesting institution
     */
    public String getRequestingInstitution() {
        return requestingInstitution;
    }

    /**
     * Sets requesting institution.
     *
     * @param requestingInstitution the requesting institution
     */
    public void setRequestingInstitution(String requestingInstitution) {
        this.requestingInstitution = requestingInstitution;
    }

    /**
     * Gets fetch type.
     *
     * @return the fetch type
     */
    public String getFetchType() {
        return fetchType;
    }

    /**
     * Sets fetch type.
     *
     * @param fetchType the fetch type
     */
    public void setFetchType(String fetchType) {
        this.fetchType = fetchType;
    }

    /**
     * Gets export from date.
     *
     * @return the export from date
     */
    public String getExportFromDate() {
        return exportFromDate;
    }

    /**
     * Sets export from date.
     *
     * @param exportFromDate the export from date
     */
    public void setExportFromDate(String exportFromDate) {
        this.exportFromDate = exportFromDate;
    }

    /**
     * Gets collection group ids.
     *
     * @return the collection group ids
     */
    public String getCollectionGroupIds() {
        return collectionGroupIds;
    }

    /**
     * Sets collection group ids.
     *
     * @param collectionGroupIds the collection group ids
     */
    public void setCollectionGroupIds(String collectionGroupIds) {
        this.collectionGroupIds = collectionGroupIds;
    }

    /**
     * Gets transmission type.
     *
     * @return the transmission type
     */
    public String getTransmissionType() {
        return transmissionType;
    }

    /**
     * Sets transmission type.
     *
     * @param transmissionType the transmission type
     */
    public void setTransmissionType(String transmissionType) {
        this.transmissionType = transmissionType;
    }

    /**
     * Gets no of bibs exported.
     *
     * @return the no of bibs exported
     */
    public String getNoOfBibsExported() {
        return noOfBibsExported;
    }

    /**
     * Sets no of bibs exported.
     *
     * @param noOfBibsExported the no of bibs exported
     */
    public void setNoOfBibsExported(String noOfBibsExported) {
        this.noOfBibsExported = noOfBibsExported;
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

    /**
     * Gets export format.
     *
     * @return the export format
     */
    public String getExportFormat() {
        return exportFormat;
    }

    /**
     * Sets export format.
     *
     * @param exportFormat the export format
     */
    public void setExportFormat(String exportFormat) {
        this.exportFormat = exportFormat;
    }

    /**
     * Gets to email id.
     *
     * @return the to email id
     */
    public String getToEmailId() {
        return toEmailId;
    }

    /**
     * Sets to email id.
     *
     * @param toEmailId the to email id
     */
    public void setToEmailId(String toEmailId) {
        this.toEmailId = toEmailId;
    }

    /**
     * Gets data dump success report list.
     *
     * @return the data dump success report list
     */
    public List<DataDumpSuccessReport> getDataDumpSuccessReportList() {
        return dataDumpSuccessReportList;
    }

    /**
     * Sets data dump success report list.
     *
     * @param dataDumpSuccessReportList the data dump success report list
     */
    public void setDataDumpSuccessReportList(List<DataDumpSuccessReport> dataDumpSuccessReportList) {
        this.dataDumpSuccessReportList = dataDumpSuccessReportList;
    }

    public String getExportedItemCount() {
        return exportedItemCount;
    }

    public void setExportedItemCount(String exportedItemCount) {
        this.exportedItemCount = exportedItemCount;
    }
}
