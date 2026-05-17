package com.kerjapro.booking.mapper;

import com.kerjapro.booking.dto.request.CreateBookingRequest;
import com.kerjapro.booking.dto.response.BookingDto;
import com.kerjapro.booking.dto.response.CalendarSlotDto;
import com.kerjapro.booking.entity.Booking;
import org.mapstruct.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingMapper {

    BookingDto toDto(Booking entity);

    @Mapping(target = "mainContractorId", ignore = true)
    @Mapping(target = "status",           ignore = true)
    @Mapping(target = "cancelledReason",  ignore = true)
    Booking toEntity(CreateBookingRequest request);

    @Mapping(target = "bookingId",       source = "id")
    @Mapping(target = "appointmentEnd",  expression = "java(calcEnd(entity))")
    CalendarSlotDto toCalendarSlot(Booking entity);

    default Instant calcEnd(Booking entity) {
        return entity.getAppointmentAt()
                .plus(entity.getDurationMinutes(), ChronoUnit.MINUTES);
    }
}
