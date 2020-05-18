package org.recap.model.jpa;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 7/7/17.
 */
@Entity
@Table(name = "JOB_PARAM_T", schema = "RECAP", catalog = "")
public class JobParamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "RECORD_NUM")
    private Integer recordNumber;

    @Column(name = "JOB_NAME")
    private String jobName;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "RECORD_NUM")
    private List<JobParamDataEntity> jobParamDataEntities = new ArrayList<>();

    /**
     * Gets record number.
     *
     * @return the record number
     */
    public Integer getRecordNumber() {
        return recordNumber;
    }

    /**
     * Sets record number.
     *
     * @param recordNumber the record number
     */
    public void setRecordNumber(Integer recordNumber) {
        this.recordNumber = recordNumber;
    }

    /**
     * Gets job name.
     *
     * @return the job name
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * Sets job name.
     *
     * @param jobName the job name
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * Gets job param data entities.
     *
     * @return the job param data entities
     */
    public List<JobParamDataEntity> getJobParamDataEntities() {
        return jobParamDataEntities;
    }

    /**
     * Sets job param data entities.
     *
     * @param jobParamDataEntities the job param data entities
     */
    public void setJobParamDataEntities(List<JobParamDataEntity> jobParamDataEntities) {
        this.jobParamDataEntities = jobParamDataEntities;
    }

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
