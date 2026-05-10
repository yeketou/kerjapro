package com.kerjapro.project.entity;

import com.kerjapro.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "work_packages")
@Getter
@Setter
@NoArgsConstructor
public class WorkPackage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_category", nullable = false)
    private TradeCategory tradeCategory;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "required_brand")
    private String requiredBrand;

    @Column(name = "budget_min", precision = 15, scale = 2)
    private BigDecimal budgetMin;

    @Column(name = "budget_max", precision = 15, scale = 2)
    private BigDecimal budgetMax;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PackageStatus status = PackageStatus.OPEN;

    @Column(name = "assigned_sub_id")
    private UUID assignedSubId; // references public.subcontractor_profiles(id)

    public enum PackageStatus {
        OPEN, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED
    }

    public enum TradeCategory {
        PLUMBING, ELECTRICAL, TILING, CARPENTRY,
        PAINTING, ROOFING, HVAC, LANDSCAPING,
        FLOORING, WATERPROOFING, ALUMINUM_WORKS,
        GLASS_WORKS, RENOVATION, BATHROOM, KITCHEN,
        CIVIL_WORKS, OTHER
    }
}
