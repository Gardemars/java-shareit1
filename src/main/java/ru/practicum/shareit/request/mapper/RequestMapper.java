package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.RequestAddDto;
import ru.practicum.shareit.request.dto.RequestAnswerDto;
import ru.practicum.shareit.request.dto.RequestWithItemDto;
import ru.practicum.shareit.request.model.Request;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    Request requestAddDtoToRequest(RequestAddDto requestAddDto);

    RequestAnswerDto requestToRequestAnswerDto(Request request);

    RequestWithItemDto requestToRequestWithItemDto(Request request);

}