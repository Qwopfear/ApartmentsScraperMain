package com.example.olxwebscrabber.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class WebClientImpl implements WebClient {

    private final RestTemplate restTemplate;

    @Override
    public String parsePage(String url) {
        return restTemplate.getForEntity(url, String.class).getBody();
    }
}
