package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestAddDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@Controller
@AllArgsConstructor
@RequestMapping("/requests")
public class RequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                      @Valid @RequestBody RequestAddDto dto) {
        log.info("Запрос на создание запроса - сервер gateway");
        return requestClient.add(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestByOwnerId(@RequestHeader(name = USER_ID_HEADER) Long userId) {
        log.info("Запрос на выдачу списка запросов пользователя - сервер gateway");
        return requestClient.getAllRequestByOwnerId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequest(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                @Positive @RequestParam(defaultValue = "20") Integer size) {
        log.info("Запрос на выдачу списка всех запросов - сервер gateway");
        return requestClient.getAllRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getByRequestIdWithItem(@PathVariable Long requestId, @RequestHeader(name = USER_ID_HEADER) Long userId) {
        log.info("Запрос на выдачу запроса - сервер gateway");
        return requestClient.getByRequestIdWithItem(requestId, userId);
    }
}