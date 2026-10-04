package com.rentease.repository;

import com.rentease.entity.Locality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LocalityRepository extends JpaRepository<Locality, String> {
    List<Locality> findByCityId(String cityId);
}
