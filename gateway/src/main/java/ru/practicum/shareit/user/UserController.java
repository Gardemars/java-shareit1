package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.RequestUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import javax.validation.Valid;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody @Valid RequestUserDto dto) {
        log.info("Запрос на создание пользователя - сервер gateway");
        return userClient.add(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @Valid @RequestBody UpdateUserDto dto) {
        log.info("Запрос на обновление пользователя - сервер gateway");
        return userClient.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> remove(@PathVariable Long id) {
        log.info("Запрос на удаление пользователя - сервер gateway");
        return userClient.remove(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Запрос на выдачу всех пользователей - сервер gateway");
        return userClient.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        log.info("Запрос на выдачу пользователя - сервер gateway");
        return userClient.getByUserId(id);
    }
}
