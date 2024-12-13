package com.example.YoutubeService4.service;

import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class PostFileToS3 {
    private static final Logger log = LoggerFactory.getLogger(PostFileToS3.class);
    private final S3Client s3Client;
  //TODO private S3LoggingService s3LoggingService;
    
    public PostFileToS3(S3Client s3Client){
        this.s3Client = s3Client;
     //TODO   this.s3LoggingService = s3LoggingService;
    }

    public void PostFileToS3Bucket(Path youtubeVideo, String landingBucket, String youtubeBucketKey){
        try{
            log.info("Uploading the Youtube Video to AWS S3");
            //Verify file Exists
            if (!youtubeVideo.toFile().exists()){
                log.error("Error: File does not exist: {}", youtubeVideo);
           //TODO   s3LoggingService.logMessageToS3("Error: Error on PostFileToS3.java. S3File Does not Exist - PostFileToS3 line 29: " + LocalDate.now() + " On: youtube-service-3" + ",");
            }

            //Create the Put Object Request
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                                                .bucket(landingBucket)
                                                                .key(youtubeBucketKey)
                                                                .build();
            
            //Upload the file
            s3Client.putObject(putObjectRequest, RequestBody.fromFile(youtubeVideo.toFile()));
            log.info("YouTube Video has been successfully saved to the " + landingBucket + " Bucket in directory: " + youtubeBucketKey);
        } catch (Exception e){
            log.error("Error: Error on PostFileToS3 - uploading the youtube Video to the S3 Bucket has failed. Line 42", e.getMessage(),e);
           //TODO s3LoggingService.logMessageToS3("Error: Error on PostFileToS3.java. Filed To Upload file to S3 - PostFileToS3 line 41: " + LocalDate.now() + " On: youtube-service-3" + ",");
            throw new RuntimeException("Filed To Upload Video to S3", e);
        }  
    }
}