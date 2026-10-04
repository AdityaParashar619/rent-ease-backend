package com.rentease.service;

import com.rentease.dto.dashboard.AdminDashboardDto;
import com.rentease.entity.Listing;
import com.rentease.entity.VerificationDocument;
import com.rentease.enums.BookingStatus;
import com.rentease.enums.ListingStatus;
import com.rentease.enums.VerificationStatus;
import com.rentease.exception.ResourceNotFoundException;
import com.rentease.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@Service
public class AdminService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final VerificationDocumentRepository verificationDocumentRepository;
    private final DisputeRepository disputeRepository;
    private final ListingService listingService;

    public AdminService(ListingRepository listingRepository, UserRepository userRepository,
                        BookingRepository bookingRepository, VerificationDocumentRepository verificationDocumentRepository,
                        DisputeRepository disputeRepository, ListingService listingService) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.verificationDocumentRepository = verificationDocumentRepository;
        this.disputeRepository = disputeRepository;
        this.listingService = listingService;
    }

    @Transactional(readOnly = true)
    public AdminDashboardDto getDashboardStats() {
        AdminDashboardDto dto = new AdminDashboardDto();
        dto.setTotalUsers(userRepository.count());
        dto.setTotalListings(listingRepository.count());
        dto.setActiveListings(listingRepository.countByStatus(ListingStatus.ACTIVE));
        dto.setPendingVerifications(verificationDocumentRepository.countByStatus(VerificationStatus.SUBMITTED)
            + listingRepository.countByStatus(ListingStatus.PENDING_VERIFICATION));
        dto.setTotalBookings(bookingRepository.count());
        dto.setActiveBookings(bookingRepository.countByStatus(BookingStatus.CONFIRMED));
        return dto;
    }

    @Transactional
    public void reviewDocument(String documentId, boolean approved, String notes, String adminUserId) {
        VerificationDocument doc = verificationDocumentRepository.findById(documentId)
            .orElseThrow(() -> new ResourceNotFoundException("VerificationDocument", "id", documentId));

        doc.setStatus(approved ? VerificationStatus.VERIFIED : VerificationStatus.REJECTED);
        doc.setReviewerNotes(notes);
        doc.setReviewedAt(Instant.now());
        doc.setReviewedBy(userRepository.findById(adminUserId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", adminUserId)));
        verificationDocumentRepository.save(doc);

        // A single document decision must not stand in for complete listing review.
        if (doc.getListing() != null) {
            Listing listing = listingRepository.findById(doc.getListing().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Listing", "id", doc.getListing().getId()));
            boolean allDocumentsVerified = verificationDocumentRepository.findByListingId(listing.getId()).stream()
                .allMatch(document -> document.getStatus() == VerificationStatus.VERIFIED);
            if (!approved) {
                listing.setVerified(false);
                listing.setVerificationDate(null);
                listing.setStatus(ListingStatus.REJECTED);
            } else if (allDocumentsVerified) {
                listing.setVerified(true);
                listing.setVerificationDate(Instant.now());
                listing.setStatus(ListingStatus.ACTIVE);
            }
            listingRepository.save(listing);
        }
    }

    @Transactional(readOnly = true)
    public com.rentease.dto.common.PageResponse<com.rentease.dto.listing.ListingSummaryDto> getPendingListings(int page, int size) {
        return listingService.getListingsByStatus(ListingStatus.PENDING_VERIFICATION, page, size);
    }

    @Transactional
    public void reviewListing(String listingId, boolean approved) {
        Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new ResourceNotFoundException("Listing", "id", listingId));
        listing.setVerified(approved);
        listing.setVerificationDate(approved ? Instant.now() : null);
        listing.setStatus(approved ? ListingStatus.ACTIVE : ListingStatus.REJECTED);
        listingRepository.save(listing);
    }
}
