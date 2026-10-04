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
        return ResponseEntity.ok(ApiResponse.ok("Booking confirmed successfully", booking));
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

    @GetMapping("/{id}")
    @Operation(summary = "Get specific booking details by ID")
    public ResponseEntity<ApiResponse<BookingDto>> getBookingById(@PathVariable String id) {
        BookingDto booking = bookingService.getBookingById(id);
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
