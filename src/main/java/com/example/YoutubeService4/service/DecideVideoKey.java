package com.example.YoutubeService4.service;

import java.time.LocalDate;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DecideVideoKey {
    private static final Logger log = LoggerFactory.getLogger(DecideVideoKey.class);
    private S3LoggingService s3LoggingService;

    public DecideVideoKey(S3LoggingService s3LoggingService){
        this.s3LoggingService = s3LoggingService;
    }

    public String getVideoKey(String videoBucketKey){
        try{
        videoBucketKey = videoBucketKey + getRandomNumber() + ".mp4";
        log.info("The Key fo the Video being use to compile is: " + videoBucketKey);
        return videoBucketKey;

        } catch (Exception e){
            log.error("Error: There was an error getting the video bucket key: Line 18 on DecideVideoKey.java");
            s3LoggingService.logMessageToS3("Error: There was an error getting the video bucket key: Line 18 on DecideVideoKey.java: " + LocalDate.now() + " On: youtube-service-4" + ",");
        }
        return "Error: There was an error getting the video bucket key: Line 20 on DecideVideoKey.java";
    }

    public String getRandomNumber(){
        Random random = new Random();
        int number = random.nextInt(10) + 1;
        return String.valueOf(number);
    }
}
