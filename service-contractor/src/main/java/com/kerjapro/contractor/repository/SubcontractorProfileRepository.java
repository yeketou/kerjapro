package com.kerjapro.contractor.repository;

import com.kerjapro.contractor.entity.SubcontractorProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SubcontractorProfileRepository
        extends JpaRepository<SubcontractorProfile, UUID>,
                JpaSpecificationExecutor<SubcontractorProfile> {

    Optional<SubcontractorProfile> findByUserIdAndDeletedAtIsNull(String userId);

    boolean existsByUserIdAndDeletedAtIsNull(String userId);

    @Query("""
        SELECT p FROM SubcontractorProfile p
        WHERE p.deletedAt IS NULL
        AND p.available = true
        AND (:city IS NULL OR LOWER(p.city) = LOWER(:city))
        AND (:state IS NULL OR LOWER(p.state) = LOWER(:state))
        AND (:minRating IS NULL OR p.averageRating >= :minRating)
        AND (:verified IS NULL OR p.verified = :verified)
        ORDER BY
            CASE WHEN p.subscriptionTier = 'PREMIUM' THEN 0
                 WHEN p.subscriptionTier = 'PRO'     THEN 1
                 ELSE 2 END ASC,
            p.averageRating DESC NULLS LAST
        """)
    Page<SubcontractorProfile> searchProfiles(
            @Param("city")      String city,
            @Param("state")     String state,
            @Param("minRating") Double minRating,
            @Param("verified")  Boolean verified,
            Pageable pageable);

    @Modifying
    @Query("UPDATE SubcontractorProfile p SET p.averageRating = :rating, p.totalReviews = :count WHERE p.id = :id")
    void updateRating(@Param("id") UUID id, @Param("rating") Double rating, @Param("count") int count);

    @Modifying
    @Query("UPDATE SubcontractorProfile p SET p.totalCompletedJobs = p.totalCompletedJobs + 1 WHERE p.id = :id")
    void incrementCompletedJobs(@Param("id") UUID id);
}
