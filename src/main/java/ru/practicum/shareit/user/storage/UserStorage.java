package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getAllUser();

    User getUser(long id);

    User updateUser(User user);

    User addUser(User user);

    void deleteUser(long id);
}