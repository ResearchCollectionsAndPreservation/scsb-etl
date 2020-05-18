package org.recap.model.jpa;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by peris on 7/17/16.
 */
@Entity
@Table(name = "xml_records_t", schema = "recap", catalog = "")
public class XmlRecordEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "id")
    private Integer id;

    @Lob
    @Column(name = "xml_record")
    private byte[] xml;

    @Column(name = "xml_file")
    private String xmlFileName;

    @Column(name="owning_inst")
    private String owningInst;

    @Column(name="owning_inst_bib_id")
    private String owningInstBibId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_loaded")
    private Date dataLoaded;

    /**
     * Gets data loaded.
     *
     * @return the data loaded
     */
    public Date getDataLoaded() {
        return dataLoaded;
    }

    /**
     * Sets data loaded.
     *
     * @param dataLoaded the data loaded
     */
    public void setDataLoaded(Date dataLoaded) {
        this.dataLoaded = dataLoaded;
    }

    /**
     * Gets owning inst bib id.
     *
     * @return the owning inst bib id
     */
    public String getOwningInstBibId() {
        return owningInstBibId;
    }

    /**
     * Sets owning inst bib id.
     *
     * @param owningInstBibId the owning inst bib id
     */
    public void setOwningInstBibId(String owningInstBibId) {
        this.owningInstBibId = owningInstBibId;
    }

    /**
     * Gets owning inst.
     *
     * @return the owning inst
     */
    public String getOwningInst() {
        return owningInst;
    }

    /**
     * Sets owning inst.
     *
     * @param owningInst the owning inst
     */
    public void setOwningInst(String owningInst) {
        this.owningInst = owningInst;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Get xml byte [ ].
     *
     * @return the byte [ ]
     */
    public byte[] getXml() {
        return xml;
    }

    /**
     * Sets xml.
     *
     * @param xml the xml
     */
    public void setXml(byte[] xml) {
        this.xml = xml;
    }

    /**
     * Gets xml file name.
     *
     * @return the xml file name
     */
    public String getXmlFileName() {
        return xmlFileName;
    }

    /**
     * Sets xml file name.
     *
     * @param xmlFileName the xml file name
     */
    public void setXmlFileName(String xmlFileName) {
        this.xmlFileName = xmlFileName;
    }
}
