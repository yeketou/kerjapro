package com.kerjapro.contractor.dto.response;

import com.kerjapro.contractor.entity.SubcontractorProfile.SubscriptionTier;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class SubcontractorProfileDto {
    private UUID id;
    private String userId;
    private String businessName;
    private String displayName;
    private String bio;
    private String phone;
    private String email;
    private String city;
    private String state;
    private String profilePhotoUrl;
    private String cidbGrade;
    private String cidbRegistrationNo;
    private SubscriptionTier subscriptionTier;
    private boolean verified;
    private boolean available;
    private BigDecimal averageRating;
    private Integer totalReviews;
    private Integer totalCompletedJobs;
    private List<TradeSpecializationDto> tradeSpecializations;
    private List<BrandCertificationDto> brandCertifications;
    private List<PortfolioItemDto> portfolio;
}
