package com.rentease.repository;

import com.rentease.entity.Listing;
import com.rentease.enums.ListingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, String>, JpaSpecificationExecutor<Listing> {

    Page<Listing> findByStatus(ListingStatus status, Pageable pageable);

    Page<Listing> findByProviderId(String providerId, Pageable pageable);

    @Query("SELECT l FROM Listing l WHERE l.featured = true AND l.status = 'ACTIVE'")
    List<Listing> findFeaturedActiveListings();

    @Query("SELECT COUNT(l) FROM Listing l WHERE l.status = :status")
    long countByStatus(@Param("status") ListingStatus status);
}
