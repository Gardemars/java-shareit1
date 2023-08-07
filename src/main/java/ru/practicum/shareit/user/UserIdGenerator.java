package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

@Component
public class UserIdGenerator {
    private static long id = 0;

    public long getId() {
        return ++id;
    }
}