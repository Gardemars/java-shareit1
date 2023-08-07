package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private static final Map<Long, User> userMap = new HashMap<>();

    @Override
    public List<User> getAllUser() {
        return new LinkedList<>(userMap.values());
    }

    @Override
    public User getUser(long id) {
        checkExistId(id);
        return userMap.get(id);
    }

    @Override
    public User updateUser(User user) {
        checkExistId(user.getId());
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User addUser(User user) {
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(long id) {
        checkExistId(id);
        userMap.remove(id);
    }

    private void checkExistId(long id) {
        Set<Long> keySet = userMap.keySet();
        if (!(keySet.contains(id))) {
            throw new NotFoundException("Нет пользователя с таким id " + id);
        }
    }
}
