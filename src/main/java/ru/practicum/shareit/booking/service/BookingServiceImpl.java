package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.constant.State;
import ru.practicum.shareit.booking.dto.BookingNewAnswerDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;

    @Transactional
    @Override
    public BookingNewAnswerDto add(Long bookerId, Long itemId, Booking booking) {
        User booker = userRepository.findById(bookerId).orElseThrow(()
                -> new EntityNotFoundException(String.format("Пользователь с id = %d не найден в базе", bookerId)));

        Item item = itemRepository.findById(itemId).orElseThrow(() ->
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
        BookingNewAnswerDto result = bookingMapper.bookingToBookingNewAnswerDto(bookingRepository.save(booking));
        result.setItem(itemMapper.itemToItemForBookingDto(item));
        return result;
    }

    @Override
    public Booking getByBookingId(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
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

        //Владелец вещи отвечает на запрос
        if (Objects.equals(booking.getItem().getOwner().getId(), ownerId)) {
            booking.setStatus(isApprove ? BookingStatus.APPROVED : BookingStatus.REJECTED);
            return bookingRepository.save(booking);
        }

        throw new FailIdException(String.format(
                "Подтверждать броннирование с id = %d может только владелец вещи", bookingId));
    }

    @Override
    public List<Booking> getAllBookingByOwnerId(Long ownerId, String state, Pageable pageable) {
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        // Если нет вещей этого пользователя в базе - ошибка
        if (items.isEmpty()) {
            throw new FailIdException(String.format(
                    "Владелец вещей с id = %d отсутствует в базе", ownerId));
        }

        try {
            switch (State.valueOf(state)) {
                case ALL:
                    return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId, pageable);
                case CURRENT:
                    return bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                case PAST:
                    return bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now(), pageable);
                case FUTURE:
                    return bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now(), pageable);
                case WAITING:
                    return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING, pageable);
                case REJECTED:
                    return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED, pageable);
                default:
                    throw new StatusException();
            }
        } catch (IllegalArgumentException exp) {
            throw new StatusException();
        }
    }

    @Override
    public List<Booking> getAllBookingByBookerId(Long bookerId, String state, Pageable pageable) {
        //Проверка наличия такого пользователя в системе
        userRepository.findById(bookerId).orElseThrow(()
                -> new EntityNotFoundException(String.format("Пользователь с id = %d не найден в базе", bookerId)));

        try {
            switch (State.valueOf(state)) {
                case ALL:
                    return bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId, pageable);
                case CURRENT:
                    return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                case PAST:
                    return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now(), pageable);
                case FUTURE:
                    return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now(), pageable);
                case WAITING:
                    return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING, pageable);
                case REJECTED:
                    return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED, pageable);
                default:
                    throw new StatusException();
            }
        } catch (IllegalArgumentException exp) {
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