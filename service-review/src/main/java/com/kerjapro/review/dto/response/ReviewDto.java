package com.kerjapro.review.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class ReviewDto {
    private UUID id;
    private UUID bookingId;
    private String reviewerId;
    private UUID subProfileId;
    private BigDecimal overallRating;
    private BigDecimal workmanship;
    private BigDecimal punctuality;
    private BigDecimal communication;
    private BigDecimal brandKnowledge;
    private String comment;
    private Instant createdAt;
}
