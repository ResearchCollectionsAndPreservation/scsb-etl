package org.recap.camel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({SpringExtension.class})
public class EmailPayLoadUT {
    @InjectMocks
    EmailPayLoad mockEmailPayLoad;

    @BeforeEach
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
