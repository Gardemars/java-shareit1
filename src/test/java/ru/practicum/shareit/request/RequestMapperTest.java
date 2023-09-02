package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import ru.practicum.shareit.request.dto.RequestAddDto;
import ru.practicum.shareit.request.dto.RequestAnswerDto;
import ru.practicum.shareit.request.dto.RequestWithItemDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestMapperTest {
    @Spy
    private RequestMapper requestMapper = Mappers.getMapper(RequestMapper.class);

    private Long userId;
    private Long requestId;

    private User user;
    private Request request;

    @BeforeEach
    void setUp() {
        userId = 1L;
        requestId = 1L;

        user = new User(
                userId,
                "John",
                "john.doe@mail.com");

        request = new Request(
                requestId,
                "description",
                LocalDateTime.now(),
                user);
    }

    @Test
    void requestToRequestAnswerDto() {
        RequestAnswerDto dto = requestMapper.requestToRequestAnswerDto(request);
        assertThat(dto.getId()).isEqualTo(request.getId());
        assertThat(dto.getDescription()).isEqualTo(request.getDescription());
    }

    @Test
    void requestToRequestAnswerDto_shouldReturnNull() {
        RequestAnswerDto dto = requestMapper.requestToRequestAnswerDto(null);
        assertThat(dto).isEqualTo(null);
    }

    @Test
    void requestToRequestWithItemDto() {
        RequestWithItemDto dto = requestMapper.requestToRequestWithItemDto(request);
        assertThat(dto.getId()).isEqualTo(request.getId());
        assertThat(dto.getDescription()).isEqualTo(request.getDescription());
    }

    @Test
    void requestToRequestWithItemDto_shouldReturnNull() {
        RequestWithItemDto dto = requestMapper.requestToRequestWithItemDto(null);
        assertThat(dto).isEqualTo(null);
    }

    @Test
    void requestAddDtoToRequest_shouldReturnNull() {
        Request request1 = requestMapper.requestAddDtoToRequest(null);
        assertThat(request1).isEqualTo(null);
    }

    @Test
    void requestAddDtoToRequest() {
        Request request1 = requestMapper.requestAddDtoToRequest(
                new RequestAddDto("str"));
        assertThat(request1.getDescription()).isEqualTo("str");
    }
}
