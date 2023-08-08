package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> getAllUser() {
        log.info("InMemoryUserStorage метод getAllUser возврат информации");
        return new LinkedList<>(users.values());
    }

    @Override
    public User getUser(long id) {
        checkExistId(id);
        log.info("InMemoryUserStorage метод getUser возврат информации с пользователем с id = {}", id);
        return users.get(id);
    }

    @Override
    public User updateUser(User user) {
        checkExistId(user.getId());
        users.put(user.getId(), user);
        log.info("InMemoryUserStorage метод updateUser обновлена информация пользователя = {}", user);
        return user;
    }

    @Override
    public User addUser(User user) {
        users.put(user.getId(), user);
        log.info("InMemoryUserStorage метод addUser добавлена информация по пользователю = {}", user);
        return user;
    }

    @Override
    public void deleteUser(long id) {
        checkExistId(id);
        log.info("InMemoryUserStorage метод deleteUser удалена информация по пользователю с id = {}", id);
        users.remove(id);
    }

    private void checkExistId(long id) {
        Set<Long> keySet = users.keySet();
        if (!(keySet.contains(id))) {
            throw new NotFoundException("Нет пользователя с таким id " + id);
        }
    }
}
