package com.example.YoutubeService4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import com.example.YoutubeService4.service.TriggerService;

@SpringBootApplication
public class YoutubeService4Application {
    private static final Logger log = LoggerFactory.getLogger(YoutubeService4Application.class);

    public static void main(String[] args) {
        int exitCode = 0;
        ConfigurableApplicationContext context = null;

        try {
            context = SpringApplication.run(YoutubeService4Application.class, args);

            // Run All Services
            TriggerService triggerService = context.getBean(TriggerService.class);
            triggerService.run();

            log.info("Service Successfully Triggered!");
        } catch (Exception e) {
            log.error("An error occurred during application execution: {}", e.getMessage(), e);
            exitCode = 1; // Non-zero exit code indicates an error
        } finally {
            if (context != null) {
                SpringApplication.exit(context);
            }
            log.info("Exiting the service with exit code: {}", exitCode);
            System.exit(exitCode);
        }
    }
}