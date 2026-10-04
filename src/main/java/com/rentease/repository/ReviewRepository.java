package com.rentease.repository;

import com.rentease.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    Page<Review> findByListingId(String listingId, Pageable pageable);
    Optional<Review> findByBookingId(String bookingId);
}
