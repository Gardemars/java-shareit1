package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item addItem(long userId, List<Item> item);

    Item updateItem(long userId, List<Item> items);

    Item getItem(long userId, long id);

    List<Item> getAllItemsByIdOwner(long userId);

    List<Item> searchItem(String text);

}
