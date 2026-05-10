package com.kerjapro.contractor.entity;

import com.kerjapro.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "subcontractor_profiles")
@Getter
@Setter
@NoArgsConstructor
public class SubcontractorProfile extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "bio", length = 1000)
    private String bio;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;

    @Column(name = "cidb_grade")
    private String cidbGrade;

    @Column(name = "cidb_registration_no")
    private String cidbRegistrationNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_tier", nullable = false)
    private SubscriptionTier subscriptionTier = SubscriptionTier.FREE;

    @Column(name = "is_verified", nullable = false)
    private boolean verified = false;

    @Column(name = "is_available", nullable = false)
    private boolean available = true;

    @Column(name = "average_rating")
    private Double averageRating;

    @Column(name = "total_reviews")
    private Integer totalReviews = 0;

    @Column(name = "total_completed_jobs")
    private Integer totalCompletedJobs = 0;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TradeSpecialization> tradeSpecializations;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BrandCertification> brandCertifications;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioItem> portfolio;

    public enum SubscriptionTier {
        FREE, PRO, PREMIUM
    }
}
