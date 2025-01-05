package com.example.YoutubeService4.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {
    private static final Logger log = LoggerFactory.getLogger(S3Config.class);
    @Bean
    public S3Client s3Client(){
        log.info("Initializing S3Client");
        return S3Client.create();
    }

    @Bean
    public S3AsyncClient s3AsyncClient() {
        log.info("Initializing S3AsycClient");

        AwsCredentials credentials = DefaultCredentialsProvider.create().resolveCredentials();
        log.info("S3AsyncClient credentials loaded: Access Key ID = {}", credentials.accessKeyId());

        return S3AsyncClient.builder()
                .region(Region.US_EAST_2) // Replace with your region
                .credentialsProvider(DefaultCredentialsProvider.create())
                .httpClientBuilder(AwsCrtAsyncHttpClient.builder()) // Explicitly set CRT HTTP client
                .build();
    }

}
