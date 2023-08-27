package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public User add(User user) {
        log.info("Добавление пользователя");
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException exp) {
            throw new DuplicateEmailException(String.format(
                    "Пользователь с email = %s уже зарегистрирован", user.getEmail()));
        }
    }

    @Transactional
    @Override
    public User update(Long id, User user) {
        log.info(String.format("Обновление пользователя c id = %d", id));
        User updateUser = this.getByUserId(id);

        if (user.getEmail() != null) {
            updateUser.setEmail(user.getEmail());
        }

        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }

        try {
            return userRepository.save(updateUser);
        } catch (DataIntegrityViolationException exp) {
            throw new DuplicateEmailException(String.format(
                    "Пользователь с email = %s уже зарегистрирован", user.getEmail()));
        }
    }

    @Transactional
    @Override
    public void remove(Long id) {
        log.info(String.format("Удаление пользователя c id = %d", id));
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getAll() {
        log.info("Выдача всех пользователей");
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public User getByUserId(Long id) {
        log.info(String.format("Выдача пользователя c id = %d", id));
        return userRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException(String.format("Пользователь с id = %d не найден в базе", id)));
    }
}
