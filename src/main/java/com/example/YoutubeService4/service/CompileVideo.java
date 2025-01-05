package com.example.YoutubeService4.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
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
    private S3LoggingService s3LoggingService;

    public CompileVideo(S3LoggingService s3LoggingService){
        this.s3LoggingService = s3LoggingService;
    }


    public Path createVideo(Path audioFile, Path videoFile, List<SubtitleEntry> speechMarkData, String FFMPEG_PATH){
        try{
            log.info("Initializing Video Compile");
            Path youtubeVideoFile = Files.createTempFile("youtube-video-" + UUID.randomUUID(), ".mp4");

            //Build the subtitles for the video
            StringBuilder subtitlesFilter = new StringBuilder();
            for (int i = 0; i < speechMarkData.size(); i++) {
                SubtitleEntry entry = speechMarkData.get(i);
                double startTime = entry.getTimeInSeconds();
                double endTime;
            
                if (i < speechMarkData.size() - 1) {
                    // Calculate duration using the next word's time
                    endTime = speechMarkData.get(i + 1).getTimeInSeconds();
                } else {
                    // Default duration for the last word
                    endTime = startTime + 1.0; // 1 second
                }
            
                String text = entry.getValue();
                subtitlesFilter.append(String.format(
                    "drawtext=fontfile=/path/to/font.ttf:fontsize=72:fontcolor=white:borderw=4:bordercolor=black:" +
                    "text='%s':enable='between(t\\,%.3f\\,%.3f)':x=(w-text_w)/2:y=(h-text_h)/2,", 
                    text, startTime, endTime));
            }
            
            // Remove trailing comma
            if (subtitlesFilter.length() > 0) {
                subtitlesFilter.setLength(subtitlesFilter.length() - 1);
            }
            log.info("Generated FFmpeg subtitles filter: {}", subtitlesFilter.toString());
            log.debug("Generated FFmpeg subtitles filter: {}", subtitlesFilter.toString());

            // Initialize FFmpeg and build the trimming command
            FFmpeg ffmpeg = new FFmpeg(FFMPEG_PATH);
            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(videoFile.toAbsolutePath().toString()) // Input video file
                    .addInput(audioFile.toAbsolutePath().toString())
                    .addOutput(youtubeVideoFile.toAbsolutePath().toString()) // Output video file
                    .setFormat("mp4")
                    .addExtraArgs("-map", "0:v:0") // Map video stream from first input
                    .addExtraArgs("-map", "1:a:0") // Map audio stream from second input
                    .addExtraArgs("-c:v", "libx264") // Re-encode video for compatibility - Required if Original video does not have H.264 encoding
                    .addExtraArgs("-preset", "ultrafast") // Use ultrafast preset for encoding
                    //.addExtraArgs("-c:v", "copy") //uncomment if we dont need to add subtitles or the words to the video and comment out the 2 above
                    .addExtraArgs("-c:a", "aac") // Encode audio in AAC format
                    .addExtraArgs("-b:a", "192k") // Set audio bitrate
                    .addExtraArgs("-shortest") // Match the shortest input duration
                    .addExtraArgs("-vf", subtitlesFilter.toString()) // Add Words to Video
                    .done();
  
            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);
            executor.createJob(builder).run();

            log.info("The Youtube Video has been Successfully Created!");
            return youtubeVideoFile;
        } catch (IOException e){
            log.error("Error: Unable to compile audio file with video file. Youtube video NOT Created. Line 44 of CompileVideo.java", e);
            s3LoggingService.logMessageToS3("Error: Unable to compile audio file with video file. Youtube video NOT Created. Line 44 of CompileVideo.java: " + LocalDate.now() + " On: youtube-service-4" + ",");
            throw new RuntimeException("Error: Unable to compile audio file with video file. Youtube video NOT Created. Line 46 of CompileVideo.java", e);
    }
    }

    // Helper method to format time in seconds to FFmpeg's time format
    private String formatTime(double timeInSeconds) {
        return String.format("%.3f", timeInSeconds);
    }
}
