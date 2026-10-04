package com.rentease.dto.ai;

import java.util.List;

public class AiDescriptionResponse {
    private String title;
    private String description;
    private List<String> suggestedAmenities;
    private Double estimatedRentMin;
    private Double estimatedRentMax;
    private String modelUsed;

    public AiDescriptionResponse() {
    }

    public AiDescriptionResponse(String title, String description, List<String> suggestedAmenities, Double estimatedRentMin, Double estimatedRentMax, String modelUsed) {
        this.title = title;
        this.description = description;
        this.suggestedAmenities = suggestedAmenities;
        this.estimatedRentMin = estimatedRentMin;
        this.estimatedRentMax = estimatedRentMax;
        this.modelUsed = modelUsed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSuggestedAmenities() {
        return suggestedAmenities;
    }

    public void setSuggestedAmenities(List<String> suggestedAmenities) {
        this.suggestedAmenities = suggestedAmenities;
    }

    public Double getEstimatedRentMin() {
        return estimatedRentMin;
    }

    public void setEstimatedRentMin(Double estimatedRentMin) {
        this.estimatedRentMin = estimatedRentMin;
    }

    public Double getEstimatedRentMax() {
        return estimatedRentMax;
    }

    public void setEstimatedRentMax(Double estimatedRentMax) {
        this.estimatedRentMax = estimatedRentMax;
    }

    public String getModelUsed() {
        return modelUsed;
    }

    public void setModelUsed(String modelUsed) {
        this.modelUsed = modelUsed;
    }
}
