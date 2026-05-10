package com.kerjapro.contractor.repository;

import com.kerjapro.contractor.entity.BrandCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BrandCertificationRepository extends JpaRepository<BrandCertification, UUID> {

    List<BrandCertification> findByProfileIdAndDeletedAtIsNull(UUID profileId);

    Optional<BrandCertification> findByIdAndProfileIdAndDeletedAtIsNull(UUID id, UUID profileId);

    @Query("""
        SELECT DISTINCT p FROM com.kerjapro.contractor.entity.SubcontractorProfile p
        JOIN p.brandCertifications bc
        WHERE LOWER(bc.brandName) = LOWER(:brandName)
        AND bc.deletedAt IS NULL
        AND p.deletedAt IS NULL
        AND p.available = true
        """)
    List<Object> findProfilesByBrand(@Param("brandName") String brandName);

    boolean existsByProfileIdAndBrandNameIgnoreCaseAndDeletedAtIsNull(UUID profileId, String brandName);
}
