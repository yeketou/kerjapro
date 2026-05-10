package com.kerjapro.contractor.repository;

import com.kerjapro.contractor.entity.PortfolioItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, UUID> {

    List<PortfolioItem> findByProfileIdAndDeletedAtIsNullOrderByCompletedDateDesc(UUID profileId);

    Optional<PortfolioItem> findByIdAndProfileIdAndDeletedAtIsNull(UUID id, UUID profileId);

    int countByProfileIdAndDeletedAtIsNull(UUID profileId);
}
