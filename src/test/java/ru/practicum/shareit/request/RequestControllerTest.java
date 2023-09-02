package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.controller.ErrorHandler;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.dto.RequestAddDto;
import ru.practicum.shareit.request.dto.RequestAnswerDto;
import ru.practicum.shareit.request.dto.RequestWithItemDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RequestControllerTest {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final Long userId = 1L;
    private final Long requestId = 1L;
    @Mock
    private RequestMapper requestMapper;
    @Mock
    private RequestService requestService;
    @InjectMocks
    private RequestController requestController;
    private MockMvc mockMvc;
    private Request request;
    private RequestWithItemDto requestWithItemDto;
    private User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(requestController)
                .setControllerAdvice(ErrorHandler.class)
                .build();

        user = new User(
                1L,
                "John",
                "john.doe@mail.com");
        request = new Request(requestId, "description", LocalDateTime.now(), user);

        requestWithItemDto = new RequestWithItemDto(requestId, request.getDescription(),
                LocalDateTime.now(), List.of(new ItemDto()));
    }

    @Test
    void addRequest_shouldReturnNewRequest() throws Exception {
        RequestAddDto requestAddDto = new RequestAddDto(request.getDescription());
        RequestAnswerDto requestAnswerDto = new RequestAnswerDto(requestId, request.getDescription(), request.getCreated());


        when(requestMapper.requestAddDtoToRequest(requestAddDto)).thenReturn(request);
        when(requestService.add(any(), any())).thenReturn(request);
        when(requestMapper.requestToRequestAnswerDto(
                requestService.add(userId, requestMapper.requestAddDtoToRequest(requestAddDto))))
                .thenReturn(requestAnswerDto);

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAddDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestAnswerDto)));
    }


    @Test
    void getAllRequestByOwnerId_shouldReturnListOfRequests() throws Exception {
        List<RequestWithItemDto> requestWithItemDtos = List.of(requestWithItemDto);

        when(requestService.getAllRequestsByOwnerId(userId)).thenReturn(requestWithItemDtos);


        mockMvc.perform(get("/requests").header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestWithItemDtos)));
    }

    @Test
    void getAllRequestByOwnerId_shouldReturnNotFound() throws Exception {
        when(requestService.getAllRequestsByOwnerId(userId))
                .thenThrow(new EntityNotFoundException(""));

        mockMvc.perform(get("/requests").header(USER_ID_HEADER, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllRequests_shouldReturnListOfRequests() throws Exception {
        List<RequestWithItemDto> requestWithItemDtos = List.of(requestWithItemDto);

        when(requestService.getAllRequest(anyLong(), any())).thenReturn(requestWithItemDtos);

        mockMvc.perform(get("/requests/all").header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestWithItemDtos)));
    }

    @Test
    void getAllRequests_shouldReturnNotFond() throws Exception {
        when(requestService.getAllRequest(anyLong(), any()))
                .thenThrow(new EntityNotFoundException(""));

        mockMvc.perform(get("/requests/all").header(USER_ID_HEADER, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByRequestIdWithItem_shouldReturnRequest() throws Exception {
        when(requestService.getByRequestIdWithItem(requestId, userId)).thenReturn(requestWithItemDto);

        mockMvc.perform(get("/requests/" + requestId).header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestWithItemDto)));
    }

    @Test
    void getByRequestIdWithItem_shouldReturnNotFound() throws Exception {

        when(requestService.getByRequestIdWithItem(requestId, userId))
                .thenThrow(new EntityNotFoundException(""));

        mockMvc.perform(get("/requests/" + requestId).header(USER_ID_HEADER, userId))
                .andExpect(status().isNotFound());
    }
}
