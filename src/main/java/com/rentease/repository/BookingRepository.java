package com.rentease.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rentease.entity.Booking;
import com.rentease.enums.BookingStatus;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    Optional<Booking> findByBookingReference(String bookingReference);

    Page<Booking> findByCustomerId(String customerId, Pageable pageable);

    Page<Booking> findByListingProviderId(String providerId, Pageable pageable);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
           "WHERE b.listing.id = :listingId " +
           "AND b.status IN ('CONFIRMED', 'ACTIVE', 'REQUESTED') " +
           "AND (:startDate <= b.endDate AND :endDate >= b.startDate)")
    boolean existsOverlappingBooking(
        @Param("listingId") String listingId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = :status")
    long countByStatus(@Param("status") BookingStatus status);
}
