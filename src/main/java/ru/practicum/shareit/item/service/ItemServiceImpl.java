package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.ItemIdGenerator;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private static final String EMPTY_STRING = "";
    private static final String SPACE_STRING = " ";
    private final ItemStorage itemStorage;
    private final UserService userService;
    private final ItemIdGenerator generator;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage,
                           UserService userService,
                           ItemIdGenerator generator) {
        this.itemStorage = itemStorage;
        this.userService = userService;
        this.generator = generator;
    }

    @Override
    public Item addItem(long userId, Item item) {
        userService.getUser(userId);
        item.setId(generator.getId());
        item.setOwner(userService.getUser(userId));
        List<Item> items = new LinkedList<>(itemStorage.getAllItemsByIdOwner(userId));
        items.add(item);
        log.info("ItemServiceImpl addItem - возрат информации из itemStorage");
        return itemStorage.addItem(userId, items);
    }

    @Override
    public Item updateItem(long userId, long itemId, Item item) {
        userService.getUser(userId);
        Item updateItem = itemStorage.getItem(userId, itemId);
        if (updateItem.getOwner().getId() != userId) {
            throw new ForbiddenException("Нет прав для изменения вещи");
        }
        List<Item> items = new LinkedList<>(itemStorage.getAllItemsByIdOwner(userId));
        items.remove(updateItem);
        String name = item.getName();
        String description = item.getDescription();
        Boolean available = item.getAvailable();
        if (name != null) {
            updateItem.setName(name);
        }
        if (description != null) {
            updateItem.setDescription(description);
        }
        if (available != null) {
            updateItem.setAvailable(available);
        }
        items.add(updateItem);
        log.info("ItemServiceImpl updateItem - возрат информации из itemStorage");
        return itemStorage.updateItem(userId, items);
    }

    @Override
    public Item getItem(long userId, long id) {
        userService.getUser(userId);
        log.info("ItemServiceImpl getItem - возрат информации из itemStorage");
        return itemStorage.getItem(userId, id);
    }

    @Override
    public List<Item> getAllItemsByIdOwner(long userId) {
        userService.getUser(userId);
        log.info("ItemServiceImpl getAllItemsByIdOwner - возрат информации из itemStorage");
        return itemStorage.getAllItemsByIdOwner(userId);
    }

    @Override
    public List<Item> searchItem(long userId, String text) {
        userService.getUser(userId);
        if (text.equals(EMPTY_STRING) || text.equals(SPACE_STRING)) {
            log.info("ItemServiceImpl searchItem - text.equals(EMPTY_STRING) || text.equals(SPACE_STRING)");
            return new LinkedList<>();
        }
        log.info("ItemServiceImpl searchItem - возрат информации из itemStorage");
        return itemStorage.searchItem(text);
    }
}
