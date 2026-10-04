package com.rentease.repository;

import com.rentease.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SubCategoryRepository extends JpaRepository<SubCategory, String> {
    Optional<SubCategory> findByCodeIgnoreCase(String code);
}
