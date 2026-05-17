package com.kerjapro.review.entity;

import com.kerjapro.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
public class Review extends BaseEntity {

    @Column(name = "booking_id", nullable = false, unique = true)
    private UUID bookingId;

    @Column(name = "reviewer_id", nullable = false)
    private String reviewerId;           // Keycloak subject

    @Column(name = "sub_profile_id", nullable = false)
    private UUID subProfileId;

    @Column(name = "overall_rating", nullable = false, precision = 2, scale = 1)
    private BigDecimal overallRating;

    @Column(name = "workmanship", precision = 2, scale = 1)
    private BigDecimal workmanship;

    @Column(name = "punctuality", precision = 2, scale = 1)
    private BigDecimal punctuality;

    @Column(name = "communication", precision = 2, scale = 1)
    private BigDecimal communication;

    @Column(name = "brand_knowledge", precision = 2, scale = 1)
    private BigDecimal brandKnowledge;

    @Column(name = "comment", length = 1000)
    private String comment;
}
