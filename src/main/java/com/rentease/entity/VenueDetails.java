package com.rentease.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "venue_details")
public class VenueDetails {

    @Id
    @Column(name = "listing_id", length = 64)
    private String listingId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "listing_id")
    private Listing listing;

    @Column(name = "guest_capacity", nullable = false)
    private Integer guestCapacity;

    @Column(name = "indoor_outdoor", length = 50)
    private String indoorOutdoor; // INDOOR, OUTDOOR, BOTH

    @Column(name = "parking_capacity")
    private Integer parkingCapacity;

    @Column(name = "rooms_available")
    private Integer roomsAvailable = 4;

    @Column(name = "catering_policy", length = 100)
    private String cateringPolicy;

    @Column(name = "music_curfew", length = 50)
    private String musicCurfew = "11:00 PM";

    @Column(name = "has_lawn")
    private boolean hasLawn = true;

    public VenueDetails() {
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

    public Integer getParkingCapacity() {
        return parkingCapacity;
    }

    public void setParkingCapacity(Integer parkingCapacity) {
        this.parkingCapacity = parkingCapacity;
    }

    public Integer getRoomsAvailable() {
        return roomsAvailable;
    }

    public void setRoomsAvailable(Integer roomsAvailable) {
        this.roomsAvailable = roomsAvailable;
    }

    public String getCateringPolicy() {
        return cateringPolicy;
    }

    public void setCateringPolicy(String cateringPolicy) {
        this.cateringPolicy = cateringPolicy;
    }

    public String getMusicCurfew() {
        return musicCurfew;
    }

    public void setMusicCurfew(String musicCurfew) {
        this.musicCurfew = musicCurfew;
    }

    public boolean isHasLawn() {
        return hasLawn;
    }

    public void setHasLawn(boolean hasLawn) {
        this.hasLawn = hasLawn;
    }
}
