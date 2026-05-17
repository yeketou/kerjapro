package com.kerjapro.booking.service;

import com.kerjapro.common.exception.BusinessException;
import com.kerjapro.common.exception.ResourceNotFoundException;
import com.kerjapro.booking.dto.request.CreateBookingRequest;
import com.kerjapro.booking.dto.request.CancelBookingRequest;
import com.kerjapro.booking.dto.response.BookingDto;
import com.kerjapro.booking.dto.response.CalendarSlotDto;
import com.kerjapro.booking.entity.Booking;
import com.kerjapro.booking.entity.Booking.BookingStatus;
import com.kerjapro.booking.mapper.BookingMapper;
import com.kerjapro.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository repo;
    private final BookingMapper     mapper;

    // ─────────────────────────────────────────────
    // Create booking (Main Contractor)
    // ─────────────────────────────────────────────

    @Transactional
    public BookingDto createBooking(String contractorId, CreateBookingRequest request) {
        // Enforce minimum lead time: 24 hours
        if (request.getAppointmentAt().isBefore(Instant.now().plus(24, ChronoUnit.HOURS))) {
            throw new BusinessException(
                "Appointment must be at least 24 hours from now",
                HttpStatus.BAD_REQUEST, "BOOKING_LEAD_TIME");
        }

        // Check for scheduling conflicts
        Instant end = request.getAppointmentAt()
                .plus(request.getDurationMinutes(), ChronoUnit.MINUTES);

        if (repo.hasConflict(request.getSubProfileId(), request.getAppointmentAt(), end)) {
            throw new BusinessException(
                "The subcontractor already has a confirmed booking at this time",
                HttpStatus.CONFLICT, "BOOKING_CONFLICT");
        }

        Booking booking = mapper.toEntity(request);
        booking.setMainContractorId(contractorId);
        Booking saved = repo.save(booking);

        log.info("Booking created: id={}, sub={}, at={}", saved.getId(),
                request.getSubProfileId(), request.getAppointmentAt());
        return mapper.toDto(saved);
    }

    // ─────────────────────────────────────────────
    // Read
    // ─────────────────────────────────────────────

    @Transactional(readOnly = true)
    public BookingDto getBooking(String userId, UUID bookingId) {
        Booking booking = findActive(bookingId);
        assertAccess(booking, userId);
        return mapper.toDto(booking);
    }

    @Transactional(readOnly = true)
    public Page<BookingDto> getContractorBookings(String contractorId,
                                                   BookingStatus status, Pageable pageable) {
        Page<Booking> page = (status != null)
                ? repo.findByMainContractorIdAndStatusAndDeletedAtIsNull(contractorId, status, pageable)
                : repo.findByMainContractorIdAndDeletedAtIsNull(contractorId, pageable);
        return page.map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<BookingDto> getSubBookings(UUID subProfileId,
                                            BookingStatus status, Pageable pageable) {
        Page<Booking> page = (status != null)
                ? repo.findBySubProfileIdAndStatusAndDeletedAtIsNull(subProfileId, status, pageable)
                : repo.findBySubProfileIdAndDeletedAtIsNull(subProfileId, pageable);
        return page.map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<CalendarSlotDto> getCalendar(UUID subProfileId, Instant from, Instant to) {
        return repo.findCalendarSlots(subProfileId, from, to)
                .stream().map(mapper::toCalendarSlot).toList();
    }

    // ─────────────────────────────────────────────
    // State transitions
    // ─────────────────────────────────────────────

    @Transactional
    public BookingDto confirm(String subUserId, UUID bookingId) {
        Booking booking = findActive(bookingId);
        try {
            booking.confirm();
        } catch (IllegalStateException e) {
            throw new BusinessException(e.getMessage(), HttpStatus.CONFLICT, "INVALID_TRANSITION");
        }
        log.info("Booking confirmed: id={}", bookingId);
        return mapper.toDto(repo.save(booking));
    }

    @Transactional
    public BookingDto decline(String subUserId, UUID bookingId, CancelBookingRequest request) {
        Booking booking = findActive(bookingId);
        try {
            booking.decline(request.getReason());
        } catch (IllegalStateException e) {
            throw new BusinessException(e.getMessage(), HttpStatus.CONFLICT, "INVALID_TRANSITION");
        }
        log.info("Booking declined: id={}", bookingId);
        return mapper.toDto(repo.save(booking));
    }

    @Transactional
    public BookingDto complete(String userId, UUID bookingId) {
        Booking booking = findActive(bookingId);
        assertAccess(booking, userId);
        try {
            booking.complete();
        } catch (IllegalStateException e) {
            throw new BusinessException(e.getMessage(), HttpStatus.CONFLICT, "INVALID_TRANSITION");
        }
        log.info("Booking completed: id={}", bookingId);
        return mapper.toDto(repo.save(booking));
    }

    @Transactional
    public BookingDto cancel(String userId, UUID bookingId, CancelBookingRequest request) {
        Booking booking = findActive(bookingId);
        assertAccess(booking, userId);
        try {
            booking.cancel(request.getReason());
        } catch (IllegalStateException e) {
            throw new BusinessException(e.getMessage(), HttpStatus.CONFLICT, "INVALID_TRANSITION");
        }
        log.info("Booking cancelled: id={}", bookingId);
        return mapper.toDto(repo.save(booking));
    }

    // ─────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────

    private Booking findActive(UUID id) {
        return repo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
    }

    private void assertAccess(Booking booking, String userId) {
        boolean isContractor = booking.getMainContractorId().equals(userId);
        boolean isSub        = booking.getSubProfileId().toString().equals(userId);
        if (!isContractor && !isSub) {
            throw new BusinessException(
                "Access denied to this booking", HttpStatus.FORBIDDEN, "BOOKING_ACCESS_DENIED");
        }
    }
}
