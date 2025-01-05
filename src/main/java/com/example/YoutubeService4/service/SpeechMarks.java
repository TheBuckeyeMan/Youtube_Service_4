package com.example.YoutubeService4.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

//Child is SubtitleEntry
@Service
public class SpeechMarks {
    private static final Logger log = LoggerFactory.getLogger(SpeechMarks.class);
    private final S3AsyncClient s3Client;
    private S3LoggingService s3LoggingService;

    public SpeechMarks(S3AsyncClient s3Client, S3LoggingService s3LoggingService){
        this.s3LoggingService = s3LoggingService;
        this.s3Client = s3Client;
    }

    //Method gets the saved speech marks from s3
    public String getSpeechMarks(String landingBucket, String speechMarksBucketKey){
        try{
            //This Creates the Get request to AWS S3
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                                                                .bucket(landingBucket)
                                                                .key(speechMarksBucketKey)
                                                                .build();

            //Convert byte array to string
            CompletableFuture<String> basicContentFuture = s3Client.getObject(getObjectRequest, AsyncResponseTransformer.toBytes()).thenApply(responseBytes -> {
                String basicContent = new String(responseBytes.asByteArray(),StandardCharsets.UTF_8);
                log.info("The content from the speechMarks file saved in the speechMarksData variable is: " + basicContent);
                return basicContent;
            });

            // Wait for and return the result
            String basicFileContent = basicContentFuture.join();
            if (basicFileContent != null){
                return basicFileContent;
            } else {
                log.error("Error: speechMarks Content is blank, or was unable to be retrieved form source");
                s3LoggingService.logMessageToS3("Error: SpeechMarks Content is blank, or was unable to be retrieved form source - line 45 on SpeechMarks.java: " + LocalDate.now() + " On: youtube-service-3" + ",");
                return "Error: speechMarks Content is blank, or was unable to be retrieved form source";
            }
        } catch (Exception e){
            log.error("Error Reading file form speechMarks S3: {}", e.getMessage(),e);
            s3LoggingService.logMessageToS3("Error: Error Reading file form S3 Line 50 of SpeechMarks.java: " + LocalDate.now() + " On: youtube-service-3" + ",");
            return "Error: Unable to read speechMarks file contents.";
        }
    };

    //Service to prep SpeechMarks for Video Compile
    public List<SubtitleEntry> getSpeechMarksForVideo(String landingBucket, String speechMarksBucketKey){
        try{
            String speechMarksJson = getSpeechMarks(landingBucket,speechMarksBucketKey);
            if (speechMarksJson == null || speechMarksJson.trim().isEmpty()){
                log.error("Speech marks JSON is empty or null.");
                s3LoggingService.logMessageToS3("Error: Speech marks JSON is empty or null Line 67 of SpeechMarks.java: " + LocalDate.now() + " On: youtube-service-3" + ",");
                return new ArrayList<>();
            }

            List<SubtitleEntry> subtitles = new ArrayList<>();
            Gson gson = new Gson();
            String[] lines = speechMarksJson.split("\n");
            for (String line : lines){
                try{
                    SubtitleEntry entry = gson.fromJson(line.trim(), SubtitleEntry.class);
                    entry.setValue(sanitizeText(entry.getValue()));
                    subtitles.add(entry);

                } catch (Exception e){
                    log.warn("Skipping invalid subtitle entry: {}", line, e);
                }
            }
            
            log.info("Successfully parsed {} subtitle entries.", subtitles.size());
            log.info("The value of the subtitles are: " + subtitles);
            log.info("The types of the subtitles are: " + subtitles.stream().map(SubtitleEntry::getType).toList());
            log.info("The values of the subtitles are: " + subtitles.stream().map(SubtitleEntry::getValue).toList());
            return subtitles;
        } catch (Exception e){
            log.error("Error parsing speechMarks JSON from S3: {}", e.getMessage(), e);
            s3LoggingService.logMessageToS3("Error: Error parsing speechMarks JSON from string to required format Line 80 of SpeechMarks.java: " + LocalDate.now() + " On: youtube-service-3" + ",");
            return new ArrayList<>();
        }
    }
    // Helper method to sanitize subtitle text
    private String sanitizeText(String text) {
        return text.replace("\\", "\\\\") // Escape backslashes
                   .replace("\\\\'", "'") // Handle pre-escaped single quotes
                   .replace("'", "\\\\'") // Escape single quotes for FFmpeg
                   .replace("\"", "\\\"") // Escape double quotes
                   .replace("\n", " ")    // Replace newlines with spaces
                   .replace("\r", " ")    // Replace carriage returns with spaces
                   .replaceAll("[^\\w\\s]", "") // Remove all special characters except words and spaces
                   .replace("%", "")      // Remove percent signs
                   .replace("$", "\\$")   // Escape dollar signs
                   .replace("#", "\\#")   // Escape hash signs
                   .replace("@", "\\@")   // Escape at symbols
                   .replace("^", "\\^")   // Escape carets
                   .replace("&", "\\&")   // Escape ampersands
                   .replace("*", "\\*")   // Escape asterisks
                   .replace("(", "\\(")   // Escape opening parentheses
                   .replace(")", "\\)");  // Escape closing parentheses
    }
}