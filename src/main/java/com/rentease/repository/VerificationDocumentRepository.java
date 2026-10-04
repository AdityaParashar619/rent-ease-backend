package com.rentease.repository;

import com.rentease.entity.VerificationDocument;
import com.rentease.enums.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VerificationDocumentRepository extends JpaRepository<VerificationDocument, String> {
    List<VerificationDocument> findByUserId(String userId);
    List<VerificationDocument> findByListingId(String listingId);
    Page<VerificationDocument> findByStatus(VerificationStatus status, Pageable pageable);
    long countByStatus(VerificationStatus status);
}
