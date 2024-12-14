package com.example.YoutubeService4.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

//TODO Refactor AudioFile and VideoFile to share a S3Bucketclient class 
@Service
public class GetAudioFile {
    public static final Logger log = LoggerFactory.getLogger(GetAudioFile.class);
    private S3Client s3Client;
    private S3LoggingService s3LoggingService;

    public GetAudioFile(S3Client s3Client, S3LoggingService s3LoggingService){
        this.s3Client = s3Client;
        this.s3LoggingService = s3LoggingService;
    }

    public Path getAudio(String landingBucket, String audioBucketKey){
        try{
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(landingBucket)
                .key(audioBucketKey)
                .build();
            
            //Get File from S3
            log.info("Attempting to retrieve Audio File!");
            ResponseInputStream<?> objecStream = s3Client.getObject(getObjectRequest);

            //Save File Locally
            Path tempFile = Files.createTempFile("s3-audio-" + UUID.randomUUID(), ".mp3");
            Files.copy(objecStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

            //Return File
            log.info("Audio File Downloaded Successfully. Saved to: " + tempFile);
            return tempFile;
        } catch (IOException e){
            log.error("Error: Error while trying to retrieve audio file from S3. Line 43 on GetAudioFile.java", e);
            s3LoggingService.logMessageToS3("Error: Error while trying to retrieve audio file from S3. Line 43 on GetAudioFile.java: " + LocalDate.now() + " On: youtube-service-4" + ",");
            throw new RuntimeException("Error: Error while trying to retrieve audio file from S3. Line 43 on GetAudioFile.java",e);
        }
    }
}
