package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingNewAnswerDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;


public interface BookingService {
    BookingNewAnswerDto add(Long userId, Long itemId, Booking booking);

    Booking approved(Long bookingId, Long ownerId, boolean isApprove);

    Booking getByBookingId(Long bookingId, Long userId);

    List<Booking> getAllBookingByOwnerId(Long ownerId, String state, Pageable pageable);

    List<Booking> getAllBookingByBookerId(Long bookerId, String state, Pageable pageable);
}
