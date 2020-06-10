package org.recap.model.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by rajeshbabuk on 7/7/17.
 */
@Entity
@Table(name = "JOB_PARAM_DATA_T", schema = "RECAP", catalog = "")
@AttributeOverride(name = "id", column = @Column(name = "JOB_PARAM_DATA_ID"))
@Getter
@Setter
public class JobParamDataEntity extends AbstractEntity<Integer> {

    @Column(name = "PARAM_NAME")
    private String paramName;

    @Column(name = "PARAM_VALUE")
    private String paramValue;

    @Column(name = "RECORD_NUM")
    private String recordNum;
}