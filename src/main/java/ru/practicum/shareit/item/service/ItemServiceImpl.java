package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.constant.State;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.FailIdException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Transactional
    @Override
    public Item add(Long userId, Item item) {
        log.info("Добавление вещи");
        item.setOwner(userService.getByUserId(userId));
        return itemRepository.save(item);
    }

    @Transactional
    @Override
    public Item update(Long itemId, Long userId, Item item) {
        log.info(String.format("Обновление вещи c id = %d", itemId));

        User user = userService.getByUserId(userId);
        Item updateItem = this.getByItemId(itemId, userId);
        User owner = updateItem.getOwner();

        if (!user.equals(owner)) {
            throw new FailIdException(
                    String.format("Вы не можете обновлять информацию по предмету с id = %d", itemId));
        }

        if (item.getName() != null) {
            updateItem.setName(item.getName());
        }

        if (item.getDescription() != null) {
            updateItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            updateItem.setAvailable(item.getAvailable());
        }

        return itemRepository.save(updateItem);
    }

    @Transactional
    @Override
    public void remove(Long itemId) {
        log.info(String.format("Удаление вещи с id = %d", itemId));
        itemRepository.deleteById(itemId);
    }

    @Override
    public Item getByItemId(Long itemId, Long userId) {
        log.info(String.format("Выдача вещи с id = %d", itemId));

        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Предмет с id = %d не найден в базе", itemId)));


        if (Objects.equals(item.getOwner().getId(), userId)) {
            List<Booking> bookings = bookingRepository.findAllByItemIdAndStatusOrderByStartAsc(itemId, BookingStatus.APPROVED);

            item.setNextBooking(bookingMapper.bookingToBookingShortDto(getNextBooking(bookings)));
            item.setLastBooking(bookingMapper.bookingToBookingShortDto(getLastBooking(bookings)));
        }

        item.setComments(commentMapper.commentListToCommentDtoList(commentRepository.findAllByItemId(itemId)));

        return item;
    }

    @Override
    public List<Item> getByOwnerId(Long ownerId) {
        log.info(String.format("Выдача вещей владельца с id = %d", ownerId));
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);

        return items.stream().peek(item -> {
            List<Booking> bookings = bookingRepository.findAllByItemIdAndStatusOrderByStartAsc(item.getId(), BookingStatus.APPROVED);

            item.setNextBooking(bookingMapper.bookingToBookingShortDto(getNextBooking(bookings)));
            item.setLastBooking(bookingMapper.bookingToBookingShortDto(getLastBooking(bookings)));
            item.setComments(commentMapper.commentListToCommentDtoList(commentRepository.findAllByItemId(item.getId())));

        }).collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        log.info(String.format("Выдача вещи по поиску строки = %s", text.toLowerCase()));
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findByText("%" + text.toLowerCase() + "%");
    }

    @Transactional
    @Override
    public Comment addComment(Long itemId, Long userId, Comment comment) {

        Item item = this.getByItemId(itemId, userId);
        User user = userService.getByUserId(userId);
        List<Booking> bookings = bookingService.getAllBookingByBookerId(userId, String.valueOf(State.PAST));

        List<Booking> bookingsForItem = bookings.stream().map(booking -> {
            boolean isBooking = Objects.equals(booking.getItem().getId(), item.getId());
            if (isBooking) {
                return booking;
            }
            return null;
        }).collect(Collectors.toList());

        if (bookingsForItem.isEmpty()) {
            throw new ValidationException(
                    String.format("Оставлять комментарий по предмету с id = %d можно только после взятия в аренду", itemId));
        }

        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    private Booking getNextBooking(List<Booking> bookings) {
        List<Booking> filteredBookings = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());

        return filteredBookings.isEmpty() ? null : filteredBookings.get(0);
    }

    private Booking getLastBooking(List<Booking> bookings) {
        List<Booking> filteredBookings = bookings.stream()
                .filter(booking -> (booking.getEnd().isAfter(LocalDateTime.now())
                        && booking.getStart().isBefore(LocalDateTime.now())) || booking.getEnd().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());

        return filteredBookings.isEmpty() ? null : filteredBookings.get(filteredBookings.size() - 1);
    }
}
