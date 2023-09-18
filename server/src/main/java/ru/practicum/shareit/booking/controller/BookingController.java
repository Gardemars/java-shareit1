package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingAnswerDto;
import ru.practicum.shareit.booking.dto.BookingNewAnswerDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.core.mapper.PaginationMapper;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;


    @PostMapping
    public BookingNewAnswerDto add(@RequestHeader(name = USER_ID_HEADER) long bookerId,
                                   @RequestBody BookingRequestDto dto) {
        log.info("Запрос на создание броннирования");
        return bookingService.add(bookerId, dto.getItemId(), bookingMapper.bookingRequestDtoToBooking(dto));
    }

    @GetMapping("/{bookingId}")
    public BookingAnswerDto getByBookingId(@PathVariable long bookingId,
                                           @RequestHeader(name = USER_ID_HEADER) long userId) {
        log.info("Запрос на выдачу броннирования");
        return bookingMapper.bookingToBookingAnswerDto(bookingService.getByBookingId(bookingId, userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingAnswerDto approved(@PathVariable long bookingId,
                                     @RequestHeader(name = USER_ID_HEADER) long ownerId,
                                     @RequestParam boolean approved) {
        log.info("Запрос на подтверждение бронирования");
        return bookingMapper.bookingToBookingAnswerDto(
                bookingService.approved(bookingId, ownerId, approved));
    }

    @GetMapping
    public List<BookingAnswerDto> getAllByBooker(@RequestHeader(name = USER_ID_HEADER) long bookerId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @RequestParam(required = false) Integer from,
                                                 @RequestParam(required = false) Integer size) {
        log.info("Запрос на выдачу списка бронирований пользователя");
        return bookingMapper.bookingListToListBookingAnswerDto(
                bookingService.getAllBookingByBookerId(bookerId, state, PaginationMapper.toMakePage(from, size)));
    }

    @GetMapping("/owner")
    public List<BookingAnswerDto> getAllByOwner(@RequestHeader(name = USER_ID_HEADER) long ownerId,
                                                @RequestParam(defaultValue = "ALL") String state,
                                                @RequestParam(required = false) Integer from,
                                                @RequestParam(required = false) Integer size) {
        log.info("Запрос на выдачу списка бронирований для всех предметов владельца");
        return bookingMapper.bookingListToListBookingAnswerDto(
                bookingService.getAllBookingByOwnerId(ownerId, state, PaginationMapper.toMakePage(from, size)));
    }
}
