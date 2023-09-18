package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.core.mapper.PaginationMapper;
import ru.practicum.shareit.request.dto.RequestAddDto;
import ru.practicum.shareit.request.dto.RequestAnswerDto;
import ru.practicum.shareit.request.dto.RequestWithItemDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class RequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final RequestMapper requestMapper;
    private final RequestService requestService;

    @PostMapping
    public RequestAnswerDto add(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                @RequestBody RequestAddDto requestAddDto) {
        log.info("Запрос на создание запроса");
        return requestMapper.requestToRequestAnswerDto(requestService.add(userId, requestMapper.requestAddDtoToRequest(requestAddDto)));
    }

    @GetMapping
    public List<RequestWithItemDto> getAllRequestByOwnerId(@RequestHeader(name = USER_ID_HEADER) Long userId) {
        log.info("Запрос на выдачу списка запросов пользователя");
        return requestService.getAllRequestsByOwnerId(userId);
    }

    @GetMapping("/all")
    public List<RequestWithItemDto> getAllRequest(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                                  @RequestParam(required = false) Integer from,
                                                  @RequestParam(required = false) Integer size) {
        log.info("Запрос на выдачу списка всех запросов");
        return requestService.getAllRequest(userId, PaginationMapper.toMakePage(from, size));
    }

    @GetMapping("/{requestId}")
    public RequestWithItemDto getByRequestIdWithItem(@PathVariable Long requestId, @RequestHeader(name = USER_ID_HEADER) Long userId) {
        log.info("Запрос на выдачу запроса");
        return requestService.getByRequestIdWithItem(requestId, userId);
    }

}
