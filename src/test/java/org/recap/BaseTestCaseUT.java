package org.recap;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@TestPropertySource("classpath:application.properties")
@ExtendWith({SpringExtension.class})
public class BaseTestCaseUT {
    @BeforeEach
    public  void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void contextLoads() {
    }
}