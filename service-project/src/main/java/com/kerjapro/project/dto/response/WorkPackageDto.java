package com.kerjapro.project.dto.response;

import com.kerjapro.project.entity.WorkPackage.PackageStatus;
import com.kerjapro.project.entity.WorkPackage.TradeCategory;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class WorkPackageDto {
    private UUID id;
    private UUID projectId;
    private String title;
    private TradeCategory tradeCategory;
    private String description;
    private String requiredBrand;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private LocalDate startDate;
    private LocalDate endDate;
    private PackageStatus status;
    private UUID assignedSubId;
}
