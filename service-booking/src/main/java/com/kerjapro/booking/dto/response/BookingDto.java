package com.kerjapro.booking.dto.response;

import com.kerjapro.booking.entity.Booking.BookingStatus;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class BookingDto {
    private UUID id;
    private UUID workPackageId;
    private String mainContractorId;
    private UUID subProfileId;
    private Instant appointmentAt;
    private int durationMinutes;
    private String location;
    private String notes;
    private BookingStatus status;
    private String cancelledReason;
    private Instant createdAt;
}
