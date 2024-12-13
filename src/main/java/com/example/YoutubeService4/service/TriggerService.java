package com.example.YoutubeService4.service;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class TriggerService {
    public static final Logger log = LoggerFactory.getLogger(TriggerService.class);    
    private DecideVideoKey decideVideoKey;
    private GetAudioFile getAudioFile;
    private GetAudioLength getAudioLength;

    @Value("${aws.s3.key.video}")
    private String VideoBucketKey;

    @Value("${aws.s3.bucket.landing}")
    private String landingBucket;

    @Value("${aws.s3.key.audio}")
    private String audioBucketKey;


    public TriggerService(DecideVideoKey decideVideoKey, GetAudioFile getAudioFile, GetAudioLength getAudioLength){
        this.decideVideoKey = decideVideoKey;
        this.getAudioFile = getAudioFile;
        this.getAudioLength = getAudioLength;
    }

    public String run(){
        //Decide which video to use
        String videoBuckeyKey = decideVideoKey.getVideoKey(VideoBucketKey);

        //Get Audio File
        Path audioFile = getAudioFile.getAudio(landingBucket, audioBucketKey);

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
