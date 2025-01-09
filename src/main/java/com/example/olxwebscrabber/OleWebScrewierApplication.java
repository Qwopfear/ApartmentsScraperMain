package com.example.olxwebscrabber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OlxWebScrabberApplication {

    public static void main(String[] args) {
        SpringApplication.run(OlxWebScrabberApplication.class, args);
    }

}
