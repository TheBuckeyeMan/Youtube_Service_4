package com.example.YoutubeService4.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {
    @Bean
    public S3Client s3Client(){
        return S3Client.create();
    }

    @Bean
    public S3AsyncClient s3AsyncClient(){
        return S3AsyncClient.builder()
                            .region(Region.US_EAST_2) // Replace with your region
                            .credentialsProvider(DefaultCredentialsProvider.create())
                            .build();
    }
}
