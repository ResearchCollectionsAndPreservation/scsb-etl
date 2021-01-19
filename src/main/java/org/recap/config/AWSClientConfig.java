package org.recap.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSClientConfig {

    @Value("${awsAccessKey}")
    private String awsAccessKey;

    @Value("${awsAccessSecretKey}")
    private String awsAccessSecretKey;

    @Bean
    public AmazonS3 getAwsClient() {

        AWSCredentials credentials = new BasicAWSCredentials(
                awsAccessKey,
                awsAccessSecretKey
        );

        AmazonS3 awsS3Client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_2)
                .build();
        return awsS3Client;
    }

}
