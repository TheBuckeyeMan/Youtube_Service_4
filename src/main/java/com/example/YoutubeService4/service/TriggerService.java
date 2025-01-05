package com.example.YoutubeService4.service;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

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
    private S3LoggingService s3LoggingService;
    private SpeechMarks speechMarks;

    @Value("${aws.s3.key.video}")
    private String VideoBucketKey;

    @Value("${aws.s3.bucket.landing}")
    private String landingBucket;

    @Value("${aws.s3.key.audio}")
    private String audioBucketKey;

    @Value("${aws.s3.key.youtube}")
    private String youtubeBucketKey;

    @Value("${aws.s3.key.speech}")
    private String speechMarksKey;

    private static final String FFMPEG_PATH = "/usr/bin/ffmpeg";

    public TriggerService(DecideVideoKey decideVideoKey, GetAudioFile getAudioFile, GetVideoFile getVideoFile,CompileVideo compileVideo, PostFileToS3 postFileToS3, S3LoggingService s3LoggingService, SpeechMarks speechMarks){
        this.decideVideoKey = decideVideoKey;
        this.getAudioFile = getAudioFile;
        this.getVideoFile = getVideoFile;
        this.compileVideo = compileVideo;
        this.postFileToS3 = postFileToS3;
        this.s3LoggingService = s3LoggingService;
        this.speechMarks = speechMarks;
    }

    public String run(){
        //Decide which video to use
        String videoBuckeyKey = decideVideoKey.getVideoKey(VideoBucketKey);

        //Get Audio File
        Path audioFile = getAudioFile.getAudio(landingBucket, audioBucketKey);

        //Get Video File
        Path videoFile = getVideoFile.getVideo(landingBucket, videoBuckeyKey);

        //Get Speech Marks for video
        List<SubtitleEntry> speechMarksData = speechMarks.getSpeechMarksForVideo(landingBucket, speechMarksKey);

        //Compile Video File
        Path youtubeVideo = compileVideo.createVideo(audioFile, videoFile, speechMarksData, FFMPEG_PATH);

        //Save to S3
        postFileToS3.PostFileToS3Bucket(youtubeVideo,landingBucket ,youtubeBucketKey);

        //Add in email loggging
        log.info("The Service has successfully complete and the audio file is saved in the " + youtubeBucketKey + " Directory of the " + landingBucket + " Bucket!");
        s3LoggingService.logMessageToS3("Succcess: Success occured at: " + LocalDateTime.now() + " On: youtube-service-4" + ",");
        log.info("Final: The ECS Task has triggered successfully and the audio file is now saved in the S3 Bucket: " + landingBucket);
        return "Servies have all been triggered Successfully!";

        //TODO 
        //Add Tests
        //3 Refactor so that we dont have 2 methods to get the video and audio, refactr so we just need to have 1 set of code
    }
}