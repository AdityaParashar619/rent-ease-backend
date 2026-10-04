package com.rentease.controller;

import com.rentease.dto.booking.BookingDto;
import com.rentease.dto.booking.CreateBookingRequest;
import com.rentease.dto.common.ApiResponse;
import com.rentease.dto.common.PageResponse;
import com.rentease.security.UserPrincipal;
import com.rentease.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/v1/bookings", "/api/bookings"})
@Tag(name = "Bookings", description = "Rental reservation and slot locking management")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @Operation(summary = "Create a new booking reservation with conflict validation")
    public ResponseEntity<ApiResponse<BookingDto>> createBooking(
        @Valid @RequestBody CreateBookingRequest request,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        BookingDto booking = bookingService.createBooking(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Booking request submitted", booking));
    }

    @GetMapping("/my")
    @Operation(summary = "List current user's active and past bookings")
    public ResponseEntity<ApiResponse<PageResponse<BookingDto>>> getMyBookings(
        @AuthenticationPrincipal UserPrincipal currentUser,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<BookingDto> bookings = bookingService.getCustomerBookings(currentUser.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.ok(bookings));
    }

    @GetMapping("/provider")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('PROVIDER', 'BROKER', 'ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<BookingDto>>> getProviderBookings(
        @AuthenticationPrincipal UserPrincipal currentUser,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "100") int size
    ) {
        return ResponseEntity.ok(ApiResponse.ok(bookingService.getProviderBookings(currentUser.getId(), page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get specific booking details by ID")
    public ResponseEntity<ApiResponse<BookingDto>> getBookingById(
        @PathVariable String id, @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        BookingDto booking = bookingService.getBookingById(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(booking));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel an existing booking")
    public ResponseEntity<ApiResponse<BookingDto>> cancelBooking(
        @PathVariable String id,
        @RequestParam(defaultValue = "Customer requested cancellation") String reason,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        BookingDto cancelled = bookingService.cancelBooking(id, reason, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok("Booking cancelled successfully", cancelled));
    }
}
