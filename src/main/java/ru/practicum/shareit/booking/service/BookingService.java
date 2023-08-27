package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;


public interface BookingService {
    Booking add(Long userId, Long itemId, Booking booking);

    Booking approved(Long bookingId, Long ownerId, boolean isApprove);

    Booking getByBookingId(Long bookingId, Long userId);

    List<Booking> getAllBookingByOwnerId(Long ownerId, String state);

    List<Booking> getAllBookingByBookerId(Long bookerId, String state);
}