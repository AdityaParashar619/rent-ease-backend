package com.rentease.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "property_details")
public class PropertyDetails {

    @Id
    @Column(name = "listing_id", length = 64)
    private String listingId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "listing_id")
    private Listing listing;

    private Integer bedrooms;
    private Integer bathrooms;

    @Column(name = "carpet_area_sq_ft")
    private Integer carpetAreaSqFt;

    @Column(name = "furnishing_type", length = 30)
    private String furnishingType;

    @Column(name = "maintenance_monthly", precision = 10, scale = 2)
    private BigDecimal maintenanceMonthly = BigDecimal.ZERO;

    @Column(name = "gender_preference", length = 30)
    private String genderPreference = "ANY";

    @Column(name = "food_included")
    private boolean foodIncluded = false;

    @Column(name = "pet_friendly")
    private boolean petFriendly = true;

    @Column(name = "is_gated_society")
    private boolean gatedSociety = true;

    @Column(name = "floor_number")
    private Integer floorNumber;

    @Column(name = "total_floors")
    private Integer totalFloors;

    @Column(name = "available_from")
    private LocalDate availableFrom;

    public PropertyDetails() {
    }

    public String getListingId() {
        return listingId;
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
    }

    public Listing getListing() {
        return listing;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }

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

    public String getGenderPreference() {
        return genderPreference;
    }

    public void setGenderPreference(String genderPreference) {
        this.genderPreference = genderPreference;
    }

    public boolean isFoodIncluded() {
        return foodIncluded;
    }

    public void setFoodIncluded(boolean foodIncluded) {
        this.foodIncluded = foodIncluded;
    }

    public boolean isPetFriendly() {
        return petFriendly;
    }

    public void setPetFriendly(boolean petFriendly) {
        this.petFriendly = petFriendly;
    }

    public boolean isGatedSociety() {
        return gatedSociety;
    }

    public void setGatedSociety(boolean gatedSociety) {
        this.gatedSociety = gatedSociety;
    }

    public Integer getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(Integer floorNumber) {
        this.floorNumber = floorNumber;
    }

    public Integer getTotalFloors() {
        return totalFloors;
    }

    public void setTotalFloors(Integer totalFloors) {
        this.totalFloors = totalFloors;
    }

    public LocalDate getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(LocalDate availableFrom) {
        this.availableFrom = availableFrom;
    }
}
