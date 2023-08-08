package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Slf4j
@Repository
public class InMemoryItemStorage implements ItemStorage {
    private static final Map<Long, List<Item>> itemMap = new HashMap<>();

    @Override
    public Item addItem(long userId, List<Item> items) {
        itemMap.put(userId, items);
        log.info("InMemoryItemStorage метод addItem, user id = {} и items = {} добавлены", userId, items);
        return items.get(items.size() - 1);
    }

    @Override
    public Item updateItem(long userId, List<Item> items) {
        itemMap.put(userId, items);
        log.info("InMemoryItemStorage метод updateItem, user id = {} и items = {} обновлены", userId, items);
        return items.get(items.size() - 1);
    }

    @Override
    public Item getItem(long userId, long id) {
        log.info("InMemoryItemStorage метод getItem, userId = {} с id = {} переданы для получения", userId, id);
        return checkExistId(id);
    }

    @Override
    public List<Item> getAllItemsByIdOwner(long userId) {
        Set<Long> keySet = itemMap.keySet();
        if (keySet.isEmpty()) {
            return new LinkedList<>();
        }
        List<Item> items = itemMap.get(userId);
        log.info("InMemoryItemStorage метод getAllItemsByIdOwner, userId = {}  передан для получения", userId);
        return Objects.requireNonNullElseGet(items, LinkedList::new);
    }

    @Override
    public List<Item> searchItem(String text) {
        List<Item> foundItems = new LinkedList<>();
        Set<Long> keySet = itemMap.keySet();
        for (Long key : keySet) {
            List<Item> items = getAllItemsByIdOwner(key);
            if (!(items.isEmpty())) {
                for (Item item : items) {
                    if ((item.getName().toLowerCase().contains(text.toLowerCase())) ||
                            (item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                                    (item.getAvailable().equals(true))) {
                        foundItems.add(item);
                    }
                }
            }
        }
        log.info("InMemoryItemStorage метод searchItem, text = {}  найден", text);
        return foundItems;
    }

    private Item checkExistId(long id) {
        Set<Long> keySet = itemMap.keySet();
        if (keySet.isEmpty()) {
            throw new NotFoundException("Список вещей пуст");
        }
        for (Long key : keySet) {
            List<Item> items = itemMap.get(key);
            for (Item item : items) {
                if (item.getId() == id) {
                    return item;
                }
            }
        }
        throw new NotFoundException("Нет вещи с таким id" + id);
    }
}
