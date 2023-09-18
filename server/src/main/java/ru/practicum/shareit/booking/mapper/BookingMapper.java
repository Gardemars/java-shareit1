package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingAnswerDto;
import ru.practicum.shareit.booking.dto.BookingNewAnswerDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    Booking bookingRequestDtoToBooking(BookingRequestDto bookingRequestDto);

    BookingAnswerDto bookingToBookingAnswerDto(Booking booking);

    BookingNewAnswerDto bookingToBookingNewAnswerDto(Booking booking);

    List<BookingAnswerDto> bookingListToListBookingAnswerDto(List<Booking> bookings);

    @Mapping(target = "bookerId", source = "booker.id")
    BookingShortDto bookingToBookingShortDto(Booking booking);

}
