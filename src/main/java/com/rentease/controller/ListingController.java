package com.rentease.controller;

import com.rentease.dto.common.ApiResponse;
import com.rentease.dto.common.PageResponse;
import com.rentease.dto.listing.CreateListingRequest;
import com.rentease.dto.listing.ListingSummaryDto;
import com.rentease.enums.OwnerType;
import com.rentease.security.UserPrincipal;
import com.rentease.service.ListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping({"/api/v1/listings", "/api/listings"})
@Tag(name = "Listings", description = "Multi-category rental inventory search and management")
public class ListingController {

    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping
    @Operation(summary = "Search and filter listings across residential, vehicle, commercial, and event categories")
    public ResponseEntity<ApiResponse<PageResponse<ListingSummaryDto>>> searchListings(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String subCategory,
        @RequestParam(required = false) String cityId,
        @RequestParam(required = false) String locality,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        @RequestParam(required = false) OwnerType ownerType,
        @RequestParam(required = false) Boolean verifiedOnly,
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "12") int size,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "desc") String sortDir
    ) {
        PageResponse<ListingSummaryDto> results = listingService.searchListings(
            category, subCategory, cityId, locality, minPrice, maxPrice, ownerType,
            verifiedOnly, keyword, page, size, sortBy, sortDir
        );
        return ResponseEntity.ok(ApiResponse.ok(results));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Fetch detailed specifications for a specific listing")
    public ResponseEntity<ApiResponse<ListingSummaryDto>> getListingById(
        @PathVariable String id, @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        ListingSummaryDto listing = listingService.getListingById(id, currentUser == null ? null : currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(listing));
    }

    @GetMapping("/featured")
    @Operation(summary = "Fetch top verified featured listings")
    public ResponseEntity<ApiResponse<List<ListingSummaryDto>>> getFeaturedListings() {
        List<ListingSummaryDto> featured = listingService.getFeaturedListings();
        return ResponseEntity.ok(ApiResponse.ok(featured));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('PROVIDER', 'BROKER', 'ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<ListingSummaryDto>>> getMyListings(
        @AuthenticationPrincipal UserPrincipal currentUser,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "100") int size
    ) {
        return ResponseEntity.ok(ApiResponse.ok(listingService.getProviderListings(currentUser.getId(), page, size)));
    }

    @PatchMapping("/my/{id}/availability")
    @PreAuthorize("hasAnyRole('PROVIDER', 'BROKER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ListingSummaryDto>> updateAvailability(
        @PathVariable String id,
        @RequestParam boolean available,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Listing availability updated",
            listingService.updateAvailability(id, currentUser.getId(), available)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PROVIDER', 'BROKER', 'ADMIN')")
    @Operation(summary = "Publish a new rental listing (Requires Provider or Broker role)")
    public ResponseEntity<ApiResponse<ListingSummaryDto>> createListing(
        @Valid @RequestBody CreateListingRequest request,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        ListingSummaryDto created = listingService.createListing(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Listing submitted for verification", created));
    }
}
