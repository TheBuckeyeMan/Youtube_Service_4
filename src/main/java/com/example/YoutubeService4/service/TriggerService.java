package com.example.YoutubeService4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class TriggerService {
    public static final Logger log = LoggerFactory.getLogger(TriggerService.class);    
    private DecideVideoKey decideVideoKey;

    @Value("${aws.s3.key.video}")
    private String VideoBucketKey;


    public TriggerService(DecideVideoKey decideVideoKey){
        this.decideVideoKey = decideVideoKey;
    }

    public String run(){
        //Decide which video to use
        String videoBuckeyKey = decideVideoKey.getVideoKey(VideoBucketKey);

        //Get Audio File

        //Get Audio File Length

        //Get Video File

        //Trim the video file

        //Compile Video File

        //Save to S3

        //Add in email loggging

        //Add Tests















        return "Servies have all been triggered Successfully!";
    }


}
