package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item add(Long userId, Long requestId, Item item);

    Item update(Long itemId, Long userId, Item item);

    void remove(Long itemId);

    Item getByItemId(Long itemId, Long userId);

    List<Item> getByOwnerId(Long ownerId, Pageable pageable);

    List<Item> search(String text, Pageable pageable);

    Comment addComment(Long itemId, Long userId, Comment comment);
}
