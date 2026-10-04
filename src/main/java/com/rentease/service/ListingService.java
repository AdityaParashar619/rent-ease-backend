package com.rentease.service;

import com.rentease.dto.common.PageResponse;
import com.rentease.dto.listing.CreateListingRequest;
import com.rentease.dto.listing.ListingSummaryDto;
import com.rentease.entity.*;
import com.rentease.enums.ListingStatus;
import com.rentease.enums.OwnerType;
import com.rentease.exception.ResourceNotFoundException;
import com.rentease.repository.*;
import com.rentease.specification.ListingSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ListingService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CityRepository cityRepository;
    private final LocalityRepository localityRepository;

    public ListingService(ListingRepository listingRepository, UserRepository userRepository,
                          CategoryRepository categoryRepository, CityRepository cityRepository,
                          LocalityRepository localityRepository) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.cityRepository = cityRepository;
        this.localityRepository = localityRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<ListingSummaryDto> searchListings(
        String category,
        String subCategory,
        String cityId,
        String locality,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        OwnerType ownerType,
        Boolean verifiedOnly,
        String keyword,
        int page,
        int size,
        String sortBy,
        String sortDir
    ) {
        Sort sort = Sort.by("asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC,
            sortBy != null ? sortBy : "createdAt");

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Listing> spec = ListingSpecification.filterListings(
            category, subCategory, cityId, locality, minPrice, maxPrice, ownerType, verifiedOnly, keyword
        );

        Page<Listing> listingPage = listingRepository.findAll(spec, pageable);

        List<ListingSummaryDto> dtos = listingPage.getContent().stream()
            .map(this::mapToSummaryDto)
            .collect(Collectors.toList());

        return new PageResponse<>(
            dtos,
            listingPage.getNumber(),
            listingPage.getSize(),
            listingPage.getTotalElements(),
            listingPage.getTotalPages(),
            listingPage.isLast()
        );
    }

    @Transactional(readOnly = true)
    public ListingSummaryDto getListingById(String id) {
        Listing listing = listingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Listing", "id", id));
        return mapToSummaryDto(listing);
    }

    @Transactional(readOnly = true)
    public List<ListingSummaryDto> getFeaturedListings() {
        return listingRepository.findFeaturedActiveListings().stream()
            .map(this::mapToSummaryDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public ListingSummaryDto createListing(CreateListingRequest request, String providerId) {
        User provider = userRepository.findById(providerId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", providerId));

        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        City city = cityRepository.findById(request.getCityId())
            .orElseThrow(() -> new ResourceNotFoundException("City", "id", request.getCityId()));

        Locality locality = localityRepository.findById(request.getLocalityId())
            .orElseThrow(() -> new ResourceNotFoundException("Locality", "id", request.getLocalityId()));

        Listing listing = new Listing();
        listing.setId("lst_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        listing.setProvider(provider);
        listing.setCategory(category);
        listing.setTitle(request.getTitle());
        listing.setDescription(request.getDescription());
        listing.setPrice(request.getPrice());
        listing.setPricingUnit(request.getPricingUnit());
        listing.setSecurityDeposit(request.getSecurityDeposit() != null ? request.getSecurityDeposit() : request.getPrice());
        listing.setServiceFee(request.getPrice().multiply(BigDecimal.valueOf(0.045)));
        listing.setOwnerType("BROKER".equalsIgnoreCase(request.getOwnerType()) ? OwnerType.BROKER : OwnerType.DIRECT_OWNER);
        listing.setStatus(ListingStatus.ACTIVE);
        listing.setVerified(true);
        listing.setVerificationDate(Instant.now());
        listing.setCity(city);
        listing.setLocality(locality);
        listing.setMaskedAddress(request.getMaskedAddress());
        listing.setFullAddress(request.getFullAddress());

        // Category details
        if ("RESIDENTIAL".equalsIgnoreCase(category.getName())) {
            PropertyDetails prop = new PropertyDetails();
            prop.setListing(listing);
            prop.setBedrooms(request.getBedrooms() != null ? request.getBedrooms() : 2);
            prop.setBathrooms(request.getBathrooms() != null ? request.getBathrooms() : 2);
            prop.setCarpetAreaSqFt(request.getCarpetAreaSqFt() != null ? request.getCarpetAreaSqFt() : 1200);
            prop.setFurnishingType(request.getFurnishingType() != null ? request.getFurnishingType() : "SEMI_FURNISHED");
            prop.setMaintenanceMonthly(request.getMaintenanceMonthly() != null ? request.getMaintenanceMonthly() : BigDecimal.ZERO);
            listing.setPropertyDetails(prop);
        }

        // Add cover image
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<ListingImage> images = new ArrayList<>();
            for (int i = 0; i < request.getImages().size(); i++) {
                ListingImage img = new ListingImage();
                img.setId("img_" + UUID.randomUUID().toString().substring(0, 8));
                img.setListing(listing);
                img.setImageUrl(request.getImages().get(i));
                img.setDisplayOrder(i);
                img.setCover(i == 0);
                images.add(img);
            }
            listing.setImages(images);
        }

        Listing saved = listingRepository.save(listing);
        return mapToSummaryDto(saved);
    }

    public ListingSummaryDto mapToSummaryDto(Listing listing) {
        ListingSummaryDto dto = new ListingSummaryDto();
        dto.setId(listing.getId());
        dto.setTitle(listing.getTitle());
        dto.setCategory(listing.getCategory() != null ? listing.getCategory().getName() : "RESIDENTIAL");
        dto.setSubCategory(listing.getSubCategory() != null ? listing.getSubCategory().getCode() : "");
        dto.setPrice(listing.getPrice());
        dto.setPricingUnit(listing.getPricingUnit());
        dto.setSecurityDeposit(listing.getSecurityDeposit());
        dto.setOwnerType(listing.getOwnerType().name());
        dto.setVerified(listing.isVerified());
        dto.setLocality(listing.getLocality() != null ? listing.getLocality().getName() : "");
        dto.setCity(listing.getCity() != null ? listing.getCity().getName() : "");
        dto.setMaskedAddress(listing.getMaskedAddress());
        dto.setRating(listing.getRating());
        dto.setReviewCount(listing.getReviewCount());
        dto.setFeatured(listing.isFeatured());

        if (listing.getImages() != null && !listing.getImages().isEmpty()) {
            dto.setCoverImage(listing.getImages().get(0).getImageUrl());
        }

        List<String> keyFeatures = new ArrayList<>();
        if (listing.getPropertyDetails() != null) {
            keyFeatures.add(listing.getPropertyDetails().getBedrooms() + " BHK");
            keyFeatures.add(listing.getPropertyDetails().getCarpetAreaSqFt() + " sq.ft");
            keyFeatures.add(listing.getPropertyDetails().getFurnishingType());
        } else if (listing.getVehicleDetails() != null) {
            keyFeatures.add(listing.getVehicleDetails().getBrand() + " " + listing.getVehicleDetails().getModel());
            keyFeatures.add(listing.getVehicleDetails().getTransmission());
            keyFeatures.add(listing.getVehicleDetails().getFuelType());
        } else if (listing.getVenueDetails() != null) {
            keyFeatures.add("Capacity: " + listing.getVenueDetails().getGuestCapacity());
            keyFeatures.add(listing.getVenueDetails().getIndoorOutdoor());
        }
        dto.setKeyFeatures(keyFeatures);

        return dto;
    }
}
