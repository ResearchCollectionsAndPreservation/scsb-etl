package org.recap.model.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by peris on 7/17/16.
 */
@Entity
@Table(name = "xml_records_t", schema = "recap", catalog = "")
@Getter
@Setter
public class XmlRecordEntity extends IdentifiableBase<Integer> {
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
}
