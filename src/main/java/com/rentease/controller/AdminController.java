package com.rentease.controller;

import com.rentease.dto.common.ApiResponse;
import com.rentease.dto.dashboard.AdminDashboardDto;
import com.rentease.security.UserPrincipal;
import com.rentease.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/v1/admin", "/api/admin"})
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@Tag(name = "Admin Desk", description = "Operations and trust compliance monitoring")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get high-level platform statistics and compliance metrics")
    public ResponseEntity<ApiResponse<AdminDashboardDto>> getDashboardStats() {
        AdminDashboardDto stats = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @PostMapping("/verifications/{documentId}/review")
    @Operation(summary = "Approve or reject a submitted verification document")
    public ResponseEntity<ApiResponse<String>> reviewVerificationDocument(
        @PathVariable String documentId,
        @RequestParam boolean approved,
        @RequestParam(defaultValue = "Document verified by trust desk") String notes,
        @AuthenticationPrincipal UserPrincipal adminUser
    ) {
        adminService.reviewDocument(documentId, approved, notes, adminUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(approved ? "Document approved and verified" : "Document rejected", null));
    }
}
