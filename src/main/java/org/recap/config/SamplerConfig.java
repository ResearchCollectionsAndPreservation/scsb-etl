package org.recap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import brave.sampler.Sampler;

/**
 * @author Rathin Maheswaran on 6/24/20
 */
@Configuration
public class SamplerConfig {
	
    @Bean
    public Sampler defaultSampler() {
          return Sampler.ALWAYS_SAMPLE;
    }
}
