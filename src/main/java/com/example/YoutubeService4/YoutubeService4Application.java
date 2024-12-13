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
        ConfigurableApplicationContext context = SpringApplication.run(YoutubeService4Application.class, args);

        TriggerService triggerService = context.getBean(TriggerService.class);
        triggerService.run();

        log.info("Service Successfully Triggered!");
        log.info("Exiting the service now!");
        int exitCode = SpringApplication.exit(context);
        log.info("Service Exited Successfully");
        System.exit(exitCode);
    }
}