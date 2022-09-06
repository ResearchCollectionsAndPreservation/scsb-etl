package org.recap.config;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.recap.BaseTestCaseUT;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceInitializationMode;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

public class PrimaryDataSourceConfigUT extends BaseTestCaseUT {

    @InjectMocks
    @Spy
    PrimaryDataSourceConfig primaryDataSourceConfig;

    @Mock
    private Environment env;

    @Test
    public void primaryDataSourceProperties() {
        DataSourceProperties dataSourceProperties = primaryDataSourceConfig.primaryDataSourceProperties();
        assertNotNull(dataSourceProperties);
    }

    @Test
    public void test() {
        DataSourceProperties dataSourceProperties = getDataSourceProperties();
        // PrimaryDataSourceConfig spy = Mockito.spy(dataSourceConfig);
        DataSource dataSource = new HikariDataSource();
        //Mockito.when(primaryDataSourceConfig.primaryDataSourceProperties().initializeDataSourceBuilder().type(any()).build()).thenReturn(dataSource);
        // primaryDataSourceConfig.primaryDataSource();
    }

    private DataSourceProperties getDataSourceProperties() {
        DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setDriverClassName("test");
//        dataSourceProperties.setInitializationMode(DataSourceInitializationMode.EMBEDDED);
        return dataSourceProperties;
    }

    @Test
    public void primaryEntityManagerFactory() {
        //primaryDataSourceConfig.primaryEntityManagerFactory(builder);
    }

    @Test
    public void getJPAProperties() {
        Mockito.when(env.getProperty(any())).thenReturn("test");
        Map<String, Object> jpaProperties = primaryDataSourceConfig.getJPAProperties();
        assertNotNull(jpaProperties);
    }

}
