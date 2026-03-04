package com.SG_Predictor.SG_Predictor.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
public class WebClientConfig {

    @Value("${ml.service.base-url:http://localhost:8000}")
    private String mlServiceBaseUrl;

    @Bean
    public WebClient mlWebClient(){
        log.info("[STARTUP] Configuring ML WebClient with base URL: {}", mlServiceBaseUrl);
        return WebClient.builder().baseUrl(mlServiceBaseUrl).build();
    }
}
