package com.kerjapro.contractor.entity;

import com.kerjapro.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "portfolio_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private SubcontractorProfile profile;

    @Column(name = "project_title", nullable = false)
    private String projectTitle;

    @Column(name = "project_description", length = 2000)
    private String projectDescription;

    @Column(name = "location")
    private String location;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    @ElementCollection
    @CollectionTable(name = "portfolio_photos", joinColumns = @JoinColumn(name = "portfolio_item_id"))
    @Column(name = "photo_url")
    private List<String> photoUrls;

    @Column(name = "brand_used")
    private String brandUsed;
}
