package com.eazybytes.eazystore.service.impl;

import com.eazybytes.eazystore.service.IChatService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ChatServiceImpl implements IChatService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model:gemini-pro-1.0}")
    private String model;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta}")
    private String apiUrl;

    @Override
    public String ask(String prompt) {
        try {
            var requestBody = new HashMap<String, Object>();
            requestBody.put("contents", new Object[] {
                Map.of("parts", new Object[] { Map.of("text", prompt) })
            });
            requestBody.put("generationConfig", Map.of(
                "temperature", 0.7,
                "maxOutputTokens", 250
            ));

            String url = String.format("%s/models/%s:generateContent?key=%s", apiUrl, model, apiKey);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(20))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(requestBody)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JsonNode root = OBJECT_MAPPER.readTree(response.body());
                JsonNode candidates = root.path("candidates");
                if (candidates.isArray() && candidates.size() > 0) {
                    JsonNode first = candidates.get(0);
                    JsonNode contentParts = first.path("content").path("parts");
                    if (contentParts.isArray() && contentParts.size() > 0) {
                        String output = contentParts.get(0).path("text").asText(null);
                        if (output != null && !output.isBlank()) {
                            return output.trim();
                        }
                    }
                }
                // fallback for alternate response formats
                String textOutput = root.path("output").asText(null);
                if (textOutput != null && !textOutput.isBlank()) {
                    return textOutput.trim();
                }
                return "I’m sorry, I couldn’t understand the response from the LLM. Please try again later.";
            }

            log.error("Gemini API error ({}): {}", response.statusCode(), response.body());
            return "Sorry, I couldn’t reach the assistant right now. Please try again in a moment.";

        } catch (IOException | InterruptedException e) {
            log.error("Error calling Gemini API", e);
            Thread.currentThread().interrupt();
            return "Sorry, something went wrong while contacting the assistant. Please try again later.";
        }
    }
}
