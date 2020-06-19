package org.recap.model.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 7/7/17.
 */
@Entity
@Table(name = "JOB_PARAM_T", schema = "RECAP", catalog = "")
@AttributeOverride(name = "id", column = @Column(name = "RECORD_NUM"))
@Getter
@Setter
public class JobParamEntity extends AbstractEntity<Integer> {

    @Column(name = "JOB_NAME")
    private String jobName;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "RECORD_NUM")
    private List<JobParamDataEntity> jobParamDataEntities = new ArrayList<>();

    /**
     * Add all.
     *
     * @param jobParamDataEntities the job param data entities
     */
    public void addAll(List<JobParamDataEntity> jobParamDataEntities) {
        if (null == getJobParamDataEntities()) {
            this.jobParamDataEntities = new ArrayList<>();
        }
        this.jobParamDataEntities.addAll(jobParamDataEntities);
    }
}
