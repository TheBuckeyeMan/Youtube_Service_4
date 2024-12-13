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
    private GetVideoFile getVideoFile;
    private CompileVideo compileVideo;
    private PostFileToS3 postFileToS3;

    @Value("${aws.s3.key.video}")
    private String VideoBucketKey;

    @Value("${aws.s3.bucket.landing}")
    private String landingBucket;

    @Value("${aws.s3.key.audio}")
    private String audioBucketKey;

    @Value("${aws.s3.key.youtube}")
    private String youtubeBucketKey;

    private static final String FFPROBE_PATH = "/usr/bin/ffprobe";
    private static final String FFMPEG_PATH = "/usr/bin/ffmpeg";

    public TriggerService(DecideVideoKey decideVideoKey, GetAudioFile getAudioFile, GetVideoFile getVideoFile,CompileVideo compileVideo, PostFileToS3 postFileToS3){
        this.decideVideoKey = decideVideoKey;
        this.getAudioFile = getAudioFile;
        this.getVideoFile = getVideoFile;
        this.compileVideo = compileVideo;
        this.postFileToS3 = postFileToS3;
    }

    public String run(){
        //Decide which video to use
        String videoBuckeyKey = decideVideoKey.getVideoKey(VideoBucketKey);

        //Get Audio File
        Path audioFile = getAudioFile.getAudio(landingBucket, audioBucketKey);

        //Get Video File
        Path videoFile = getVideoFile.getVideo(landingBucket, videoBuckeyKey);

        //Compile Video File
        Path youtubeVideo = compileVideo.createVideo(audioFile, videoFile, FFMPEG_PATH);

        //Save to S3
        postFileToS3.PostFileToS3Bucket(youtubeVideo,landingBucket ,youtubeBucketKey);
        //Add in email loggging

        //Add Tests

        //TODO 
        //1. Remove get audio length - not needed
        //2. Remove Trim Video - not needed 
        //3 Refactor so that we dont have 2 methods to get the video and audio, refactr so we just need to have 1 set of code
        
        return "Servies have all been triggered Successfully!";
    }


}
