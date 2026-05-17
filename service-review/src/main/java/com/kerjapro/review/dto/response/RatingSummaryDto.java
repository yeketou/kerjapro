package com.kerjapro.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class RatingSummaryDto {
    private UUID subProfileId;
    private BigDecimal averageRating;
    private BigDecimal averageWorkmanship;
    private BigDecimal averagePunctuality;
    private BigDecimal averageCommunication;
    private BigDecimal averageBrandKnowledge;
    private int totalReviews;
}
