package org.recap.camel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chenchulakshmig on 15/9/16.
 */
public class EmailPayLoad implements Serializable{

    private List<String> institutions;
    private String location;
    private Integer count;
    private Integer failedCount;
    private String to;
    private String cc;
    private Integer itemCount;
    private String subject;

    /**
     * Gets institutions.
     *
     * @return the institutions
     */
    public List<String> getInstitutions() {
        return institutions;
    }

    /**
     * Sets institutions.
     *
     * @param institutions the institutions
     */
    public void setInstitutions(List<String> institutions) {
        this.institutions = institutions;
    }

    /**
     * Gets location.
     *
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets location.
     *
     * @param location the location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets count.
     *
     * @return the count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * Sets count.
     *
     * @param count the count
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * Gets failed count.
     *
     * @return the failed count
     */
    public Integer getFailedCount() {
        return failedCount;
    }

    /**
     * Sets failed count.
     *
     * @param failedCount the failed count
     */
    public void setFailedCount(Integer failedCount) {
        this.failedCount = failedCount;
    }

    /**
     * Gets to.
     *
     * @return the to
     */
    public String getTo() {
        return to;
    }

    /**
     * Sets to.
     *
     * @param to the to
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * Gets item count.
     *
     * @return the item count
     */
    public Integer getItemCount() {
        return itemCount;
    }

    /**
     * Sets item count.
     *
     * @param itemCount the item count
     */
    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    /**
     * Gets subject.
     *
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets subject.
     *
     * @param subject the subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Gets cc.
     *
     * @return the cc
     */
    public String getCc() {
        return cc;
    }

    /**
     * Sets cc.
     *
     * @param cc the cc
     */
    public void setCc(String cc) {
        this.cc = cc;
    }
}
