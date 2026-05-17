package com.kerjapro.review.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateReviewRequest {

    @NotNull(message = "Booking ID is required")
    private UUID bookingId;

    @NotNull(message = "Overall rating is required")
    @DecimalMin("1.0") @DecimalMax("5.0")
    private BigDecimal overallRating;

    @DecimalMin("1.0") @DecimalMax("5.0")
    private BigDecimal workmanship;

    @DecimalMin("1.0") @DecimalMax("5.0")
    private BigDecimal punctuality;

    @DecimalMin("1.0") @DecimalMax("5.0")
    private BigDecimal communication;

    @DecimalMin("1.0") @DecimalMax("5.0")
    private BigDecimal brandKnowledge;

    @Size(max = 1000)
    private String comment;
}
