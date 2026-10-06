package org.recap.config;

import brave.sampler.Sampler;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SamplerConfigUT extends BaseTestCaseUT {

    @InjectMocks
    SamplerConfig samplerConfig;

    @Test
    public void defaultSampler() {
        Sampler sampler = samplerConfig.defaultSampler();
        assertNotNull(sampler);
    }

}
