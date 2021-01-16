package org.recap.model.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;

/**
 * Created by pvsubrah on 6/10/16.
 */
@Getter
@Setter
@Entity
@Table(name = "bibliographic_t", schema = "recap", catalog = "")
@AttributeOverride(name = "id", column = @Column(name = "BIBLIOGRAPHIC_ID"))
public class BibliographicEntity extends BibliographicAbstractEntity {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "OWNING_INST_ID", insertable=false, updatable=false)
    private InstitutionEntity institutionEntity;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "bibliographic_holdings_t", joinColumns = {
            @JoinColumn(name = "BIBLIOGRAPHIC_ID", referencedColumnName = "BIBLIOGRAPHIC_ID")},
            inverseJoinColumns = {
                    @JoinColumn(name = "HOLDINGS_ID", referencedColumnName = "HOLDINGS_ID")})
    private List<HoldingsEntity> holdingsEntities;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "bibliographic_item_t", joinColumns = {
            @JoinColumn(name="BIBLIOGRAPHIC_ID", referencedColumnName = "BIBLIOGRAPHIC_ID")},
            inverseJoinColumns = {
                    @JoinColumn(name="ITEM_ID", referencedColumnName = "ITEM_ID")})
    private List<ItemEntity> itemEntities;

    /**
     * Instantiates a new Bibliographic entity.
     */
    public BibliographicEntity() {
        super();
    }
}

