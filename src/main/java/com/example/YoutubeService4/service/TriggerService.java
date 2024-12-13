package com.example.YoutubeService4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TriggerService {
    public static final Logger log = LoggerFactory.getLogger(TriggerService.class);    
    private Test test;

    public TriggerService(Test test){
        this.test = test;
    }

    public String run(){
        test.testCase();
        return "Servies have all been triggered Successfully!";
    }


}
