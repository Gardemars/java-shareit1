package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;

@Component
public class ItemIdGenerator {
    private static long id = 0;

    public long getId() {
        return ++id;
    }
}
