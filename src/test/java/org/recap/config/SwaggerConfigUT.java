package org.recap.config;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;
import springfox.documentation.spring.web.plugins.Docket;

import static org.junit.Assert.assertNotNull;

public class SwaggerConfigUT extends BaseTestCaseUT {

    @InjectMocks
    SwaggerConfig swaggerConfig;

    @Test
    public void documentation() {
        Docket docket = swaggerConfig.documentation();
        assertNotNull(docket);
    }
}
