package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingAnswerDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;


    @PostMapping
    public BookingAnswerDto add(@RequestHeader(name = USER_ID_HEADER) long bookerId,
                                @Valid @RequestBody BookingRequestDto dto) {
        return bookingMapper.bookingToBookingAnswerDto(
                bookingService.add(bookerId, dto.getItemId(), bookingMapper.bookingRequestDtoToBooking(dto)));
    }

    @GetMapping("/{bookingId}")
    public BookingAnswerDto getByBookingId(@PathVariable long bookingId,
                                           @RequestHeader(name = USER_ID_HEADER) long userId) {
        return bookingMapper.bookingToBookingAnswerDto(bookingService.getByBookingId(bookingId, userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingAnswerDto approved(@PathVariable long bookingId,
                                     @RequestHeader(name = USER_ID_HEADER) long ownerId,
                                     @RequestParam boolean approved) {
        return bookingMapper.bookingToBookingAnswerDto(
                bookingService.approved(bookingId, ownerId, approved));
    }

    @GetMapping
    public List<BookingAnswerDto> getAllByBooker(@RequestHeader(name = USER_ID_HEADER) long bookerId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        return bookingMapper.bookingListToListBookingAnswerDto(
                bookingService.getAllBookingByBookerId(bookerId, state));
    }

    @GetMapping("/owner")
    public List<BookingAnswerDto> getAllByOwner(@RequestHeader(name = USER_ID_HEADER) long ownerId,
                                                @RequestParam(defaultValue = "ALL") String state) {
        return bookingMapper.bookingListToListBookingAnswerDto(
                bookingService.getAllBookingByOwnerId(ownerId, state));
    }
}