package ru.practicum.shareit.booking;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingNewAnswerDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServerTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private BookingMapper bookingMapper;

    private Long userId;
    private Long itemId;
    private Long bookingId;

    private User user;
    private User user1;
    private Item item;
    private Item item1;
    private Booking booking;
    private Booking newBooking;
    private ItemForBookingDto itemForBookingDto;
    private BookingNewAnswerDto bookingNewAnswerDto;

    @BeforeEach
    void setUp() {
        userId = 1L;
        itemId = 1L;
        bookingId = 1L;

        user = new User(
                userId,
                "John",
                "john.doe@mail.com");

        user1 = new User(
                2L,
                "Jo",
                "jo.do@mail.com");

        item = new Item(
                itemId,
                "name",
                "description",
                true,
                user,
                new BookingShortDto(2L, 2L),
                new BookingShortDto(3L, 2L),
                null,
                null);

        item1 = new Item(
                itemId,
                "name",
                "description",
                true,
                user1,
                new BookingShortDto(2L, 2L),
                new BookingShortDto(3L, 2L),
                null,
                null);

        booking = new Booking(
                bookingId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                user,
                BookingStatus.WAITING);

        newBooking = new Booking(
                bookingId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                null,
                null,
                null);

        itemForBookingDto = new ItemForBookingDto(item1.getId(), item1.getName());

        bookingNewAnswerDto = new BookingNewAnswerDto(
                bookingId,
                LocalDateTime.now(),
                LocalDateTime.now(),
                BookingStatus.WAITING, itemForBookingDto, user);
    }

    @Test
    void add_shouldCreateBooking() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item1));
        when(bookingMapper.bookingToBookingNewAnswerDto(any())).thenReturn(bookingNewAnswerDto);
        when(itemMapper.itemToItemForBookingDto(item1)).thenReturn(itemForBookingDto);
        when(bookingRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        BookingNewAnswerDto booking = bookingService.add(userId, itemId, newBooking);

        assertThat(booking.getItem().getId()).isEqualTo(item1.getId());
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(booking.getBooker().getName()).isEqualTo(user.getName());
    }

    @Test
    void add_shouldThrowValidationException() {
        Booking wrongTimeBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .item(null).booker(null)
                .status(null)
                .build();

        Booking wrongTimeBooking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(2))
                .item(null).booker(null)
                .status(null)
                .build();

        Booking wrongTimeBooking2 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().minusDays(2))
                .item(null).booker(null)
                .status(null)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item1));

        assertThatThrownBy(() -> bookingService.add(userId, itemId, wrongTimeBooking))
                .isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> bookingService.add(userId, itemId, wrongTimeBooking1))
                .isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> bookingService.add(userId, itemId, wrongTimeBooking2))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void add_shouldThrowEntityNotFoundExceptionIfNotItem() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookingService.add(userId, itemId, newBooking))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void add_shouldThrowEntityNotFoundExceptionIfNotUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookingService.add(userId, itemId, newBooking))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void add_shouldThrowItemNotAvailableException() {
        item1.setAvailable(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item1));

        assertThatThrownBy(() -> bookingService.add(userId, itemId, newBooking))
                .isInstanceOf(ItemNotAvailableException.class);
    }

    @Test
    void add_shouldThrowFailIdException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.add(userId, itemId, newBooking))
                .isInstanceOf(FailIdException.class);
    }

    @Test
    void getByBookingId_shouldReturnBooking() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThat(bookingService.getByBookingId(bookingId, userId)).isEqualTo(booking);
    }

    @Test
    void getByBookingId_shouldThrowEntityNotFoundException() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getByBookingId(bookingId, userId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getByBookingId_shouldThrowFailIdException() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getByBookingId(bookingId, 4L))
                .isInstanceOf(FailIdException.class);
    }

    @Test
    void approve_shouldReturnBookingWithApprovedStatus() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        assertThat((bookingService.approved(bookingId, userId, true).getStatus())).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void approve_shouldThrowValidationException() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.approved(bookingId, userId, true))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void approve_shouldThrowFailIdException() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.approved(bookingId, 4L, true))
                .isInstanceOf(FailIdException.class);
    }

    @Test
    void getAllBookingByBookerId_shouldCallFindAllByBookerIdOrderByStartDesc() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        bookingService.getAllBookingByBookerId(userId, "ALL", null);
        verify(bookingRepository).findAllByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getAllBookingByBookerId_shouldCallFindAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        bookingService.getAllBookingByBookerId(userId, "CURRENT", null);
        verify(bookingRepository)
                .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any());
    }

    @Test
    void getAllBookingByBookerId_shouldCallFindAllByBookerIdAndEndBeforeOrderByStartDesc() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        bookingService.getAllBookingByBookerId(userId, "PAST", null);
        verify(bookingRepository)
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getAllBookingByBookerId_shouldCallFindAllByBookerIdAndStartAfterOrderByStartDesc() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        bookingService.getAllBookingByBookerId(userId, "FUTURE", null);
        verify(bookingRepository)
                .findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getAllBookingByBookerId_shouldCallFindAllByBookerIdAndStatusOrderByStartDescWithStatusWaiting() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        bookingService.getAllBookingByBookerId(userId, "WAITING", null);
        verify(bookingRepository)
                .findAllByBookerIdAndStatusOrderByStartDesc(1, BookingStatus.WAITING, null);
    }

    @Test
    void getAllBookingByBookerId_shouldCallFindAllByBookerIdAndStatusOrderByStartDescWithStatusRejected() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        bookingService.getAllBookingByBookerId(userId, "REJECTED", null);
        verify(bookingRepository)
                .findAllByBookerIdAndStatusOrderByStartDesc(1, BookingStatus.REJECTED, null);
    }

    @Test
    void getAllBookingByBookerId_shouldThrowUnsupportedStatusException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        assertThatThrownBy(() -> bookingService.getAllBookingByBookerId(userId, "Example", null))
                .isInstanceOf(StatusException.class);
    }

    @Test
    void getAllBookingByBookerId_shouldThrowEntityNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookingService.getAllBookingByBookerId(userId, "Example", null))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getAllBookingByOwnerId_shouldCallFindAllByItemOwnerIdOrderByStartDesc() {
        when(itemRepository.findAllByOwnerId(userId)).thenReturn(List.of(item));
        bookingService.getAllBookingByOwnerId(userId, "ALL", null);
        verify(bookingRepository).findAllByItemOwnerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getAllBookingByOwnerId_shouldCallFindAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        when(itemRepository.findAllByOwnerId(userId)).thenReturn(List.of(item));
        bookingService.getAllBookingByOwnerId(userId, "CURRENT", null);
        verify(bookingRepository)
                .findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any());
    }

    @Test
    void getAllBookingByOwnerId_shouldCallFindAllByItemOwnerIdAndEndBeforeOrderByStartDesc() {
        when(itemRepository.findAllByOwnerId(userId)).thenReturn(List.of(item));
        bookingService.getAllBookingByOwnerId(userId, "PAST", null);
        verify(bookingRepository)
                .findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getAllBookingByOwnerId_shouldCallFindAllByItemOwnerIdAndStartAfterOrderByStartDesc() {
        when(itemRepository.findAllByOwnerId(userId)).thenReturn(List.of(item));
        bookingService.getAllBookingByOwnerId(userId, "FUTURE", null);
        verify(bookingRepository)
                .findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getAllBookingByOwnerId_shouldCallFindAllByItemOwnerIdAndStatusOrderByStartDescWithStatusWaiting() {
        when(itemRepository.findAllByOwnerId(userId)).thenReturn(List.of(item));
        bookingService.getAllBookingByOwnerId(userId, "WAITING", null);
        verify(bookingRepository)
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(1, BookingStatus.WAITING, null);
    }

    @Test
    void getAllBookingByOwnerId_shouldCallFindAllByItemOwnerIdAndStatusOrderByStartDescWithStatusRejected() {
        when(itemRepository.findAllByOwnerId(userId)).thenReturn(List.of(item));
        bookingService.getAllBookingByOwnerId(userId, "REJECTED", null);
        verify(bookingRepository)
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(1, BookingStatus.REJECTED, null);
    }

    @Test
    void getAllBookingByOwnerId_shouldThrowUnsupportedStatusException() {
        when(itemRepository.findAllByOwnerId(userId)).thenReturn(List.of(item));
        assertThatThrownBy(() -> bookingService.getAllBookingByOwnerId(userId, "Example", null))
                .isInstanceOf(StatusException.class);
    }

    @Test
    void getAllBookingByOwnerId_shouldThrowFailIdException() {
        when(itemRepository.findAllByOwnerId(userId)).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> bookingService.getAllBookingByOwnerId(userId, "Example", null))
                .isInstanceOf(FailIdException.class);
    }


}
