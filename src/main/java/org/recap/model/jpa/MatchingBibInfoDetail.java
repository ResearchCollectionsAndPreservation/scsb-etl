package org.recap.model.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by premkb on 28/1/17.
 */
@Entity
@Table(name="MATCHING_BIB_INFO_DETAIL_T",schema="recap",catalog="")
@AttributeOverride(name = "id", column = @Column(name = "MATCHING_BIB_INFO_DATA_DUMP_ID"))
@Getter
@Setter
public class MatchingBibInfoDetail extends IdentifiableBase<Integer> {

    @Column(name = "BIB_ID")
    private String bibId;

    @Column(name = "OWNING_INST_BIB_ID")
    private String owningInstitutionBibId;

    @Column(name = "OWNING_INST")
    private String owningInstitution;

    @Column(name = "LATEST_RECORD_NUM")
    private Integer recordNum;
}
