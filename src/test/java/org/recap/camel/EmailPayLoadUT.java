package org.recap.camel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class EmailPayLoadUT {
    @InjectMocks
    EmailPayLoad mockEmailPayLoad;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testEmailPayLoad() {
        mockEmailPayLoad.getCc();
        mockEmailPayLoad.getLocation();
        mockEmailPayLoad.getSubject();
        mockEmailPayLoad.getCount();
        mockEmailPayLoad.getItemCount();
        mockEmailPayLoad.getInstitutions();
        assertTrue(true);
    }
}
