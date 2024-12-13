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
    private GetVideoFile getVideoFile;
    private TrimVideo trimVideo;
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

    public TriggerService(DecideVideoKey decideVideoKey, GetAudioFile getAudioFile, GetAudioLength getAudioLength, GetVideoFile getVideoFile, TrimVideo trimVideo, CompileVideo compileVideo, PostFileToS3 postFileToS3){
        this.decideVideoKey = decideVideoKey;
        this.getAudioFile = getAudioFile;
        this.getAudioLength = getAudioLength;
        this.getVideoFile = getVideoFile;
        this.trimVideo = trimVideo;
        this.compileVideo = compileVideo;
        this.postFileToS3 = postFileToS3;
    }

    public String run(){
        //Decide which video to use
        String videoBuckeyKey = decideVideoKey.getVideoKey(VideoBucketKey);

        //Get Audio File
        Path audioFile = getAudioFile.getAudio(landingBucket, audioBucketKey);

        //Get Audio File Length
        long audioLength = getAudioLength.audioLength(audioFile, FFPROBE_PATH);

        //Get Video File
        Path videoFile = getVideoFile.getVideo(landingBucket, videoBuckeyKey);

        //Trim the video file
        //Path trimmedVideo = trimVideo.newVideo(videoFile, audioLength, FFMPEG_PATH);

        //Compile Video File
        Path youtubeVideo = compileVideo.createVideo(audioFile, videoFile, FFMPEG_PATH);

        //Save to S3
        postFileToS3.PostFileToS3Bucket(youtubeVideo,landingBucket ,youtubeBucketKey);
        //Add in email loggging

        //Add Tests
        return "Servies have all been triggered Successfully!";
    }


}
