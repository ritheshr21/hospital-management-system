package com.hms.triage_service.client;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.hms.triage_service.config.GroqProperties;
import com.hms.triage_service.exception.TriageException;

import lombok.RequiredArgsConstructor;

/**
 * Thin wrapper over Groq's OpenAI-compatible chat completions API.
 * Returns the raw assistant message content (expected to be a JSON string).
 */
@Component
@RequiredArgsConstructor
public class GroqClient {

    private final RestClient restClient;
    private final GroqProperties props;

    public String complete(String systemPrompt, String userPrompt) {
        if (props.getApiKey() == null || props.getApiKey().isBlank()) {
            throw new TriageException("GROQ_API_KEY is not configured on triage-service");
        }

        Map<String, Object> body = Map.of(
                "model", props.getModel(),
                "temperature", 0.2,
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)));

        JsonNode response;
        try {
            response = restClient.post()
                    .uri(props.getUrl())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + props.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (Exception e) {
            throw new TriageException("Failed to reach Groq API: " + e.getMessage(), e);
        }

        if (response == null || !response.has("choices") || response.get("choices").isEmpty()) {
            throw new TriageException("Groq API returned no choices");
        }
        return response.get("choices").get(0).path("message").path("content").asText();
    }
}
