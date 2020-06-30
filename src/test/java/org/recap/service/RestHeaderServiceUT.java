package org.recap.service;

import org.junit.Test;
import org.mockito.Mock;
import org.recap.BaseTestCase;
import org.recap.spring.SwaggerAPIProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import static org.junit.Assert.*;

public class RestHeaderServiceUT extends BaseTestCase {

    RestHeaderService restHeaderService = new RestHeaderService() ;

    @Mock
    SwaggerAPIProvider swaggerAPIProvider;

    @Test
    public void testRestHeaderService(){
        HttpHeaders httpHeaders = restHeaderService.getHttpHeaders();
        assertNotNull(httpHeaders);
    }
}
