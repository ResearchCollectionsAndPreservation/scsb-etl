package org.recap.model.jpa;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "ims_location_t", schema = "recap", catalog = "")
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@AttributeOverride(
        name = "id",
        column = @Column(
                name = "IMS_LOCATION_ID"
        )
)
public class ImsLocationEntity extends AbstractEntity<Integer>{

    @Column(name = "IMS_LOCATION_CODE")
    private String imsLocationCode;

    @Column(name = "IMS_LOCATION_NAME")
    private String imsLocationName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ACTIVE")
    private boolean active;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_DATE")
    private Date updatedDate;
}
