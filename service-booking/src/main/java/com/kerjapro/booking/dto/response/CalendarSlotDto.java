package com.kerjapro.booking.dto.response;

import com.kerjapro.booking.entity.Booking.BookingStatus;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class CalendarSlotDto {
    private UUID bookingId;
    private Instant appointmentAt;
    private Instant appointmentEnd;  // appointmentAt + durationMinutes
    private int durationMinutes;
    private BookingStatus status;
    private String location;
}
