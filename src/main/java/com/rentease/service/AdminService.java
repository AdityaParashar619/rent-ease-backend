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

    public AdminService(ListingRepository listingRepository, UserRepository userRepository,
                        BookingRepository bookingRepository, VerificationDocumentRepository verificationDocumentRepository,
                        DisputeRepository disputeRepository) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.verificationDocumentRepository = verificationDocumentRepository;
        this.disputeRepository = disputeRepository;
    }

    @Transactional(readOnly = true)
    public AdminDashboardDto getDashboardStats() {
        AdminDashboardDto dto = new AdminDashboardDto();
        dto.setTotalUsers(userRepository.count());
        dto.setTotalListings(listingRepository.count());
        dto.setActiveListings(listingRepository.countByStatus(ListingStatus.ACTIVE));
        dto.setPendingVerifications(verificationDocumentRepository.countByStatus(VerificationStatus.SUBMITTED));
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
        verificationDocumentRepository.save(doc);

        // If document was tied to a listing, update listing verification badge
        if (doc.getListing() != null && approved) {
            Listing listing = doc.getListing();
            listing.setVerified(true);
            listing.setVerificationDate(Instant.now());
            listingRepository.save(listing);
        }
    }
}
