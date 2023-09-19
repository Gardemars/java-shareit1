package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@Controller
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(USER_ID_HEADER) Long userId,
                                      @RequestBody @Valid BookingRequestDto dto) {
        log.info("Запрос на создание броннирования - сервер gateway");
        return bookingClient.add(userId, dto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getByBookingId(@PathVariable Long bookingId,
                                                 @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос на выдачу броннирования - сервер gateway");
        return bookingClient.getByBookingId(bookingId, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approved(@PathVariable Long bookingId,
                                           @RequestHeader(USER_ID_HEADER) Long ownerId,
                                           @RequestParam boolean approved) {
        log.info("Запрос на подтверждение бронирования - сервер gateway");
        return bookingClient.approved(bookingId, ownerId, approved);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByBooker(@RequestHeader(USER_ID_HEADER) Long bookerId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {

        log.info("Запрос на создание броннирования - сервер gateway");
        return bookingClient.getAllByBooker(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                @RequestParam(defaultValue = "ALL") String state,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {

        log.info("Запрос на создание броннирования - сервер gateway");
        return bookingClient.getAllByOwner(ownerId, state, from, size);
    }
}
