package com.kerjapro.review.controller;

import com.kerjapro.review.dto.request.CreateReviewRequest;
import com.kerjapro.review.dto.response.RatingSummaryDto;
import com.kerjapro.review.dto.response.ReviewDto;
import com.kerjapro.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Ratings and reviews — only after completed bookings")
public class ReviewController {

    private final ReviewService service;

    @PostMapping
    @Operation(summary = "Submit a review (main contractor only, booking must be COMPLETED)")
    public ResponseEntity<ReviewDto> create(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createReview(userId, request));
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "Get a review by ID")
    public ResponseEntity<ReviewDto> get(@PathVariable UUID reviewId) {
        return ResponseEntity.ok(service.getReview(reviewId));
    }

    @GetMapping("/subcontractor/{subProfileId}")
    @Operation(summary = "Get all reviews for a subcontractor (public)")
    public ResponseEntity<Page<ReviewDto>> getForSub(
            @PathVariable UUID subProfileId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                service.getReviewsForSub(subProfileId, PageRequest.of(page, size)));
    }

    @GetMapping("/subcontractor/{subProfileId}/summary")
    @Operation(summary = "Get rating summary for a subcontractor (public)")
    public ResponseEntity<RatingSummaryDto> getSummary(@PathVariable UUID subProfileId) {
        return ResponseEntity.ok(service.getRatingSummary(subProfileId));
    }

    @GetMapping("/my")
    @Operation(summary = "Reviews I have written")
    public ResponseEntity<Page<ReviewDto>> getMine(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                service.getMyReviews(userId, PageRequest.of(page, size)));
    }
}
