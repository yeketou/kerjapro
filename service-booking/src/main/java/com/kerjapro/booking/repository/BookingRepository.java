package com.kerjapro.booking.repository;

import com.kerjapro.booking.entity.Booking;
import com.kerjapro.booking.entity.Booking.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    // Main contractor — their outgoing bookings
    Page<Booking> findByMainContractorIdAndDeletedAtIsNull(String contractorId, Pageable pageable);

    Page<Booking> findByMainContractorIdAndStatusAndDeletedAtIsNull(
            String contractorId, BookingStatus status, Pageable pageable);

    // Subcontractor — their incoming bookings
    Page<Booking> findBySubProfileIdAndDeletedAtIsNull(UUID subProfileId, Pageable pageable);

    Page<Booking> findBySubProfileIdAndStatusAndDeletedAtIsNull(
            UUID subProfileId, BookingStatus status, Pageable pageable);

    Optional<Booking> findByIdAndDeletedAtIsNull(UUID id);

    // Availability check — are there confirmed bookings that overlap?
    @Query("""
        SELECT COUNT(b) > 0 FROM Booking b
        WHERE b.subProfileId = :subId
        AND b.status = 'CONFIRMED'
        AND b.deletedAt IS NULL
        AND b.appointmentAt < :end
        AND FUNCTION('datetime_plus_minutes', b.appointmentAt, b.durationMinutes) > :start
        """)
    boolean hasConflict(
            @Param("subId")  UUID subId,
            @Param("start")  Instant start,
            @Param("end")    Instant end);

    // Calendar: all confirmed/pending bookings for a sub in a time window
    @Query("""
        SELECT b FROM Booking b
        WHERE b.subProfileId = :subId
        AND b.status IN ('PENDING', 'CONFIRMED')
        AND b.deletedAt IS NULL
        AND b.appointmentAt BETWEEN :from AND :to
        ORDER BY b.appointmentAt ASC
        """)
    List<Booking> findCalendarSlots(
            @Param("subId") UUID subId,
            @Param("from")  Instant from,
            @Param("to")    Instant to);
}
