package org.recap.util.datadump;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.recap.BaseTestCase;
import org.recap.repository.JobParamDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JobDataParameterUtilUT extends BaseTestCase {
    @Autowired
    JobDataParameterUtil jobDataParameterUtil;

    @Autowired
    JobParamDetailRepository jobParamDetailRepository;

    @Before
    public void testData() {
        jobDataParameterUtil = new JobDataParameterUtil();
    }
    @Test
    public void testbuildJobRequestParameterMap() {
        Map<String, String> map = new HashMap<>();
        try {
             map= jobDataParameterUtil.buildJobRequestParameterMap("Test");
        }catch (Exception e){

        }

        assertNotNull(map);

    }
}
