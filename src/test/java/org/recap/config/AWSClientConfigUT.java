package org.recap.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;
import org.springframework.test.util.ReflectionTestUtils;

public class AWSClientConfigUT extends BaseTestCaseUT {

    private static final String testKey = "jsknljcsl";
    private static final String testSecretKey = "soiadkl:uc";

    @InjectMocks
    AWSClientConfig awsClientConfig;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(awsClientConfig, "awsAccessKey", testKey);
        ReflectionTestUtils.setField(awsClientConfig, "awsAccessSecretKey", testSecretKey);
    }

    @Test
    public void getAwsClient() {
        awsClientConfig.getAwsClient();
    }
}
