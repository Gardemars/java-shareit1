package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.PaginationMapper;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestWithItemDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServerTest {

    private final Long userId = 1L;
    private final Long requestId = 1L;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private RequestMapper requestMapper;
    @InjectMocks
    private RequestServiceImpl requestService;
    private User user = new User(
            userId,
            "John",
            "john.doe@mail.com");

    private Request request = new Request(
            requestId,
            "description",
            LocalDateTime.of(2023, 8, 26, 11, 0, 0),
            user);

    private RequestWithItemDto requestWithItemDto = new RequestWithItemDto(
            requestId,
            request.getDescription(),
            LocalDateTime.of(2023, 8, 26, 11, 0, 0),
            List.of(new ItemDto()));

    private ItemDto itemDto = new ItemDto(
            1L,
            "item",
            "description",
            true,
            null);

    @Test
    void add_shouldCreateNewRequest() {
        Request newRequest = Request.builder()
                .id(null)
                .description("text")
                .created(null)
                .user(null)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Request request1 = requestService.add(userId, newRequest);

        assertThat(request1.getDescription()).isEqualTo(newRequest.getDescription());
        assertThat(request1.getCreated()).isBefore(LocalDateTime.of(2023, 9, 26, 11,
                0, 0));
        assertThat(request1.getUser()).isEqualTo(user);
    }

    @Test
    void add_shouldThrowEntityNotFoundExceptionIfUserIsNotExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.add(userId, request))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getAllRequestsByOwnerId_shouldReturnListOfRequests() {
        List<Request> requests = List.of(request);
        List<ItemDto> items = List.of(itemDto);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemMapper.itemListToItemDtoList(any())).thenReturn(items);
        when(requestMapper.requestToRequestWithItemDto(any())).thenReturn(requestWithItemDto);
        when(requestRepository.findAllByUserIdOrderByCreatedDesc(userId)).thenReturn(requests);

        List<RequestWithItemDto> result = requestService.getAllRequestsByOwnerId(userId);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getItems().get(0)).isEqualTo(itemDto);

    }

    @Test
    void getAllRequestsByOwnerId_shouldThrowEntityNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> requestService.getAllRequestsByOwnerId(userId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getByRequestId_shouldReturnRequest() {
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        assertThat(requestService.getByRequestId(requestId)).isEqualTo(request);
    }

    @Test
    void getByRequestId_shouldThrowEntityNotFoundException() {
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> requestService.getByRequestId(requestId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getAllRequests_shouldReturnListOfRequests() {
        List<Request> requests = List.of(request);
        List<ItemDto> items = List.of(itemDto);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemMapper.itemListToItemDtoList(any())).thenReturn(items);
        when(requestMapper.requestToRequestWithItemDto(any())).thenReturn(requestWithItemDto);
        when(requestRepository.findAllByUserIdNotOrderByCreatedDesc(any(), any())).thenReturn(requests);

        List<RequestWithItemDto> result = requestService.getAllRequest(userId, PaginationMapper.toMakePage(1, 1));
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getItems().get(0)).isEqualTo(itemDto);
    }

    @Test
    void getAllRequests_shouldThrowEntityNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> requestService.getAllRequest(userId, PaginationMapper.toMakePage(1, 1)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getAllRequests_shouldReturnValidationException() {
        assertThatThrownBy(() ->
                requestService.getAllRequest(userId, PaginationMapper.toMakePage(0, 0)))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void getByRequestIdIdWithItem_shouldReturnRequest() {
        List<ItemDto> items = List.of(itemDto);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemMapper.itemListToItemDtoList(any())).thenReturn(items);
        when(requestMapper.requestToRequestWithItemDto(any())).thenReturn(requestWithItemDto);

        assertThat(requestService.getByRequestIdWithItem(requestId, userId)).isEqualTo(requestWithItemDto);
    }

    @Test
    void getByRequestIdIdWithItem_shouldThrowEntityNotFoundExceptionIfNotRequest() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> requestService.getByRequestIdWithItem(requestId, userId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getByRequestIdIdWithItem_shouldThrowEntityNotFoundExceptionIfNotUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> requestService.getByRequestIdWithItem(requestId, userId))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
