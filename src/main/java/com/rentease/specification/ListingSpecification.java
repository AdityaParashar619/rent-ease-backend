package com.rentease.specification;

import com.rentease.entity.Listing;
import com.rentease.enums.ListingStatus;
import com.rentease.enums.OwnerType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ListingSpecification {

    public static Specification<Listing> filterListings(
        String categoryName,
        String subCategoryCode,
        String cityId,
        String localityName,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        OwnerType ownerType,
        Boolean verifiedOnly,
        String keyword
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always only active listings for public search
            predicates.add(cb.equal(root.get("status"), ListingStatus.ACTIVE));

            if (categoryName != null && !categoryName.trim().isEmpty()) {
                predicates.add(cb.equal(cb.upper(root.get("category").get("name")), categoryName.toUpperCase()));
            }

            if (subCategoryCode != null && !subCategoryCode.trim().isEmpty()) {
                predicates.add(cb.equal(cb.upper(root.get("subCategory").get("code")), subCategoryCode.toUpperCase()));
            }

            if (cityId != null && !cityId.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("city").get("id"), cityId));
            }

            if (localityName != null && !localityName.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("locality").get("name")), "%" + localityName.toLowerCase() + "%"));
            }

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            if (ownerType != null) {
                predicates.add(cb.equal(root.get("ownerType"), ownerType));
            }

            if (verifiedOnly != null && verifiedOnly) {
                predicates.add(cb.isTrue(root.get("verified")));
            }

            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.toLowerCase().trim() + "%";
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), pattern);
                Predicate descMatch = cb.like(cb.lower(root.get("description")), pattern);
                Predicate localityMatch = cb.like(cb.lower(root.get("locality").get("name")), pattern);
                predicates.add(cb.or(titleMatch, descMatch, localityMatch));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
