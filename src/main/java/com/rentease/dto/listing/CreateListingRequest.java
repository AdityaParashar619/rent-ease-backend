package com.rentease.dto.listing;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public class CreateListingRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Category ID is required")
    private String categoryId;

    @NotBlank(message = "SubCategory ID is required")
    private String subCategoryId;

    @NotBlank(message = "City ID is required")
    private String cityId;

    @NotBlank(message = "Locality ID is required")
    private String localityId;

    @NotBlank(message = "Masked address is required")
    private String maskedAddress;

    private String fullAddress;
    private String pincode;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @NotBlank(message = "Pricing unit is required")
    private String pricingUnit; // /month, /day, /event

    private BigDecimal securityDeposit;

    @NotBlank(message = "Owner type is required")
    private String ownerType; // DIRECT_OWNER, BROKER

    @NotBlank(message = "Description is required")
    private String description;

    private List<String> images;
    private List<String> amenityIds;
    private List<String> amenityNames;

    // Optional category specifics
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer carpetAreaSqFt;
    private String furnishingType;
    private BigDecimal maintenanceMonthly;

    private String brand;
    private String model;
    private Integer year;
    private String transmission;
    private String fuelType;
    private Integer seatingCapacity;

    private Integer guestCapacity;
    private String indoorOutdoor;

    public CreateListingRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getLocalityId() {
        return localityId;
    }

    public void setLocalityId(String localityId) {
        this.localityId = localityId;
    }

    public String getMaskedAddress() {
        return maskedAddress;
    }

    public void setMaskedAddress(String maskedAddress) {
        this.maskedAddress = maskedAddress;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getPricingUnit() {
        return pricingUnit;
    }

    public void setPricingUnit(String pricingUnit) {
        this.pricingUnit = pricingUnit;
    }

    public BigDecimal getSecurityDeposit() {
        return securityDeposit;
    }

    public void setSecurityDeposit(BigDecimal securityDeposit) {
        this.securityDeposit = securityDeposit;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getAmenityIds() {
        return amenityIds;
    }

    public void setAmenityIds(List<String> amenityIds) {
        this.amenityIds = amenityIds;
    }

    public List<String> getAmenityNames() { return amenityNames; }
    public void setAmenityNames(List<String> amenityNames) { this.amenityNames = amenityNames; }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
    }

    public Integer getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(Integer bathrooms) {
        this.bathrooms = bathrooms;
    }

    public Integer getCarpetAreaSqFt() {
        return carpetAreaSqFt;
    }

    public void setCarpetAreaSqFt(Integer carpetAreaSqFt) {
        this.carpetAreaSqFt = carpetAreaSqFt;
    }

    public String getFurnishingType() {
        return furnishingType;
    }

    public void setFurnishingType(String furnishingType) {
        this.furnishingType = furnishingType;
    }

    public BigDecimal getMaintenanceMonthly() {
        return maintenanceMonthly;
    }

    public void setMaintenanceMonthly(BigDecimal maintenanceMonthly) {
        this.maintenanceMonthly = maintenanceMonthly;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public Integer getSeatingCapacity() { return seatingCapacity; }
    public void setSeatingCapacity(Integer seatingCapacity) { this.seatingCapacity = seatingCapacity; }

    public Integer getGuestCapacity() {
        return guestCapacity;
    }

    public void setGuestCapacity(Integer guestCapacity) {
        this.guestCapacity = guestCapacity;
    }

    public String getIndoorOutdoor() {
        return indoorOutdoor;
    }

    public void setIndoorOutdoor(String indoorOutdoor) {
        this.indoorOutdoor = indoorOutdoor;
    }
}
