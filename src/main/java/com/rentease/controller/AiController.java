package com.rentease.controller;

import com.rentease.dto.ai.AiDescriptionRequest;
import com.rentease.dto.ai.AiDescriptionResponse;
import com.rentease.dto.common.ApiResponse;
import com.rentease.service.GroqAiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/v1/ai", "/api/ai"})
@Tag(name = "AI Services (Groq)", description = "AI-powered property description generation, rent valuation, and listing assistance powered by Groq Llama 3.3")
public class AiController {

    private final GroqAiService groqAiService;

    public AiController(GroqAiService groqAiService) {
        this.groqAiService = groqAiService;
    }

    @PostMapping("/generate-description")
    @Operation(summary = "Generate optimized property description, title, and amenity tags using Groq AI")
    public ResponseEntity<ApiResponse<AiDescriptionResponse>> generateDescription(@RequestBody AiDescriptionRequest request) {
        AiDescriptionResponse response = groqAiService.generateListingDescription(request);
        return ResponseEntity.ok(ApiResponse.ok("Generated description successfully via Groq", response));
    }
}
