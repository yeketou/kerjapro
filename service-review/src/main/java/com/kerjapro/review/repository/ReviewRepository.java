package com.kerjapro.review.repository;

import com.kerjapro.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    boolean existsByBookingIdAndDeletedAtIsNull(UUID bookingId);

    Optional<Review> findByBookingIdAndDeletedAtIsNull(UUID bookingId);

    Page<Review> findBySubProfileIdAndDeletedAtIsNullOrderByCreatedAtDesc(
            UUID subProfileId, Pageable pageable);

    Page<Review> findByReviewerIdAndDeletedAtIsNullOrderByCreatedAtDesc(
            String reviewerId, Pageable pageable);

    // Compute new aggregate rating for a subcontractor
    @Query("""
        SELECT AVG(r.overallRating) FROM Review r
        WHERE r.subProfileId = :subId
        AND r.deletedAt IS NULL
        """)
    Optional<BigDecimal> computeAverageRating(@Param("subId") UUID subId);

    @Query("""
        SELECT COUNT(r) FROM Review r
        WHERE r.subProfileId = :subId
        AND r.deletedAt IS NULL
        """)
    int countBySubProfileId(@Param("subId") UUID subId);
}
