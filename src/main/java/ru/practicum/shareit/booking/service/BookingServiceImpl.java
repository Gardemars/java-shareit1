package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingStorage;
    private final UserService userService;
    private final ItemStorage itemStorage;

    @Transactional
    @Override
    public Booking add(Long bookerId, Long itemId, Booking booking) {
        User booker = userService.getByUserId(bookerId);
        Item item = itemStorage.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Предмет с id = %d не найден в базе", itemId)));

        if (!item.getAvailable()) {
            throw new ItemNotAvailableException(String.format("Предмет с id = %d не доступен", itemId));
        }

        if (Objects.equals(bookerId, item.getOwner().getId())) {
            throw new FailIdException(String.format(
                    "Делать броннирование вещи с id = %d  владелец не может", itemId));
        }

        checkTimeValidation(booking);

        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        return bookingStorage.save(booking);
    }

    @Override
    public Booking getByBookingId(Long bookingId, Long userId) {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Броннирование с id = %d не найдено в базе", bookingId)));

        boolean isOwner = Objects.equals(booking.getItem().getOwner().getId(), userId);
        boolean isBooker = Objects.equals(booking.getBooker().getId(), userId);

        if (!(isOwner || isBooker)) {
            throw new FailIdException(String.format(
                    "Просматривать броннирование с id = %d может только владелец броннирования/вещи", bookingId));
        }

        return booking;
    }

    @Transactional
    @Override
    public Booking approved(Long bookingId, Long ownerId, boolean isApprove) {
        Booking booking = this.getByBookingId(bookingId, ownerId);
        BookingStatus status = booking.getStatus();

        if (!status.equals(BookingStatus.WAITING)) {
            throw new ValidationException(String.format(
                    "Статус бронирования с id = %d уже был изменен ", bookingId));
        }

        if (Objects.equals(booking.getItem().getOwner().getId(), ownerId)) {
            booking.setStatus(isApprove ? BookingStatus.APPROVED : BookingStatus.REJECTED);
            return bookingStorage.save(booking);
        }

        throw new FailIdException(String.format(
                "Подтверждать броннирование с id = %d может только владелец вещи", bookingId));
    }

    @Override
    public List<Booking> getAllBookingByOwnerId(Long ownerId, String state) {
        List<Item> items = itemStorage.findAllByOwnerId(ownerId);
        if (items.isEmpty()) {
            throw new FailIdException(String.format(
                    "Владелец вещей с id = %d отсутствует в базе", ownerId));
        }

        switch (state) {
            case "ALL":
                return bookingStorage.findAllByItemOwnerIdOrderByStartDesc(ownerId);
            case "CURRENT":
                return bookingStorage.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, LocalDateTime.now(), LocalDateTime.now());
            case "PAST":
                return bookingStorage.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now());
            case "FUTURE":
                return bookingStorage.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now());
            case "WAITING":
                return bookingStorage.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
            case "REJECTED":
                return bookingStorage.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
            default:
                throw new StatusException();
        }
    }

    @Override
    public List<Booking> getAllBookingByBookerId(Long bookerId, String state) {
        userService.getByUserId(bookerId);

        switch (state) {
            case "ALL":
                return bookingStorage.findAllByBookerIdOrderByStartDesc(bookerId);
            case "CURRENT":
                return bookingStorage.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, LocalDateTime.now(), LocalDateTime.now());
            case "PAST":
                return bookingStorage.findAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now());
            case "FUTURE":
                return bookingStorage.findAllByBookerIdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now());
            case "WAITING":
                return bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING);
            case "REJECTED":
                return bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED);
            default:
                throw new StatusException();
        }
    }

    private void checkTimeValidation(Booking booking) {
        boolean isStartInPast = booking.getStart().isBefore(LocalDateTime.now());
        boolean isEndInPast = booking.getEnd().isBefore(LocalDateTime.now());
        boolean isEndBeforeStart = booking.getEnd().isBefore(booking.getStart());
        boolean isEndEqualStart = booking.getEnd().isEqual(booking.getStart());

        if (isStartInPast || isEndInPast || isEndBeforeStart || isEndEqualStart) {
            throw new ValidationException("Время броннирование указано неверно");
        }
    }
}