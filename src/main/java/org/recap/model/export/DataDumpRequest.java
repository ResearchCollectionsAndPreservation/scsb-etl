package org.recap.model.export;

import java.util.List;

/**
 * Created by premkb on 19/8/16.
 */
public class DataDumpRequest {

    private List<String> institutionCodes;
    private String fetchType;
    private String date;
    private String toDate;
    private boolean isRecordsAvailable;
    private List<Integer> collectionGroupIds;
    private String transmissionType;
    private String requestingInstitutionCode;
    private String toEmailAddress;
    private String outputFileFormat;
    private String dateTimeString;
    private String requestId;

    /**
     * Gets institution codes.
     *
     * @return the institution codes
     */
    public List<String> getInstitutionCodes() {
        return institutionCodes;
    }

    /**
     * Sets institution codes.
     *
     * @param institutionCodes the institution codes
     */
    public void setInstitutionCodes(List<String> institutionCodes) {
        this.institutionCodes = institutionCodes;
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
     * Gets date.
     *
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets date.
     *
     * @param date the date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Is records available boolean.
     *
     * @return the boolean
     */
    public boolean isRecordsAvailable() {
        return isRecordsAvailable;
    }

    /**
     * Sets records available.
     *
     * @param recordsAvailable the records available
     */
    public void setRecordsAvailable(boolean recordsAvailable) {
        isRecordsAvailable = recordsAvailable;
    }

    /**
     * Gets collection group ids.
     *
     * @return the collection group ids
     */
    public List<Integer> getCollectionGroupIds() {
        return collectionGroupIds;
    }

    /**
     * Sets collection group ids.
     *
     * @param collectionGroupIds the collection group ids
     */
    public void setCollectionGroupIds(List<Integer> collectionGroupIds) {
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
     * Gets requesting institution code.
     *
     * @return the requesting institution code
     */
    public String getRequestingInstitutionCode() {
        return requestingInstitutionCode;
    }

    /**
     * Sets requesting institution code.
     *
     * @param requestingInstitutionCode the requesting institution code
     */
    public void setRequestingInstitutionCode(String requestingInstitutionCode) {
        this.requestingInstitutionCode = requestingInstitutionCode;
    }

    /**
     * Gets to email address.
     *
     * @return the to email address
     */
    public String getToEmailAddress() {
        return toEmailAddress;
    }

    /**
     * Sets to email address.
     *
     * @param toEmailAddress the to email address
     */
    public void setToEmailAddress(String toEmailAddress) {
        this.toEmailAddress = toEmailAddress;
    }

    /**
     * Gets output file format.
     *
     * @return the output file format
     */
    public String getOutputFileFormat() {
        return outputFileFormat;
    }

    /**
     * Sets output file format.
     *
     * @param outputFileFormat the output file format
     */
    public void setOutputFileFormat(String outputFileFormat) {
        this.outputFileFormat = outputFileFormat;
    }

    /**
     * Gets date time string.
     *
     * @return the date time string
     */
    public String getDateTimeString() {
        return dateTimeString;
    }

    /**
     * Sets date time string.
     *
     * @param dateTimeString the date time string
     */
    public void setDateTimeString(String dateTimeString) {
        this.dateTimeString = dateTimeString;
    }

    /**
     * Gets request id.
     *
     * @return the request id
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets request id.
     *
     * @param requestId the request id
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Gets to date.
     *
     * @return the to date
     */
    public String getToDate() {
        return toDate;
    }

    /**
     * Sets to date.
     *
     * @param toDate the to date
     */
    public void setToDate(String toDate) {
        this.toDate = toDate;
    }
}
