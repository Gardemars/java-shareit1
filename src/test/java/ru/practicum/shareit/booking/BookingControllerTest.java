package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingAnswerDto;
import ru.practicum.shareit.booking.dto.BookingNewAnswerDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.FailIdException;
import ru.practicum.shareit.exception.controller.ErrorHandler;
import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final Long bookerId = 1L;
    private final Long bookingId = 1L;
    @Mock
    private BookingService bookingService;
    @Mock
    private BookingMapper bookingMapper;
    @InjectMocks
    private BookingController bookingController;
    private MockMvc mockMvc;
    private User user;
    private Item item;
    private ItemForBookingDto itemForBookingDto;
    private BookingAnswerDto bookingAnswerDto;
    private BookingNewAnswerDto bookingNewAnswerDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .setControllerAdvice(ErrorHandler.class)
                .build();

        user = new User(
                1L,
                "John",
                "john.doe@mail.com");

        item = new Item(
                1L,
                "name",
                "description",
                true,
                user,
                null,
                null,
                null,
                null);

        itemForBookingDto = new ItemForBookingDto(item.getId(), item.getName());

        bookingAnswerDto = new BookingAnswerDto(
                bookingId,
                LocalDateTime.now(),
                LocalDateTime.now(),
                BookingStatus.WAITING, item, user);

        bookingNewAnswerDto = new BookingNewAnswerDto(
                bookingId,
                LocalDateTime.now(),
                LocalDateTime.now(),
                BookingStatus.WAITING, itemForBookingDto, user);
    }

    @Test
    void add_shouldCreateAndReturnNewBooking() throws Exception {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1L, LocalDateTime.now(), LocalDateTime.now());
        BookingRequestDto bookingRequestDto1 = new BookingRequestDto(1L, null, null);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, bookerId)
                        .content(objectMapper.writeValueAsString(bookingRequestDto1)))
                .andExpect(status().isBadRequest());

        when(bookingService.add(anyLong(), anyLong(), any())).thenReturn(bookingNewAnswerDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, bookerId)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingNewAnswerDto)));
    }

    @Test
    void getByBookingId_shouldReturnBooking() throws Exception {
        when(bookingMapper.bookingToBookingAnswerDto(any())).thenReturn(bookingAnswerDto);

        mockMvc.perform(get("/bookings/" + bookingId).header(USER_ID_HEADER, bookerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingAnswerDto)));
    }

    @Test
    void getByBookingId_shouldReturnNotFound() throws Exception {
        when(bookingMapper.bookingToBookingAnswerDto(any())).thenThrow(new FailIdException(""));

        mockMvc.perform(get("/bookings/" + bookingId).header(USER_ID_HEADER, bookerId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllByBookerTest() throws Exception {
        when(bookingMapper.bookingListToListBookingAnswerDto(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings").header(USER_ID_HEADER, bookerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyList())));
    }

    @Test
    void getAllByOwnerTest() throws Exception {
        when(bookingMapper.bookingListToListBookingAnswerDto(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings/owner").header(USER_ID_HEADER, bookerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyList())));
    }

    @Test
    void approvedTest() throws Exception {
        when(bookingMapper.bookingToBookingAnswerDto(any())).thenReturn(bookingAnswerDto);

        mockMvc.perform(patch("/bookings/" + bookingId)
                        .header(USER_ID_HEADER, bookerId)
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingAnswerDto)));
    }
}
