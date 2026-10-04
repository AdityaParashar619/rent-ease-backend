package com.rentease.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentease.dto.ai.AiDescriptionRequest;
import com.rentease.dto.ai.AiDescriptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@Service
public class GroqAiService {

    private static final Logger log = LoggerFactory.getLogger(GroqAiService.class);

    @Value("${app.groq.api-key:}")
    private String apiKey;

    @Value("${app.groq.model:llama-3.3-70b-versatile}")
    private String model;

    @Value("${app.groq.base-url:https://api.groq.com/openai/v1}")
    private String baseUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public AiDescriptionResponse generateListingDescription(AiDescriptionRequest request) {
        String category = Optional.ofNullable(request.getCategory()).orElse("RESIDENTIAL");
        String locality = Optional.ofNullable(request.getLocality()).orElse("Bengaluru");
        String city = Optional.ofNullable(request.getCity()).orElse("Bengaluru");
        Integer bedrooms = request.getBedrooms() != null ? request.getBedrooms() : 2;
        Integer sqft = request.getCarpetAreaSqFt() != null ? request.getCarpetAreaSqFt() : 1200;
        String furnishing = Optional.ofNullable(request.getFurnishing()).orElse("SEMI_FURNISHED");
        List<String> keyFeatures = request.getKeyFeatures() != null ? request.getKeyFeatures() : List.of("Power Backup", "Gated Security", "Parking");

        // If GROQ_API_KEY is configured, invoke Groq API (e.g. llama-3.3-70b-versatile)
        if (apiKey != null && !apiKey.trim().isEmpty() && !apiKey.contains("YOUR_")) {
            try {
                String prompt = String.format(
                        "You are an expert real estate copywriter. Generate a JSON response with keys: 'title', 'description' (2-3 punchy, informative paragraphs with highlights), 'suggestedAmenities' (list of strings), and 'estimatedRentMin' and 'estimatedRentMax' (numbers in INR).\n" +
                                "Listing Details:\n- Category: %s\n- SubType: %s\n- Location: %s, %s\n- Bedrooms: %d\n- Area: %d sq.ft\n- Furnishing: %s\n- Features: %s\n" +
                                "Reply ONLY with valid JSON, no markdown formatting.",
                        category, request.getSubType(), locality, city, bedrooms, sqft, furnishing, String.join(", ", keyFeatures)
                );

                Map<String, Object> message = Map.of("role", "user", "content", prompt);
                Map<String, Object> requestBody = Map.of(
                        "model", model,
                        "messages", List.of(message),
                        "temperature", 0.7,
                        "response_format", Map.of("type", "json_object")
                );

                String jsonPayload = objectMapper.writeValueAsString(requestBody);

                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/chat/completions"))
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .timeout(Duration.ofSeconds(20))
                        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .build();

                HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JsonNode root = objectMapper.readTree(response.body());
                    String content = root.path("choices").get(0).path("message").path("content").asText();
                    JsonNode resultJson = objectMapper.readTree(content);

                    List<String> amenities = new ArrayList<>();
                    if (resultJson.has("suggestedAmenities")) {
                        resultJson.get("suggestedAmenities").forEach(n -> amenities.add(n.asText()));
                    }

                    return new AiDescriptionResponse(
                            resultJson.path("title").asText(bedrooms + "BHK Modern Home in " + locality),
                            resultJson.path("description").asText(),
                            amenities.isEmpty() ? keyFeatures : amenities,
                            resultJson.path("estimatedRentMin").asDouble(28000),
                            resultJson.path("estimatedRentMax").asDouble(38000),
                            model
                    );
                } else {
                    log.warn("Groq API returned error status: {} body: {}", response.statusCode(), response.body());
                }
            } catch (Exception e) {
                log.error("Exception calling Groq API: {}", e.getMessage(), e);
            }
        }

        // Deterministic Fallback if Groq API key is not yet set
        String title = String.format("Spacious %dBHK %s in %s", bedrooms, furnishing.replace("_", " ").toLowerCase(), locality);
        String desc = String.format(
                "Strategically located in %s, %s, this well-ventilated %s offers %d sq.ft of optimized living space. " +
                        "Equipped with modern fittings, %s setup, and prompt access to transit hubs and essential retail. " +
                        "Directly verified and managed transparently on RentEase.",
                locality, city, category.toLowerCase(), sqft, furnishing.toLowerCase()
        );

        return new AiDescriptionResponse(
                title,
                desc,
                keyFeatures,
                25000.0,
                38000.0,
                "groq-rule-based-fallback"
        );
    }
}
