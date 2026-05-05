package com.proyecto.invoicepipeline.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GeminiConfig {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.base-url}")
    private String baseUrl;

    @Value("${gemini.model}")
    private String model;

    @Bean
    public WebClient geminiWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-goog-api-key", apiKey)
                .build();
    }

    public String getModel() {
        return model;
    }
}
