package com.kerjapro.booking.controller;

import com.kerjapro.booking.dto.request.CancelBookingRequest;
import com.kerjapro.booking.dto.request.CreateBookingRequest;
import com.kerjapro.booking.dto.response.BookingDto;
import com.kerjapro.booking.dto.response.CalendarSlotDto;
import com.kerjapro.booking.entity.Booking.BookingStatus;
import com.kerjapro.booking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Appointment booking and state machine")
public class BookingController {

    private final BookingService service;

    @PostMapping
    @Operation(summary = "Create a booking request (Main Contractor)")
    public ResponseEntity<BookingDto> create(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateBookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createBooking(userId, request));
    }

    @GetMapping("/{bookingId}")
    @Operation(summary = "Get booking details")
    public ResponseEntity<BookingDto> get(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID bookingId) {
        return ResponseEntity.ok(service.getBooking(userId, bookingId));
    }

    @GetMapping("/my/outgoing")
    @Operation(summary = "My bookings as main contractor")
    public ResponseEntity<Page<BookingDto>> myOutgoing(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                service.getContractorBookings(userId, status, PageRequest.of(page, size)));
    }

    @GetMapping("/my/incoming/{subProfileId}")
    @Operation(summary = "Incoming bookings for a subcontractor profile")
    public ResponseEntity<Page<BookingDto>> myIncoming(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID subProfileId,
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                service.getSubBookings(subProfileId, status, PageRequest.of(page, size)));
    }

    // ── State transitions ────────────────────────

    @PostMapping("/{bookingId}/confirm")
    @Operation(summary = "Confirm a booking (Subcontractor)")
    public ResponseEntity<BookingDto> confirm(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID bookingId) {
        return ResponseEntity.ok(service.confirm(userId, bookingId));
    }

    @PostMapping("/{bookingId}/decline")
    @Operation(summary = "Decline a booking (Subcontractor)")
    public ResponseEntity<BookingDto> decline(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID bookingId,
            @RequestBody(required = false) CancelBookingRequest request) {
        return ResponseEntity.ok(service.decline(userId, bookingId,
                request != null ? request : new CancelBookingRequest()));
    }

    @PostMapping("/{bookingId}/complete")
    @Operation(summary = "Mark booking as completed")
    public ResponseEntity<BookingDto> complete(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID bookingId) {
        return ResponseEntity.ok(service.complete(userId, bookingId));
    }

    @PostMapping("/{bookingId}/cancel")
    @Operation(summary = "Cancel a booking")
    public ResponseEntity<BookingDto> cancel(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID bookingId,
            @RequestBody(required = false) CancelBookingRequest request) {
        return ResponseEntity.ok(service.cancel(userId, bookingId,
                request != null ? request : new CancelBookingRequest()));
    }

    // ── Calendar ─────────────────────────────────

    @GetMapping("/calendar/{subProfileId}")
    @Operation(summary = "Get subcontractor availability calendar")
    public ResponseEntity<List<CalendarSlotDto>> calendar(
            @PathVariable UUID subProfileId,
            @RequestParam Instant from,
            @RequestParam Instant to) {
        return ResponseEntity.ok(service.getCalendar(subProfileId, from, to));
    }
}
