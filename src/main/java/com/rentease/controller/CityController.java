package com.rentease.controller;

import com.rentease.dto.common.ApiResponse;
import com.rentease.entity.City;
import com.rentease.repository.CityRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping({"/api/v1/cities", "/api/cities"})
@Tag(name = "Geography", description = "Operational cities and rental localities")
public class CityController {

    private final CityRepository cityRepository;

    public CityController(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @GetMapping
    @Operation(summary = "List all active launch cities")
    public ResponseEntity<ApiResponse<List<City>>> getActiveCities() {
        List<City> cities = cityRepository.findByActiveTrue();
        return ResponseEntity.ok(ApiResponse.ok(cities));
    }
}
