package com.example.olxwebscrabber.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }

    @Bean(name = "phoneNumberParsingExecutor")
    public ExecutorService phoneNumberParsingExecutor() {
        return Executors.newFixedThreadPool(2);
    }

    @Bean(name = "pageParsingExecutor")
    public ExecutorService pageParsingExecutor() {
        return Executors.newFixedThreadPool(4);
    }
}