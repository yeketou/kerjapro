package com.kerjapro.booking.entity;

import com.kerjapro.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
public class Booking extends BaseEntity {

    @Column(name = "work_package_id")
    private UUID workPackageId;

    @Column(name = "main_contractor_id", nullable = false)
    private String mainContractorId;   // Keycloak subject

    @Column(name = "sub_profile_id", nullable = false)
    private UUID subProfileId;

    @Column(name = "appointment_at", nullable = false)
    private Instant appointmentAt;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes = 60;

    @Column(name = "location")
    private String location;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "cancelled_reason")
    private String cancelledReason;

    public enum BookingStatus {
        PENDING, CONFIRMED, DECLINED, COMPLETED, CANCELLED
    }

    // ── State machine transitions ─────────────────

    public void confirm() {
        assertStatus(BookingStatus.PENDING, "confirm");
        this.status = BookingStatus.CONFIRMED;
    }

    public void decline(String reason) {
        assertStatus(BookingStatus.PENDING, "decline");
        this.status = BookingStatus.DECLINED;
        this.cancelledReason = reason;
    }

    public void complete() {
        assertStatus(BookingStatus.CONFIRMED, "complete");
        this.status = BookingStatus.COMPLETED;
    }

    public void cancel(String reason) {
        if (status == BookingStatus.COMPLETED || status == BookingStatus.DECLINED) {
            throw new IllegalStateException(
                "Cannot cancel a booking with status: " + status);
        }
        this.status = BookingStatus.CANCELLED;
        this.cancelledReason = reason;
    }

    private void assertStatus(BookingStatus expected, String action) {
        if (this.status != expected) {
            throw new IllegalStateException(
                "Cannot " + action + " a booking with status: " + status);
        }
    }
}
