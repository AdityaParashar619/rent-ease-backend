package com.rentease.service;

import com.rentease.dto.common.PageResponse;
import com.rentease.dto.listing.CreateListingRequest;
import com.rentease.dto.listing.ListingSummaryDto;
import com.rentease.entity.*;
import com.rentease.enums.ListingStatus;
import com.rentease.enums.OwnerType;
import com.rentease.exception.ResourceNotFoundException;
import com.rentease.exception.BadRequestException;
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
    private final SubCategoryRepository subCategoryRepository;
    private final AmenityRepository amenityRepository;

    public ListingService(ListingRepository listingRepository, UserRepository userRepository,
                          CategoryRepository categoryRepository, CityRepository cityRepository,
                          LocalityRepository localityRepository, SubCategoryRepository subCategoryRepository,
                          AmenityRepository amenityRepository) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.cityRepository = cityRepository;
        this.localityRepository = localityRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.amenityRepository = amenityRepository;
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
        String safeSort = List.of("createdAt", "price", "rating", "title").contains(sortBy)
            ? sortBy : "createdAt";
        Sort sort = Sort.by("asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC, safeSort);

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), sort);

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
    public ListingSummaryDto getListingById(String id, String viewerId) {
        Listing listing = listingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Listing", "id", id));
        if (listing.getStatus() != ListingStatus.ACTIVE && (viewerId == null || !listing.getProvider().getId().equals(viewerId))) {
            throw new ResourceNotFoundException("Listing", "id", id);
        }
        return mapToSummaryDto(listing);
    }

    @Transactional(readOnly = true)
    public List<ListingSummaryDto> getFeaturedListings() {
        return listingRepository.findFeaturedActiveListings().stream()
            .map(this::mapToSummaryDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PageResponse<ListingSummaryDto> getProviderListings(String providerId, int page, int size) {
        Page<Listing> listingPage = listingRepository.findByProviderId(providerId,
            PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by(Sort.Direction.DESC, "createdAt")));
        return new PageResponse<>(listingPage.getContent().stream().map(this::mapToSummaryDto).toList(),
            listingPage.getNumber(), listingPage.getSize(), listingPage.getTotalElements(),
            listingPage.getTotalPages(), listingPage.isLast());
    }

    @Transactional(readOnly = true)
    public PageResponse<ListingSummaryDto> getListingsByStatus(ListingStatus status, int page, int size) {
        Page<Listing> listingPage = listingRepository.findByStatus(status,
            PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by(Sort.Direction.DESC, "createdAt")));
        return new PageResponse<>(listingPage.getContent().stream().map(this::mapToSummaryDto).toList(),
            listingPage.getNumber(), listingPage.getSize(), listingPage.getTotalElements(),
            listingPage.getTotalPages(), listingPage.isLast());
    }

    @Transactional
    public ListingSummaryDto updateAvailability(String listingId, String providerId, boolean available) {
        Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new ResourceNotFoundException("Listing", "id", listingId));
        if (!listing.getProvider().getId().equals(providerId)) {
            throw new org.springframework.security.access.AccessDeniedException("You cannot modify another provider's listing");
        }
        if (available && !listing.isVerified()) {
            throw new BadRequestException("A listing must be verified before it can be activated");
        }
        listing.setStatus(available ? ListingStatus.ACTIVE : ListingStatus.TEMPORARILY_UNAVAILABLE);
        return mapToSummaryDto(listingRepository.save(listing));
    }

    @Transactional
    public ListingSummaryDto createListing(CreateListingRequest request, String providerId) {
        if (request.getSecurityDeposit() != null && request.getSecurityDeposit().signum() < 0) {
            throw new BadRequestException("Security deposit cannot be negative");
        }
        if (request.getPricingUnit() == null || !List.of("/month", "/day", "/event", "/seat/mo", "/sqft/mo").contains(request.getPricingUnit())) {
            throw new BadRequestException("Unsupported pricing unit");
        }
        if (request.getOwnerType() == null || !List.of("DIRECT_OWNER", "OWNER", "BROKER").contains(request.getOwnerType().toUpperCase(java.util.Locale.ROOT))) {
            throw new BadRequestException("Unsupported owner type");
        }
        if (request.getImages() != null) {
            if (request.getImages().size() > 8 || request.getImages().stream().anyMatch(java.util.Objects::isNull)
                || request.getImages().stream().mapToLong(String::length).sum() > 16_000_000L) {
                throw new BadRequestException("Upload no more than 8 images with a combined size under 12 MB");
            }
            for (String image : request.getImages()) {
                boolean dataImage = image != null && image.matches("(?is)^data:image/(png|jpeg|webp);base64,[A-Za-z0-9+/]+={0,2}$");
                boolean remoteImage = false;
                try {
                    java.net.URI uri = java.net.URI.create(image);
                    remoteImage = ("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme()))
                        && uri.getHost() != null;
                } catch (RuntimeException ignored) {
                    // Invalid URLs are rejected below.
                }
                if (!dataImage && !remoteImage) throw new BadRequestException("Images must be valid HTTP(S) URLs or PNG, JPEG, or WebP uploads");
            }
        }
        User provider = userRepository.findById(providerId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", providerId));

        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        SubCategory subCategory = subCategoryRepository.findById(request.getSubCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("SubCategory", "id", request.getSubCategoryId()));
        if (!subCategory.getCategory().getId().equals(category.getId())) {
            throw new BadRequestException("Subcategory does not belong to the selected category");
        }

        City city = cityRepository.findById(request.getCityId())
            .orElseThrow(() -> new ResourceNotFoundException("City", "id", request.getCityId()));

        Locality locality = localityRepository.findById(request.getLocalityId())
            .orElseThrow(() -> new ResourceNotFoundException("Locality", "id", request.getLocalityId()));
        if (!locality.getCity().getId().equals(city.getId())) {
            throw new BadRequestException("Locality does not belong to the selected city");
        }

        Listing listing = new Listing();
        listing.setId("lst_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        listing.setProvider(provider);
        listing.setCategory(category);
        listing.setSubCategory(subCategory);
        listing.setTitle(request.getTitle());
        listing.setDescription(request.getDescription());
        listing.setPrice(request.getPrice());
        listing.setPricingUnit(request.getPricingUnit());
        listing.setSecurityDeposit(request.getSecurityDeposit() != null ? request.getSecurityDeposit() : request.getPrice());
        listing.setServiceFee(request.getPrice().multiply(BigDecimal.valueOf(0.045)));
        listing.setOwnerType("BROKER".equalsIgnoreCase(request.getOwnerType()) ? OwnerType.BROKER : OwnerType.DIRECT_OWNER);
        listing.setStatus(ListingStatus.PENDING_VERIFICATION);
        listing.setVerified(false);
        listing.setFeatured(false);
        listing.setCity(city);
        listing.setLocality(locality);
        listing.setMaskedAddress(request.getMaskedAddress());
        listing.setFullAddress(request.getFullAddress());
        listing.setAddressPincode(request.getPincode());
        listing.setLatitude(locality.getLatitude());
        listing.setLongitude(locality.getLongitude());

        // Category details
        if ("RESIDENTIAL".equalsIgnoreCase(category.getName())) {
            if (request.getBedrooms() == null || request.getBedrooms() < 1 || request.getBathrooms() == null || request.getBathrooms() < 1
                || request.getCarpetAreaSqFt() == null || request.getCarpetAreaSqFt() < 1) {
                throw new BadRequestException("Bedrooms, bathrooms, and carpet area are required for residential listings");
            }
            PropertyDetails prop = new PropertyDetails();
            prop.setListing(listing);
            prop.setBedrooms(request.getBedrooms());
            prop.setBathrooms(request.getBathrooms());
            prop.setCarpetAreaSqFt(request.getCarpetAreaSqFt());
            prop.setFurnishingType(request.getFurnishingType());
            prop.setMaintenanceMonthly(request.getMaintenanceMonthly() != null ? request.getMaintenanceMonthly() : BigDecimal.ZERO);
            listing.setPropertyDetails(prop);
        } else if ("VEHICLE".equalsIgnoreCase(category.getName())) {
            if (request.getBrand() == null || request.getBrand().isBlank() || request.getModel() == null || request.getModel().isBlank()
                || request.getYear() == null || request.getYear() < 1886 || request.getTransmission() == null || request.getFuelType() == null) {
                throw new BadRequestException("Brand, model, year, transmission, and fuel type are required for vehicle listings");
            }
            VehicleDetails vehicle = new VehicleDetails();
            vehicle.setListing(listing);
            vehicle.setBrand(request.getBrand());
            vehicle.setModel(request.getModel());
            vehicle.setYear(request.getYear());
            vehicle.setTransmission(request.getTransmission());
            vehicle.setFuelType(request.getFuelType());
            vehicle.setSeatingCapacity(request.getSeatingCapacity() == null ? 5 : request.getSeatingCapacity());
            listing.setVehicleDetails(vehicle);
        } else if ("COMMERCIAL".equalsIgnoreCase(category.getName())) {
            if (request.getCarpetAreaSqFt() == null || request.getCarpetAreaSqFt() < 1) {
                throw new BadRequestException("Carpet area is required for commercial listings");
            }
            CommercialDetails commercial = new CommercialDetails();
            commercial.setListing(listing);
            commercial.setCarpetAreaSqFt(request.getCarpetAreaSqFt());
            listing.setCommercialDetails(commercial);
        } else if ("EVENT".equalsIgnoreCase(category.getName())) {
            if (request.getGuestCapacity() == null || request.getGuestCapacity() < 1) {
                throw new BadRequestException("Guest capacity is required for event venue listings");
            }
            VenueDetails venue = new VenueDetails();
            venue.setListing(listing);
            venue.setGuestCapacity(request.getGuestCapacity());
            venue.setIndoorOutdoor(request.getIndoorOutdoor());
            listing.setVenueDetails(venue);
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

        if (request.getAmenityNames() != null && !request.getAmenityNames().isEmpty()) {
            if (request.getAmenityNames().stream().anyMatch(java.util.Objects::isNull)) {
                throw new BadRequestException("Amenity names must be valid");
            }
            List<Amenity> selectedAmenities = amenityRepository.findByNameIn(request.getAmenityNames());
            if (selectedAmenities.size() != request.getAmenityNames().stream().distinct().count()) {
                throw new BadRequestException("One or more selected amenities are not supported");
            }
            listing.setAmenities(new java.util.HashSet<>(selectedAmenities));
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

        dto.setDescription(listing.getDescription());
        dto.setListerId(listing.getProvider().getId());
        dto.setListerName(listing.getProvider().getFirstName() + " " + listing.getProvider().getLastName());
        dto.setStatus(listing.getStatus().name());
        dto.setLatitude(listing.getLatitude());
        dto.setLongitude(listing.getLongitude());
        dto.setPincode(listing.getAddressPincode() != null && !listing.getAddressPincode().isBlank()
            ? listing.getAddressPincode() : listing.getLocality() == null ? "" : listing.getLocality().getPincode());
        dto.setCreatedAt(listing.getCreatedAt());
        dto.setAmenities(listing.getAmenities().stream().map(Amenity::getName).sorted().toList());
        List<String> imageUrls = listing.getImages() == null ? List.of() : listing.getImages().stream()
            .sorted(java.util.Comparator.comparing(ListingImage::getDisplayOrder, java.util.Comparator.nullsLast(Integer::compareTo)))
            .map(ListingImage::getImageUrl).toList();
        dto.setImages(imageUrls);
        dto.setCoverImage(imageUrls.isEmpty() ? null : imageUrls.get(0));

        List<String> keyFeatures = new ArrayList<>();
        if (listing.getPropertyDetails() != null) {
            dto.setBedrooms(listing.getPropertyDetails().getBedrooms());
            dto.setBathrooms(listing.getPropertyDetails().getBathrooms());
            dto.setCarpetAreaSqFt(listing.getPropertyDetails().getCarpetAreaSqFt());
            dto.setFurnishingType(listing.getPropertyDetails().getFurnishingType());
            keyFeatures.add(listing.getPropertyDetails().getBedrooms() + " BHK");
            keyFeatures.add(listing.getPropertyDetails().getCarpetAreaSqFt() + " sq.ft");
            keyFeatures.add(listing.getPropertyDetails().getFurnishingType());
        } else if (listing.getVehicleDetails() != null) {
            dto.setBrand(listing.getVehicleDetails().getBrand());
            dto.setModel(listing.getVehicleDetails().getModel());
            dto.setYear(listing.getVehicleDetails().getYear());
            dto.setTransmission(listing.getVehicleDetails().getTransmission());
            dto.setFuelType(listing.getVehicleDetails().getFuelType());
            dto.setSeatingCapacity(listing.getVehicleDetails().getSeatingCapacity());
            keyFeatures.add(listing.getVehicleDetails().getBrand() + " " + listing.getVehicleDetails().getModel());
            keyFeatures.add(listing.getVehicleDetails().getTransmission());
            keyFeatures.add(listing.getVehicleDetails().getFuelType());
        } else if (listing.getVenueDetails() != null) {
            dto.setGuestCapacity(listing.getVenueDetails().getGuestCapacity());
            keyFeatures.add("Capacity: " + listing.getVenueDetails().getGuestCapacity());
            keyFeatures.add(listing.getVenueDetails().getIndoorOutdoor());
        } else if (listing.getCommercialDetails() != null) {
            dto.setCarpetAreaSqFt(listing.getCommercialDetails().getCarpetAreaSqFt());
            keyFeatures.add(listing.getCommercialDetails().getCarpetAreaSqFt() + " sq.ft");
        }
        dto.setKeyFeatures(keyFeatures);

        return dto;
    }
}
