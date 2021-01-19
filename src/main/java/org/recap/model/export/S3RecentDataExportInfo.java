package org.recap.model.export;

import java.util.Date;

public class S3RecentDataExportInfo {

    private String institution;
    private String bibDataFormat;
    private String keyName;
    private String gcd;
    private String bibCount;
    private String itemCount;
    private long keySize;
    private Date keyLastModified;

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getBibDataFormat() {
        return bibDataFormat;
    }

    public void setBibDataFormat(String bibDataFormat) {
        this.bibDataFormat = bibDataFormat;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getGcd() {
        return gcd;
    }

    public void setGcd(String gcd) {
        this.gcd = gcd;
    }

    public String getBibCount() {
        return bibCount;
    }

    public void setBibCount(String bibCount) {
        this.bibCount = bibCount;
    }

    public String getItemCount() {
        return itemCount;
    }

    public void setItemCount(String itemCount) {
        this.itemCount = itemCount;
    }

    public long getKeySize() {
        return keySize;
    }

    public void setKeySize(long keySize) {
        this.keySize = keySize;
    }

    public Date getKeyLastModified() {
        return keyLastModified;
    }

    public void setKeyLastModified(Date keyLastModified) {
        this.keyLastModified = keyLastModified;
    }
}
