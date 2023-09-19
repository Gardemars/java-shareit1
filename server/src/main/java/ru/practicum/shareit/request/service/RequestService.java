package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.RequestWithItemDto;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestService {
    Request add(Long userId, Request request);

    List<RequestWithItemDto> getAllRequestsByOwnerId(Long ownerId);

    List<RequestWithItemDto> getAllRequest(Long userId, Pageable pageable);

    Request getByRequestId(Long requestId);

    RequestWithItemDto getByRequestIdWithItem(Long requestId, Long userId);
}
