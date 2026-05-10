package com.kerjapro.project.repository;

import com.kerjapro.project.entity.WorkPackage;
import com.kerjapro.project.entity.WorkPackage.PackageStatus;
import com.kerjapro.project.entity.WorkPackage.TradeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkPackageRepository extends JpaRepository<WorkPackage, UUID> {

    List<WorkPackage> findByProjectIdAndDeletedAtIsNull(UUID projectId);

    List<WorkPackage> findByProjectIdAndStatusAndDeletedAtIsNull(UUID projectId, PackageStatus status);

    Optional<WorkPackage> findByIdAndProjectIdAndDeletedAtIsNull(UUID id, UUID projectId);

    // For matching subs to open packages across all tenant projects
    List<WorkPackage> findByTradeCategoryAndStatusAndDeletedAtIsNull(
            TradeCategory category, PackageStatus status);
}
