package com.example.YoutubeService4.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

@Service
public class TrimVideo {
    public static final Logger log = LoggerFactory.getLogger(TrimVideo.class);

    public Path newVideo(Path videoFile, long audioLength, String FFMPEG_PATH){
        try{
            Path trimmedVideoFile = Files.createTempFile("trimmed-video-" + UUID.randomUUID(), ".mp4");

            // Initialize FFmpeg and build the trimming command
            FFmpeg ffmpeg = new FFmpeg(FFMPEG_PATH);
            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(videoFile.toAbsolutePath().toString()) // Input video file
                    .addOutput(trimmedVideoFile.toAbsolutePath().toString()) // Output video file
                    .setStartOffset(0, TimeUnit.SECONDS) // Start from the beginning
                    .setDuration(audioLength, TimeUnit.SECONDS) // Set duration to match audio
                    .done();

            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);
            executor.createJob(builder).run();

            log.info("The Video has been Trimmed Successfully!");
            logTrimmedVideoLength(trimmedVideoFile, FFMPEG_PATH.replace("ffmpeg", "ffprobe"));
            return trimmedVideoFile;
        } catch (IOException e) {
            log.error("Error Trimming the Video Line 39 of TrimVideo.java", e);
            //TODO Add email error here 
            throw new RuntimeException("Error Trimming the Video Line 41 of TrimVideo.java", e);
        }
        
    }

    private void logTrimmedVideoLength(Path videoFile, String FFPROBE_PATH) {
        try {
            FFprobe ffprobe = new FFprobe(FFPROBE_PATH);
            FFmpegProbeResult probeResult = ffprobe.probe(videoFile.toAbsolutePath().toString());
            double durationInSeconds = probeResult.getFormat().duration;

            log.info("The length of the trimmed video file is: ", durationInSeconds + " Seconds");
        } catch (IOException e) {
            log.error("Error retrieving the length of the trimmed video file", e);
        }
    }
}