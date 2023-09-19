package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.RequestUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public UserDto add(@RequestBody RequestUserDto userDto) {
        log.info("Запрос на создание пользователя");
        return userMapper.userToUserDto(userService.add(userMapper.requestUserDtoToUser(userDto)));
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UpdateUserDto userDto) {
        log.info("Запрос на обновление пользователя");
        return userMapper.userToUserDto(userService.update(id, userMapper.updateUserDtoToUser(userDto)));
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable Long id) {
        log.info("Запрос на удаление пользователя");
        userService.remove(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Запрос на выдачу всех пользователей");
        return userMapper.userListToUserDtoList(userService.getAll());
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        log.info("Запрос на выдачу пользователя");
        return userMapper.userToUserDto(userService.getByUserId(id));
    }
}
