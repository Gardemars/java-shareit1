package ru.practicum.shareit.item;

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
import ru.practicum.shareit.core.exception.EntityNotFoundException;
import ru.practicum.shareit.core.exception.ValidationException;
import ru.practicum.shareit.core.exception.controller.ErrorHandler;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemMapper itemMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private MockMvc mockMvc;


    private ItemDto itemDto;
    private ItemGetDto itemGetDto;
    private final Long userId = 1L;
    private final Long itemId = 1L;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .setControllerAdvice(ErrorHandler.class)
                .build();

        itemDto = new ItemDto(
                itemId,
                "item",
                "description",
                true,
                null);

        itemGetDto = new ItemGetDto(
                itemId,
                itemDto.getName(),
                itemDto.getDescription(),
                true,
                null,
                null,
                null);
    }

    @Test
    void addShouldCreateAndReturnNewItem() throws Exception {
        when(itemMapper.itemToItemDto(any())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));
    }

    @Test
    void updateShouldUpdateItem() throws Exception {
        UpdateItemDto updateItemDto = new UpdateItemDto(itemId, itemDto.getName(), itemDto.getDescription(), true);

        when(itemMapper.itemToItemDto(any())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/" + itemId)
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));
    }

    @Test
    void updateShouldReturnNotFound() throws Exception {
        UpdateItemDto updateItemDto = new UpdateItemDto(itemId, itemDto.getName(), itemDto.getDescription(), true);

        when(itemMapper.itemToItemDto(any())).thenThrow(new EntityNotFoundException(""));

        mockMvc.perform(patch("/items/" + itemId)
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemDto))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void remove() throws Exception {
        mockMvc.perform(delete("/items/" + itemId))
                .andExpect(status().isOk());
    }

    @Test
    void getByItemIdShouldReturnItem() throws Exception {
        when(itemMapper.itemToItemGetDto(any())).thenReturn(itemGetDto);

        mockMvc.perform(get("/items/" + itemId).header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemGetDto)));
    }

    @Test
    void getByItemIdShouldReturnNotFound() throws Exception {
        when(itemMapper.itemToItemGetDto(any())).thenThrow(new EntityNotFoundException(""));

        mockMvc.perform(get("/items/" + itemId).header(USER_ID_HEADER, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByOwnerIdShouldReturnListOfItems() throws Exception {
        List<ItemGetDto> items = List.of(itemGetDto);

        when(itemMapper.itemListToItemGetDtoList(any())).thenReturn(items);

        mockMvc.perform(get("/items").header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
    }

    @Test
    void getByOwnerIdShouldReturnNotFound() throws Exception {
        when(itemMapper.itemListToItemGetDtoList(any()))
                .thenThrow(new EntityNotFoundException(""));

        mockMvc.perform(get("/items").header(USER_ID_HEADER, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByOwnerIdShouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/items")).andExpect(status().isInternalServerError());
    }

    @Test
    void searchShouldReturnListOfItems() throws Exception {
        List<ItemDto> items = List.of(itemDto);

        when(itemMapper.itemListToItemDtoList(any())).thenReturn(items);

        mockMvc.perform(get("/items/search").queryParam("text", "test"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
    }

    @Test
    void commentShouldReturnNewComment() throws Exception {
        AddCommentDto addCommentDto = new AddCommentDto("text");

        CommentDto comment = new CommentDto(1L, "text", "name", LocalDateTime.now());

        when(commentMapper.commentToCommentDto(any())).thenReturn(comment);

        mockMvc.perform(post("/items/" + itemId + "/comment")
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addCommentDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(comment)));
    }

    @Test
    void commentShouldReturnBadRequest() throws Exception {
        AddCommentDto addCommentDto = new AddCommentDto("text");

        when(commentMapper.commentToCommentDto(any())).thenThrow(new ValidationException(""));

        mockMvc.perform(post("/items/" + itemId + "/comment")
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addCommentDto)))
                .andExpect(status().isBadRequest());
    }
}
