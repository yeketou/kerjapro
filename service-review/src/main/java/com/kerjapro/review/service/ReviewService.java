package com.kerjapro.review.service;

import com.kerjapro.common.exception.BusinessException;
import com.kerjapro.common.exception.ResourceNotFoundException;
import com.kerjapro.review.dto.request.CreateReviewRequest;
import com.kerjapro.review.dto.response.RatingSummaryDto;
import com.kerjapro.review.dto.response.ReviewDto;
import com.kerjapro.review.entity.Review;
import com.kerjapro.review.mapper.ReviewMapper;
import com.kerjapro.review.repository.ReviewRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepo;
    private final ReviewMapper     mapper;
    private final EntityManager    em;

    // ─────────────────────────────────────────────
    // Create review
    // ─────────────────────────────────────────────

    @Transactional
    public ReviewDto createReview(String reviewerId, CreateReviewRequest request) {

        // One review per booking
        if (reviewRepo.existsByBookingIdAndDeletedAtIsNull(request.getBookingId())) {
            throw new BusinessException(
                "A review already exists for this booking",
                HttpStatus.CONFLICT, "REVIEW_EXISTS");
        }

        // Verify booking is COMPLETED and reviewer is the main contractor
        Object[] booking = findCompletedBooking(request.getBookingId());
        String   bookingContractorId = (String)  booking[0];
        UUID     subProfileId        = (UUID)    booking[1];

        if (!bookingContractorId.equals(reviewerId)) {
            throw new BusinessException(
                "Only the main contractor who made the booking can leave a review",
                HttpStatus.FORBIDDEN, "REVIEW_NOT_ALLOWED");
        }

        Review review = mapper.toEntity(request);
        review.setReviewerId(reviewerId);
        review.setSubProfileId(subProfileId);
        Review saved = reviewRepo.save(review);

        // Update aggregate rating on subcontractor profile (same DB)
        updateAggregateRating(subProfileId);

        log.info("Review created: id={}, sub={}, rating={}", saved.getId(),
                subProfileId, request.getOverallRating());
        return mapper.toDto(saved);
    }

    // ─────────────────────────────────────────────
    // Read
    // ─────────────────────────────────────────────

    @Transactional(readOnly = true)
    public ReviewDto getReview(UUID reviewId) {
        return mapper.toDto(
            reviewRepo.findById(reviewId)
                .filter(r -> r.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Review", reviewId)));
    }

    @Transactional(readOnly = true)
    public Page<ReviewDto> getReviewsForSub(UUID subProfileId, Pageable pageable) {
        return reviewRepo
            .findBySubProfileIdAndDeletedAtIsNullOrderByCreatedAtDesc(subProfileId, pageable)
            .map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ReviewDto> getMyReviews(String reviewerId, Pageable pageable) {
        return reviewRepo
            .findByReviewerIdAndDeletedAtIsNullOrderByCreatedAtDesc(reviewerId, pageable)
            .map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public RatingSummaryDto getRatingSummary(UUID subProfileId) {
        var results = em.createQuery("""
            SELECT
                AVG(r.overallRating),
                AVG(r.workmanship),
                AVG(r.punctuality),
                AVG(r.communication),
                AVG(r.brandKnowledge),
                COUNT(r)
            FROM Review r
            WHERE r.subProfileId = :subId
            AND r.deletedAt IS NULL
            """, Object[].class)
            .setParameter("subId", subProfileId)
            .getSingleResult();

        return new RatingSummaryDto(
            subProfileId,
            scale2((Double) results[0]),
            scale2((Double) results[1]),
            scale2((Double) results[2]),
            scale2((Double) results[3]),
            scale2((Double) results[4]),
            ((Long) results[5]).intValue()
        );
    }

    // ─────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────

    private Object[] findCompletedBooking(UUID bookingId) {
        try {
            return (Object[]) em.createNativeQuery("""
                SELECT main_contractor_id, sub_profile_id
                FROM public.bookings
                WHERE id = :bookingId AND status = 'COMPLETED'
                """)
                .setParameter("bookingId", bookingId)
                .getSingleResult();
        } catch (Exception e) {
            throw new BusinessException(
                "Booking not found or not yet completed. Reviews can only be left after a completed appointment.",
                HttpStatus.BAD_REQUEST, "BOOKING_NOT_COMPLETED");
        }
    }

    private void updateAggregateRating(UUID subProfileId) {
        BigDecimal avg = reviewRepo.computeAverageRating(subProfileId)
                .map(v -> v.setScale(2, RoundingMode.HALF_UP))
                .orElse(BigDecimal.ZERO);
        int count = reviewRepo.countBySubProfileId(subProfileId);

        em.createNativeQuery("""
            UPDATE public.subcontractor_profiles
            SET average_rating = :avg, total_reviews = :count
            WHERE id = :subId
            """)
            .setParameter("avg",   avg)
            .setParameter("count", count)
            .setParameter("subId", subProfileId)
            .executeUpdate();

        log.debug("Updated rating for sub={}: avg={}, count={}", subProfileId, avg, count);
    }

    private BigDecimal scale2(Double value) {
        if (value == null) return null;
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
}
