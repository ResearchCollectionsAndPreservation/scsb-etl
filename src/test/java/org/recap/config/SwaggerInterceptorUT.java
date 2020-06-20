package org.recap.config;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertTrue;

/**
 * Created by hemalathas on 19/4/17.
 */
public class SwaggerInterceptorUT extends BaseTestCase{

    @Autowired
    SwaggerInterceptor swaggerInterceptor;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Test
    public void testPreHandle() throws Exception {
        httpServletRequest.setAttribute("api_key","recap");
        boolean continueExport = swaggerInterceptor.preHandle(httpServletRequest,httpServletResponse,new Object());
        assertTrue(!continueExport);
    }

    @Test
    public void testPostHandle() throws Exception {
        swaggerInterceptor.postHandle(httpServletRequest,httpServletResponse,new Object(),new ModelAndView());
    }

    @Test
    public void testAfterCompletion() throws Exception {
        swaggerInterceptor.afterCompletion(httpServletRequest,httpServletResponse,new Object(),new Exception());
    }

}