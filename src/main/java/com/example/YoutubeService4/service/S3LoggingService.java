package com.example.YoutubeService4.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class S3LoggingService {
    private static final Logger log = LoggerFactory.getLogger(S3LoggingService.class);
    private final S3Client s3Client;

    @Value("${aws.s3.key.logging}")
    private String loggingBucketKey;

    @Value("${aws.s3.bucket.logging}")
    private String loggingBucketName;

    public S3LoggingService() {
        this.s3Client = S3Client.builder()
                                .region(Region.US_EAST_2) // Adjust region as necessary
                                .build();
    }

    // Download Existing S3 File
    public String downloadLogFilesFromS3() {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(loggingBucketName)
                .key(loggingBucketKey)
                .build();

            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
            return new BufferedReader(new InputStreamReader(s3Object))
                .lines()
                .collect(Collectors.joining("\n"));
        } catch (S3Exception e) {
            log.error("Log file not found, creating a new log file.", e);
            return "Log file not found, creating a new log file. Error on like 51 of S3LoggingService"; // Return empty if log file not found
        }
    }

    // Append new log entry to the existing log file
    public String appendLogEntry(String existingContent, String newLogEntry) {
        return existingContent + "\n" + newLogEntry;
    }

    // Upload the updated logging file to the logging S3 bucket
    public void uploadLogFileToS3(String updatedContent) {
        String fileName = "/tmp/log-file.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Write the updated content to the local file
            writer.write(updatedContent);
            writer.flush();

            // Verify file size after writing
            File file = new File(fileName);
            if (file.exists()) {
                log.info("File size after writing: " + file.length() + " bytes");
            } else {
                log.error("File not found: " + fileName);
                return;
            }

            // Upload file to S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(loggingBucketName)
                .key(loggingBucketKey)
                .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromFile(Paths.get(fileName)));
            log.info("Log file successfully uploaded to bucket: " + loggingBucketName + " with key: " + loggingBucketKey);
        } catch (IOException e) {
            log.error("Error while writing the log file to S3", e);
        } catch (S3Exception e) {
            log.error("Error occurred while uploading file to S3", e);
        }
    }

    // Log message to S3 (this method combines other steps)
    public void logMessageToS3(String message) {
        try {
            log.info("The Value of the Logging Bucket is: " + loggingBucketName);
            log.info("The Value of the Logging Bucket Key is: " + loggingBucketKey);
            log.info("Message being logged: '" + message + "'");
            if (message == null || message.trim().isEmpty()) {
                log.warn("The message is either null or empty!");
            }
            // Download the existing log file content
            String existingContent = downloadLogFilesFromS3();
            // Append the new log message
            String updatedContent = appendLogEntry(existingContent, message);
            // Upload the updated content
            uploadLogFileToS3(updatedContent);
        } catch (Exception e) {
            log.error("Error while logging messages to S3", e);
        }
    }
}