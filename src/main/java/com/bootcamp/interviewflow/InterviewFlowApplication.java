package com.bootcamp.interviewflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InterviewFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterviewFlowApplication.class, args);
    }

}
