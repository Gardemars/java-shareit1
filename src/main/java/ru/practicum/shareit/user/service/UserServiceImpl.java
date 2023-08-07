package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserValidationException;
import ru.practicum.shareit.user.UserIdGenerator;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserIdGenerator generator;
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserIdGenerator generator, UserStorage userStorage) {
        this.generator = generator;
        this.userStorage = userStorage;
    }

    @Override
    public User getUser(long id) {
        return userStorage.getUser(id);
    }

    @Override
    public User addUser(User user) {
        List<User> userList = userStorage.getAllUser();
        for (User userValid : userList) {
            if (user.getEmail().equals(userValid.getEmail())) {
                throw new UserValidationException("Пользователь с такой почтой уже есть" + user.getEmail());
            }
        }
        user.setId(generator.getId());
        return userStorage.addUser(user);
    }

    @Override
    public User updateUser(long id, User user) {
        User userUpdate = userStorage.getUser(id);
        String name = user.getName();
        String email = user.getEmail();
        if (name != null) {
            userUpdate.setName(user.getName());
        }
        if (email != null) {
            List<User> userList = userStorage.getAllUser();
            if (!(userList.isEmpty())) {
                for (User userValid : userList) {
                    if (user.getEmail().equals(userValid.getEmail()) && id != userValid.getId()) {
                        throw new UserValidationException("Пользователь с такой почтой уже есть " + user.getEmail());
                    }
                }
            }
            userUpdate.setEmail(user.getEmail());
        }
        return userStorage.updateUser(userUpdate);
    }

    @Override
    public void deleteUser(long id) {
        userStorage.deleteUser(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.getAllUser();
    }
}