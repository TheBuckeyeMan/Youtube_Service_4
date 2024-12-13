package com.example.YoutubeService4.service;

import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DecideVideoKey {
    private static final Logger log = LoggerFactory.getLogger(DecideVideoKey.class);
    public String getVideoKey(String videoBucketKey){
        try{

        videoBucketKey = videoBucketKey + getRandomNumber() + ".mp4";
        log.info("The Key fo the Video being use to compile is: " + videoBucketKey);
        return videoBucketKey;

        } catch (Exception e){
            log.error("Error: There was an error getting the video bucket key: Line 18 on DecideVideoKey.java");
            //TODO Add Email Error Here
        }
        return "Error: There was an error getting the video bucket key: Line 20 on DecideVideoKey.java";
    }

    public String getRandomNumber(){
        Random random = new Random();
        int number = random.nextInt(4) + 1;
        return String.valueOf(number);
    }
}
