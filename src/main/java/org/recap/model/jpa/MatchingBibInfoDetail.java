package org.recap.model.jpa;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by premkb on 28/1/17.
 */
@Entity
@Table(name="MATCHING_BIB_INFO_DETAIL_T",schema="recap",catalog="")
public class MatchingBibInfoDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "MATCHING_BIB_INFO_DATA_DUMP_ID")
    private Integer matchingBibInfoDetailId;

    @Column(name = "BIB_ID")
    private String bibId;

    @Column(name = "OWNING_INST_BIB_ID")
    private String owningInstitutionBibId;

    @Column(name = "OWNING_INST")
    private String owningInstitution;

    @Column(name = "LATEST_RECORD_NUM")
    private Integer recordNum;


    /**
     * Gets matching bib info detail id.
     *
     * @return the matching bib info detail id
     */
    public Integer getMatchingBibInfoDetailId() {
        return matchingBibInfoDetailId;
    }

    /**
     * Sets matching bib info detail id.
     *
     * @param matchingBibInfoDetailId the matching bib info detail id
     */
    public void setMatchingBibInfoDetailId(Integer matchingBibInfoDetailId) {
        this.matchingBibInfoDetailId = matchingBibInfoDetailId;
    }

    /**
     * Gets bib id.
     *
     * @return the bib id
     */
    public String getBibId() {
        return bibId;
    }

    /**
     * Sets bib id.
     *
     * @param bibId the bib id
     */
    public void setBibId(String bibId) {
        this.bibId = bibId;
    }

    /**
     * Gets owning institution bib id.
     *
     * @return the owning institution bib id
     */
    public String getOwningInstitutionBibId() {
        return owningInstitutionBibId;
    }

    /**
     * Sets owning institution bib id.
     *
     * @param owningInstitutionBibId the owning institution bib id
     */
    public void setOwningInstitutionBibId(String owningInstitutionBibId) {
        this.owningInstitutionBibId = owningInstitutionBibId;
    }

    /**
     * Gets owning institution.
     *
     * @return the owning institution
     */
    public String getOwningInstitution() {
        return owningInstitution;
    }

    /**
     * Sets owning institution.
     *
     * @param owningInstitution the owning institution
     */
    public void setOwningInstitution(String owningInstitution) {
        this.owningInstitution = owningInstitution;
    }

    /**
     * Gets record num.
     *
     * @return the record num
     */
    public Integer getRecordNum() {
        return recordNum;
    }

    /**
     * Sets record num.
     *
     * @param recordNum the record num
     */
    public void setRecordNum(Integer recordNum) {
        this.recordNum = recordNum;
    }
}
