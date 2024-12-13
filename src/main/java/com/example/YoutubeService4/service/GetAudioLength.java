package com.example.YoutubeService4.service;

import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

@Service
public class GetAudioLength {
    private static final Logger log = LoggerFactory.getLogger(GetAudioLength.class);

    public long audioLength(Path audioFile, String FFPROBE_PATH){
        try {
            FFprobe ffprobe = new FFprobe(FFPROBE_PATH);

            FFmpegProbeResult probeResult = ffprobe.probe(audioFile.toAbsolutePath().toString());

            //Extract Length
            double durationInSeconds = probeResult.getFormat().duration;
            long longDuration = (long) Math.ceil(durationInSeconds);
            log.info("Audio Length of the Audio File is: " + durationInSeconds);
            return longDuration;
        } catch (Exception e) {
            log.error("Error: Error while retrieving audio length on GetAudioLength Line 27");
            //TODO Add email error
            throw new RuntimeException("Error: Error while retrieving audio length on GetAudioLength Line 27");
        }
    }
}
