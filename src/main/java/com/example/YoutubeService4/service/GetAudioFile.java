package com.example.YoutubeService4.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

@Service
public class GetAudioFile {
    public static final Logger log = LoggerFactory.getLogger(GetAudioFile.class);
    private S3Client s3Client;

    public GetAudioFile(S3Client s3Client){
        this.s3Client = s3Client;
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
            log.info("File Downloaded Successfully. Saved to: " + tempFile);
            return tempFile;
        } catch (IOException e){
            log.error("Error: Error while trying to retrieve audio file from S3. Line 43 on GetAudioFile.java", e);
            //TODO Add in error email here
            throw new RuntimeException("Error: Error while trying to retrieve audio file from S3. Line 43 on GetAudioFile.java",e);
        }
    }
}
