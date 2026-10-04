package com.rentease.repository;

import com.rentease.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AmenityRepository extends JpaRepository<Amenity, String> {
    List<Amenity> findByNameIn(List<String> names);
}
