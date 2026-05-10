package com.kerjapro.contractor.controller;

import com.kerjapro.contractor.dto.request.*;
import com.kerjapro.contractor.dto.response.*;
import com.kerjapro.contractor.service.SubcontractorProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/contractors/profiles")
@RequiredArgsConstructor
@Tag(name = "Subcontractor Profiles", description = "Manage subcontractor profiles")
public class SubcontractorProfileController {

    private final SubcontractorProfileService service;

    @PostMapping
    @Operation(summary = "Create subcontractor profile (called on first onboarding)")
    public ResponseEntity<SubcontractorProfileDto> create(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateProfileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createProfile(userId, request));
    }

    @GetMapping("/me")
    @Operation(summary = "Get my own profile")
    public ResponseEntity<SubcontractorProfileDto> getMyProfile(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(service.getMyProfile(userId));
    }

    @GetMapping("/{profileId}")
    @Operation(summary = "Get subcontractor profile by ID (public)")
    public ResponseEntity<SubcontractorProfileDto> getById(@PathVariable UUID profileId) {
        return ResponseEntity.ok(service.getProfileById(profileId));
    }

    @PatchMapping("/me")
    @Operation(summary = "Update my profile")
    public ResponseEntity<SubcontractorProfileDto> update(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(service.updateProfile(userId, request));
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Soft-delete my profile")
    public void delete(@RequestHeader("X-User-Id") String userId) {
        service.deleteProfile(userId);
    }

    // ── Trades ──────────────────────────────────

    @PostMapping("/me/trades")
    @Operation(summary = "Add a trade specialisation")
    public ResponseEntity<TradeSpecializationDto> addTrade(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateTradeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addTrade(userId, request));
    }

    @DeleteMapping("/me/trades/{tradeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a trade specialisation")
    public void removeTrade(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID tradeId) {
        service.removeTrade(userId, tradeId);
    }

    // ── Brand Certifications ─────────────────────

    @PostMapping("/me/certifications")
    @Operation(summary = "Add a brand certification")
    public ResponseEntity<BrandCertificationDto> addCertification(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateCertificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addCertification(userId, request));
    }

    @DeleteMapping("/me/certifications/{certId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a brand certification")
    public void removeCertification(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID certId) {
        service.removeCertification(userId, certId);
    }

    // ── Portfolio ────────────────────────────────

    @PostMapping("/me/portfolio")
    @Operation(summary = "Add a portfolio item")
    public ResponseEntity<PortfolioItemDto> addPortfolioItem(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreatePortfolioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addPortfolioItem(userId, request));
    }

    @DeleteMapping("/me/portfolio/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a portfolio item")
    public void removePortfolioItem(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID itemId) {
        service.removePortfolioItem(userId, itemId);
    }
}
