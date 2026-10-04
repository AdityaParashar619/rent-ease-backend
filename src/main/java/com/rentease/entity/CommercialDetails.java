package com.rentease.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "commercial_details")
public class CommercialDetails {

    @Id
    @Column(name = "listing_id", length = 64)
    private String listingId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "listing_id")
    private Listing listing;

    @Column(name = "carpet_area_sq_ft", nullable = false)
    private Integer carpetAreaSqFt;

    @Column(name = "suitable_for", length = 100)
    private String suitableFor;

    @Column(name = "furnishing_status", length = 50)
    private String furnishingStatus;

    @Column(name = "power_backup")
    private boolean powerBackup = true;

    @Column(name = "reserved_parking_spots")
    private Integer reservedParkingSpots = 2;

    @Column(name = "conference_rooms")
    private Integer conferenceRooms = 1;

    public CommercialDetails() {
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

    public Integer getCarpetAreaSqFt() {
        return carpetAreaSqFt;
    }

    public void setCarpetAreaSqFt(Integer carpetAreaSqFt) {
        this.carpetAreaSqFt = carpetAreaSqFt;
    }

    public String getSuitableFor() {
        return suitableFor;
    }

    public void setSuitableFor(String suitableFor) {
        this.suitableFor = suitableFor;
    }

    public String getFurnishingStatus() {
        return furnishingStatus;
    }

    public void setFurnishingStatus(String furnishingStatus) {
        this.furnishingStatus = furnishingStatus;
    }

    public boolean isPowerBackup() {
        return powerBackup;
    }

    public void setPowerBackup(boolean powerBackup) {
        this.powerBackup = powerBackup;
    }

    public Integer getReservedParkingSpots() {
        return reservedParkingSpots;
    }

    public void setReservedParkingSpots(Integer reservedParkingSpots) {
        this.reservedParkingSpots = reservedParkingSpots;
    }

    public Integer getConferenceRooms() {
        return conferenceRooms;
    }

    public void setConferenceRooms(Integer conferenceRooms) {
        this.conferenceRooms = conferenceRooms;
    }
}
