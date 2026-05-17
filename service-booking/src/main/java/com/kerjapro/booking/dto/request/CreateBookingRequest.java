package com.kerjapro.booking.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class CreateBookingRequest {

    @NotNull(message = "Subcontractor profile ID is required")
    private UUID subProfileId;

    private UUID workPackageId;

    @NotNull(message = "Appointment date/time is required")
    @Future(message = "Appointment must be in the future")
    private Instant appointmentAt;

    @Min(30) @Max(480)
    private int durationMinutes = 60;

    @Size(max = 255)
    private String location;

    @Size(max = 1000)
    private String notes;
}
