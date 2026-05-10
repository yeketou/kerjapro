package com.kerjapro.contractor.repository;

import com.kerjapro.contractor.entity.TradeSpecialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TradeSpecializationRepository extends JpaRepository<TradeSpecialization, UUID> {

    List<TradeSpecialization> findByProfileIdAndDeletedAtIsNull(UUID profileId);

    @Query("""
        SELECT DISTINCT p FROM com.kerjapro.contractor.entity.SubcontractorProfile p
        JOIN p.tradeSpecializations ts
        WHERE ts.tradeCategory = :category
        AND ts.deletedAt IS NULL
        AND p.deletedAt IS NULL
        AND p.available = true
        """)
    List<Object> findProfilesByTradeCategory(@Param("category") TradeSpecialization.TradeCategory category);

    void deleteByProfileIdAndId(UUID profileId, UUID id);
}
