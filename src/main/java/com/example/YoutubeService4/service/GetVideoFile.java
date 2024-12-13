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


//TODO Refactor AudioFile and VideoFile to share a S3Bucketclient class 
@Service
public class GetVideoFile {
    public static final Logger log = LoggerFactory.getLogger(GetVideoFile.class);
    private S3Client s3Client;

    public GetVideoFile(S3Client s3Client){
        this.s3Client = s3Client;
    }


    public Path getVideo(String landingBucket, String videoBucketKey){
        try{
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(landingBucket)
                .key(videoBucketKey)
                .build();
            
            //Get File from S3
            log.info("Attempting to retrieve Video File!");
            ResponseInputStream<?> objecStream = s3Client.getObject(getObjectRequest);

            //Save File Locally
            Path tempVideoFile = Files.createTempFile("s3-video-" + UUID.randomUUID(), ".mp4");
            Files.copy(objecStream, tempVideoFile, StandardCopyOption.REPLACE_EXISTING);

            //Return File
            log.info("Video File Downloaded Successfully. Saved to: " + tempVideoFile);
            return tempVideoFile;
        } catch (IOException e){
            log.error("Error: Error while trying to retrieve Video file from S3. Line 46 on GetVideoFile.java", e);
            //TODO Add in error email here
            throw new RuntimeException("Error: Error while trying to retrieve Video file from S3. Line 48 on GetVideoFile.java",e);
        }
    }
}
