package org.recap.model.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

/**
 * Created by pvsubrah on 6/11/16.
 */
@Getter
@Setter
@Entity
@Table(name = "item_t", schema = "recap", catalog = "")
@IdClass(ItemPK.class)
public class ItemEntity extends ItemAbstractEntity {
    @Column(name = "ITEM_ID", insertable = false,updatable = false)
    private Integer itemId;

    @ManyToMany(mappedBy = "itemEntities")
    private List<HoldingsEntity> holdingsEntities;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ITEM_AVAIL_STATUS_ID", insertable=false, updatable=false)
    private ItemStatusEntity itemStatusEntity;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "COLLECTION_GROUP_ID", insertable=false, updatable=false)
    private CollectionGroupEntity collectionGroupEntity;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "OWNING_INST_ID", insertable=false, updatable=false)
    private InstitutionEntity institutionEntity;

    @ManyToMany(mappedBy = "itemEntities")
    private List<BibliographicEntity> bibliographicEntities;

    @Column(name = "CGD_CHANGE_LOG")
    private String cgdChangeLog;

    @Column(name = "IS_CGD_PROTECTION")
    private boolean isCgdProtection;

    @Temporal(TemporalType.DATE)
    @Column(name = "INITIAL_MATCHING_DATE")
    private Date initialMatchingDate;

    /**
     * Instantiates a new Item entity.
     */
    public ItemEntity() {
        super();
    }
}



