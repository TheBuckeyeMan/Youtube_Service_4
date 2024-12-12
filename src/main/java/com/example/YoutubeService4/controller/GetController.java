package com.example.YoutubeService4.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class GetController {
    public static final Logger log = LoggerFactory.getLogger(GetController.class);

    @Value("${spring.environment}")
    private String environment;

    @Value("${aws.s3.bucket.landing}")
    private String landingBucket;

    @Value("${aws.s3.key.audio}")
    private String audioBucketKey;

    @Value("${aws.s3.key.video}")
    private String videoBuckeyKey;

    @Value("${aws.s3.key.youtube}")
    private String youtubeBucketKey;
    
    @GetMapping("/test")
    public void testApi(){
        log.info("Message: " + "This is a test");
    }

    @GetMapping("/createvideo")
    public String createVideo(){
        //Initialization Logs
        log.info("The Active Environment is set to: " + environment);
        log.info("Begining to Compile Youtube Video");
        //1. Download audio file form S3

        //2. Download Video file from S3

        //3. Trim video to be the length of the audio file 

        //4. Combine Audio and Video File

        //5. Send data back to S3

        //6. Add in error logging and email indicators

        //7. Add Unit Tests


        log.info("Video has successfully been created");
        return "Video has successfully been created";
    }

}
