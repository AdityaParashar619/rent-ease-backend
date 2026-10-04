package com.rentease.service;

import com.rentease.dto.booking.BookingDto;
import com.rentease.dto.booking.CreateBookingRequest;
import com.rentease.dto.common.PageResponse;
import com.rentease.entity.Booking;
import com.rentease.entity.Listing;
import com.rentease.entity.User;
import com.rentease.enums.BookingStatus;
import com.rentease.exception.BookingConflictException;
import com.rentease.exception.ResourceNotFoundException;
import com.rentease.repository.BookingRepository;
import com.rentease.repository.ListingRepository;
import com.rentease.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository, ListingRepository listingRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BookingDto createBooking(CreateBookingRequest request, String customerId) {
        Listing listing = listingRepository.findById(request.getListingId())
            .orElseThrow(() -> new ResourceNotFoundException("Listing", "id", request.getListingId()));

        User customer = userRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", customerId));

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be prior to start date");
        }

        // Concurrency Slot Protection: Overlapping Check
        boolean isSlotTaken = bookingRepository.existsOverlappingBooking(
            listing.getId(), request.getStartDate(), request.getEndDate()
        );

        if (isSlotTaken) {
            throw new BookingConflictException("The selected dates are no longer available for this rental listing.");
        }

        long days = Math.max(1, ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()));

        // Pricing Engine
        BigDecimal baseAmount = listing.getPrice();
        if ("/day".equalsIgnoreCase(listing.getPricingUnit())) {
            baseAmount = listing.getPrice().multiply(BigDecimal.valueOf(days));
        }

        BigDecimal serviceFee = baseAmount.multiply(BigDecimal.valueOf(0.045)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxAmount = serviceFee.multiply(BigDecimal.valueOf(0.18)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal securityDeposit = listing.getSecurityDeposit() != null ? listing.getSecurityDeposit() : BigDecimal.ZERO;
        BigDecimal totalAmount = baseAmount.add(serviceFee).add(taxAmount).add(securityDeposit);

        Booking booking = new Booking();
        booking.setId("bkg_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        booking.setBookingReference("RE-" + System.currentTimeMillis() % 1000000);
        booking.setCustomer(customer);
        booking.setListing(listing);
        booking.setStartDate(request.getStartDate());
        booking.setEndDate(request.getEndDate());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBaseAmount(baseAmount);
        booking.setServiceFee(serviceFee);
        booking.setSecurityDeposit(securityDeposit);
        booking.setTaxAmount(taxAmount);
        booking.setTotalAmount(totalAmount);

        Booking saved = bookingRepository.save(booking);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<BookingDto> getCustomerBookings(String customerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Booking> bookingPage = bookingRepository.findByCustomerId(customerId, pageable);

        List<BookingDto> dtos = bookingPage.getContent().stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());

        return new PageResponse<>(
            dtos,
            bookingPage.getNumber(),
            bookingPage.getSize(),
            bookingPage.getTotalElements(),
            bookingPage.getTotalPages(),
            bookingPage.isLast()
        );
    }

    @Transactional(readOnly = true)
    public BookingDto getBookingById(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));
        return mapToDto(booking);
    }

    @Transactional
    public BookingDto cancelBooking(String bookingId, String reason, String userId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationReason(reason);
        Booking updated = bookingRepository.save(booking);
        return mapToDto(updated);
    }

    private BookingDto mapToDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setBookingReference(booking.getBookingReference());
        dto.setCustomerId(booking.getCustomer().getId());
        dto.setCustomerName(booking.getCustomer().getFullName());
        dto.setListingId(booking.getListing().getId());
        dto.setListingTitle(booking.getListing().getTitle());
        dto.setListingCategory(booking.getListing().getCategory() != null ? booking.getListing().getCategory().getName() : "");
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setStatus(booking.getStatus().name());
        dto.setBaseAmount(booking.getBaseAmount());
        dto.setServiceFee(booking.getServiceFee());
        dto.setSecurityDeposit(booking.getSecurityDeposit());
        dto.setTaxAmount(booking.getTaxAmount());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setCreatedAt(booking.getCreatedAt().toString());
        return dto;
    }
}
