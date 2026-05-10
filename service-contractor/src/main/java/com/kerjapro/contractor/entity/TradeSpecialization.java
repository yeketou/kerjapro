package com.kerjapro.contractor.entity;

import com.kerjapro.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trade_specializations")
@Getter
@Setter
@NoArgsConstructor
public class TradeSpecialization extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private SubcontractorProfile profile;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_category", nullable = false)
    private TradeCategory tradeCategory;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    @Column(name = "description")
    private String description;

    public enum TradeCategory {
        PLUMBING, ELECTRICAL, TILING, CARPENTRY,
        PAINTING, ROOFING, HVAC, LANDSCAPING,
        FLOORING, WATERPROOFING, ALUMINUM_WORKS,
        GLASS_WORKS, RENOVATION, BATHROOM, KITCHEN,
        CIVIL_WORKS, OTHER
    }
}
