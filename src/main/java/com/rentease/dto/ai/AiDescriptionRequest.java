package com.rentease.dto.ai;

import java.util.List;

public class AiDescriptionRequest {
    private String category;
    private String subType;
    private String locality;
    private String city;
    private Double price;
    private Integer bedrooms;
    private Integer carpetAreaSqFt;
    private String furnishing;
    private List<String> keyFeatures;
    private String tone; // PROFESSIONAL, LUXURY, COZY, DIRECT

    public AiDescriptionRequest() {
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
    }

    public Integer getCarpetAreaSqFt() {
        return carpetAreaSqFt;
    }

    public void setCarpetAreaSqFt(Integer carpetAreaSqFt) {
        this.carpetAreaSqFt = carpetAreaSqFt;
    }

    public String getFurnishing() {
        return furnishing;
    }

    public void setFurnishing(String furnishing) {
        this.furnishing = furnishing;
    }

    public List<String> getKeyFeatures() {
        return keyFeatures;
    }

    public void setKeyFeatures(List<String> keyFeatures) {
        this.keyFeatures = keyFeatures;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }
}
