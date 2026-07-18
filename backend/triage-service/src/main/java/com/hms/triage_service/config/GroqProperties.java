package com.hms.triage_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "groq")
public class GroqProperties {
    private String apiKey;
    private String url;
    private String model;
}
