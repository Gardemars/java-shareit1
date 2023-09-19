package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.core.exception.EntityNotFoundException;
import ru.practicum.shareit.core.exception.FailIdException;
import ru.practicum.shareit.core.exception.ValidationException;
import ru.practicum.shareit.core.mapper.PaginationMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServerTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Long userId;
    private Long itemId;
    private Long requestId;
    private Long bookingId;

    private User user;
    private User user1;
    private Item item;
    private Item item1;
    private Request request;
    private Booking booking;
    private CommentDto comment;
    private BookingShortDto bookingShortDto;

    @BeforeEach
    void setUp() {
        userId = 1L;
        itemId = 1L;
        requestId = 1L;
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

        request = new Request(
                requestId,
                "description",
                LocalDateTime.now(),
                user);

        booking = new Booking(
                bookingId,
                LocalDateTime.now(),
                LocalDateTime.now(),
                item,
                user1,
                BookingStatus.APPROVED);

        comment = new CommentDto(
                1L,
                "text",
                "name",
                LocalDateTime.now());

        bookingShortDto = new BookingShortDto(1L, 1L);
    }

    @Test
    void addShouldThrowEntityNotFoundExceptionIfUserIsNotExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> itemService.add(userId, null, item)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void addShouldThrowEntityNotFoundExceptionIfRequestIsNotExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.add(userId, requestId, item)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void addShouldCreateItemWithRequest() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Item newItem = itemService.add(userId, requestId, item);

        assertThat(newItem.getRequest()).isEqualTo(request);
        assertThat(newItem.getOwner()).isEqualTo(user);
    }

    @Test
    void updateShouldThrowFailIdException() {
        Item newItem = Item.builder()
                .id(1L)
                .name("newName")
                .description("newDesc")
                .available(true)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item1));
        when(commentMapper.commentListToCommentDtoList(any())).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> itemService.update(itemId, userId, newItem)).isInstanceOf(FailIdException.class);
    }

    @Test
    void updateShouldThrowEntityNotFoundException() {
        Item newItem = Item.builder()
                .id(1L)
                .name("newName")
                .description("newDesc")
                .available(true)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.update(itemId, userId, newItem)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateShouldUpdateAll() {
        Item newItem = Item.builder()
                .id(1L)
                .name("newName")
                .description("newDesc")
                .available(true)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentMapper.commentListToCommentDtoList(any())).thenReturn(Collections.emptyList());
        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Item updateItem = itemService.update(itemId, userId, newItem);

        assertThat(updateItem.getDescription()).isEqualTo(newItem.getDescription());
        assertThat(updateItem.getName()).isEqualTo(newItem.getName());
        assertThat(updateItem.getAvailable()).isEqualTo(newItem.getAvailable());
    }

    @Test
    void updateShouldUpdateName() {
        Item newItem = Item.builder()
                .id(1L)
                .name("newName")
                .description(null)
                .available(null)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentMapper.commentListToCommentDtoList(any())).thenReturn(Collections.emptyList());
        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Item updateItem = itemService.update(itemId, userId, newItem);

        assertThat(updateItem.getDescription()).isEqualTo(item.getDescription());
        assertThat(updateItem.getName()).isEqualTo(newItem.getName());
        assertThat(updateItem.getAvailable()).isEqualTo(item.getAvailable());
    }

    @Test
    void updateShouldUpdateDescription() {
        Item newItem = Item.builder()
                .id(1L)
                .name(null)
                .description("description")
                .available(null)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentMapper.commentListToCommentDtoList(any())).thenReturn(Collections.emptyList());
        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Item updateItem = itemService.update(itemId, userId, newItem);

        assertThat(updateItem.getDescription()).isEqualTo(newItem.getDescription());
    }

    @Test
    void updateShouldUpdateAvailable() {
        Item newItem = Item.builder()
                .id(1L)
                .name(null)
                .description(null)
                .available(false)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentMapper.commentListToCommentDtoList(any())).thenReturn(Collections.emptyList());
        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Item updateItem = itemService.update(itemId, userId, newItem);

        assertThat(updateItem.getAvailable()).isEqualTo(newItem.getAvailable());
    }

    @Test
    void getByItemIdShouldReturnItem() {
        List<Booking> bookings = List.of(booking);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        when(bookingRepository.findAllByItemIdAndStatusOrderByStartAsc(anyLong(), any())).thenReturn(bookings);
        when(bookingMapper.bookingToBookingShortDto(any())).thenReturn(bookingShortDto);
        when(commentMapper.commentListToCommentDtoList(any())).thenReturn(List.of(comment));


        Item item2 = itemService.getByItemId(itemId, userId);

        assertThat(item2.getComments().get(0)).isEqualTo(comment);
        assertThat(item2.getNextBooking().getId()).isEqualTo(bookingShortDto.getId());
    }

    @Test
    void getByItemIdShouldThrowEntityNotFoundException() {
        List<Booking> bookings = List.of(booking);

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getByItemId(itemId, userId)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getByOwnerIdShouldReturnItem() {
        when(itemRepository.findAllByOwnerIdOrderById(userId, PaginationMapper.toMakePage(1, 1))).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemIdAndStatusOrderByStartAsc(itemId, BookingStatus.APPROVED)).thenReturn(List.of(booking));
        when(bookingMapper.bookingToBookingShortDto(any())).thenReturn(bookingShortDto);
        when(commentMapper.commentListToCommentDtoList(any())).thenReturn(List.of(comment));

        List<Item> itemList = itemService.getByOwnerId(userId, PaginationMapper.toMakePage(1, 1));

        assertThat(itemList.get(0).getComments().get(0)).isEqualTo(comment);
        assertThat(itemList.get(0).getNextBooking().getId()).isEqualTo(bookingShortDto.getId());
    }

    @Test
    void searchShouldReturnEmptyListIfTextIsBlank() {
        assertThat(itemService.search("", null)).isEmpty();
    }

    @Test
    void searchShouldReturnListOfItems() {
        List<Item> items = List.of(item, item1);

        when(itemRepository.findByText(anyString(), any())).thenReturn(items);
        assertThat(itemService.search("text", null)).isEqualTo(items);
    }

    @Test
    void commentShouldReturnNewComment() {
        Comment newComment = new Comment(1L, "newComment", null, null, null);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(booking));

        when(commentRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Comment comment = itemService.addComment(itemId, userId, newComment);
        assertThat(comment.getAuthor().getName()).isEqualTo(user.getName());
        assertThat(comment.getItem().getId()).isEqualTo(item.getId());
        assertThat(comment.getId()).isEqualTo(newComment.getId());
        assertThat(comment.getText()).isEqualTo(newComment.getText());
    }

    @Test
    void commentShouldThrowValidationException() {
        Comment newComment = new Comment(1L, "newComment", null, null, null);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any()))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> itemService.addComment(itemId, userId, newComment))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void commentShouldThrowEntityNotFoundExceptionIfNotItem() {
        Comment newComment = new Comment(1L, "newComment", null, null, null);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.addComment(itemId, userId, newComment))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void commentShouldThrowEntityNotFoundExceptionIfNotUser() {
        Comment newComment = new Comment(1L, "newComment", null, null, null);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.addComment(itemId, userId, newComment))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
