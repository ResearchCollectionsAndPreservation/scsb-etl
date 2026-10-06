package org.recap.util.datadump;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.JobParamDataEntity;
import org.recap.model.jpa.JobParamEntity;
import org.recap.repository.JobParamDetailRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

public class JobDataParameterUtilUT extends BaseTestCaseUT {
    @InjectMocks
    JobDataParameterUtil jobDataParameterUtil;

    @Mock
    JobParamDetailRepository jobParamDetailRepository;

    @Test
    public void testbuildJobRequestParameterMap() {
        Map<String, String> map = new HashMap<>();
        JobParamEntity jobParamEntity = getJobParamEntity();
        Mockito.when(jobParamDetailRepository.findByJobName(any())).thenReturn(jobParamEntity);
        map = jobDataParameterUtil.buildJobRequestParameterMap("Test");
        assertNotNull(map);
    }

    private JobParamEntity getJobParamEntity() {
        JobParamEntity jobParamEntity = new JobParamEntity();
        JobParamDataEntity jobParamDataEntity = new JobParamDataEntity();
        jobParamDataEntity.setId(1);
        jobParamDataEntity.setParamName("test");
        jobParamDataEntity.setParamValue("234");
        jobParamDataEntity.setRecordNum("356");
        jobParamEntity.setJobParamDataEntities(Arrays.asList(jobParamDataEntity));
        return jobParamEntity;
    }
}
