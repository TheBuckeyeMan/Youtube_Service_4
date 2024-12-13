package com.example.YoutubeService4.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

@Service
public class CompileVideo {
    private static final Logger log = LoggerFactory.getLogger(CompileVideo.class);


    public Path createVideo(Path audioFile, Path videoFile, String FFMPEG_PATH){
        try{
            log.info("Initializing Video Compile");
            Path youtubeVideoFile = Files.createTempFile("youtube-video-" + UUID.randomUUID(), ".mp4");

            // Initialize FFmpeg and build the trimming command
            FFmpeg ffmpeg = new FFmpeg(FFMPEG_PATH);
            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(videoFile.toAbsolutePath().toString()) // Input video file
                    .addInput(audioFile.toAbsolutePath().toString())
                    .addOutput(youtubeVideoFile.toAbsolutePath().toString()) // Output video file
                    .setFormat("mp4")
                    .addExtraArgs("-map", "0:v:0") // Map video stream from first input
                    .addExtraArgs("-map", "1:a:0") // Map audio stream from second input
                    //.addExtraArgs("-c:v", "libx264") // Re-encode video for compatibility - Required if Original video does not have H.264 encoding
                    .addExtraArgs("-c:v", "copy")
                    .addExtraArgs("-c:a", "aac") // Encode audio in AAC format
                    .addExtraArgs("-b:a", "192k") // Set audio bitrate
                    .addExtraArgs("-shortest") // Match the shortest input duration
                    .done();
  
            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);
            executor.createJob(builder).run();

            log.info("The Youtube Video has been Successfully Created!");
            return youtubeVideoFile;
        } catch (IOException e){
            log.error("Error: Unable to compile audio file with video file. Youtube video NOT Created. Line 44 of CompileVideo.java", e);
            //TODO Add Email Error Handling
            throw new RuntimeException("Error: Unable to compile audio file with video file. Youtube video NOT Created. Line 46 of CompileVideo.java", e);
    }
    }
}
