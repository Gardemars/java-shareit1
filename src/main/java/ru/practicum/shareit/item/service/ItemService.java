package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item add(Long userId, Item item);

    Item update(Long itemId, Long userId, Item item);

    void remove(Long itemId);

    Item getByItemId(Long itemId, Long userId);

    List<Item> getByOwnerId(Long ownerId);

    List<Item> search(String text);

    Comment addComment(Long itemId, Long userId, Comment comment);
}
