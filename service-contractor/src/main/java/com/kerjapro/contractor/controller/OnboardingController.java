package com.kerjapro.contractor.controller;

import com.kerjapro.contractor.dto.request.CreateProfileRequest;
import com.kerjapro.contractor.dto.response.SubcontractorProfileDto;
import com.kerjapro.contractor.service.OnboardingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contractors/onboarding")
@RequiredArgsConstructor
@Tag(name = "Onboarding", description = "First-time user setup")
public class OnboardingController {

    private final OnboardingService onboardingService;

    @PostMapping("/subcontractor")
    @Operation(summary = "Complete subcontractor profile setup")
    public ResponseEntity<SubcontractorProfileDto> onboardSubcontractor(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateProfileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(onboardingService.onboardSubcontractor(userId, request));
    }
}
