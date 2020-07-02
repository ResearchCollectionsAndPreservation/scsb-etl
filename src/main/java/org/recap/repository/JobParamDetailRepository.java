package org.recap.repository;

import org.recap.model.jpa.JobParamEntity;
import org.recap.repository.jpa.BaseRepository;

/**
 * Created by rajeshbabuk on 7/7/17.
 */
public interface JobParamDetailRepository extends BaseRepository<JobParamEntity> {

    /**
     * Finds job param entity by using job name.
     *
     * @param jobName the job name
     * @return the job param entity
     */
    JobParamEntity findByJobName(String jobName);

}