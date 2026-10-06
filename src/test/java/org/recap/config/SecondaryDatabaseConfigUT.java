package org.recap.config;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.core.env.Environment;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

public class SecondaryDatabaseConfigUT extends BaseTestCaseUT {

    @InjectMocks
    SecondaryDatabaseConfig secondaryDatabaseConfig;

    @Mock
    private Environment env;

    @Test
    public void primaryDataSourceProperties() {
        DataSourceProperties dataSourceProperties = secondaryDatabaseConfig.secondaryDataSourceProperties();
        assertNotNull(dataSourceProperties);
    }

    @Test
    public void getJPAProperties() {
        Mockito.when(env.getProperty(any())).thenReturn("test");
        Map<String, Object> jpaProperties = secondaryDatabaseConfig.getJPAProperties();
        assertNotNull(jpaProperties);
    }
}
