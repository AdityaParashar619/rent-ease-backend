package com.rentease.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "vehicle_details")
public class VehicleDetails {

    @Id
    @Column(name = "listing_id", length = 64)
    private String listingId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "listing_id")
    private Listing listing;

    @Column(nullable = false, length = 100)
    private String brand;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false, length = 30)
    private String transmission; // AUTOMATIC, MANUAL

    @Column(name = "fuel_type", nullable = false, length = 30)
    private String fuelType; // PETROL, DIESEL, ELECTRIC, HYBRID

    @Column(name = "seating_capacity", nullable = false)
    private Integer seatingCapacity;

    @Column(name = "registration_number_masked", length = 50)
    private String registrationNumberMasked;

    @Column(name = "daily_km_limit")
    private Integer dailyKmLimit = 300;

    @Column(name = "extra_km_charge", precision = 8, scale = 2)
    private BigDecimal extraKmCharge = BigDecimal.valueOf(12.0);

    public VehicleDetails() {
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

    public Integer getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(Integer seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    public String getRegistrationNumberMasked() {
        return registrationNumberMasked;
    }

    public void setRegistrationNumberMasked(String registrationNumberMasked) {
        this.registrationNumberMasked = registrationNumberMasked;
    }

    public Integer getDailyKmLimit() {
        return dailyKmLimit;
    }

    public void setDailyKmLimit(Integer dailyKmLimit) {
        this.dailyKmLimit = dailyKmLimit;
    }

    public BigDecimal getExtraKmCharge() {
        return extraKmCharge;
    }

    public void setExtraKmCharge(BigDecimal extraKmCharge) {
        this.extraKmCharge = extraKmCharge;
    }
}
