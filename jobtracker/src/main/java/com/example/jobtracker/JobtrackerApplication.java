package com.example.jobtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class JobtrackerApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobtrackerApplication.class, args);
    }
}
