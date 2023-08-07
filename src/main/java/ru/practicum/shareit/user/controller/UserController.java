package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        User user = userMapper.toUser(userDto);
        log.info("В UserController получен PostMapping запрос addUser");
        return userMapper.toUserDto(userService.addUser(user));
    }

    @PatchMapping(value = "/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        User user = userMapper.toUser(userDto);
        log.info("В UserController получен PatchMapping запрос updateUser");
        return userMapper.toUserDto(userService.updateUser(id, user));
    }

    @GetMapping(value = "/{id}")
    public UserDto getUser(@PathVariable Long id) {
        log.info("В UserController получен GetMapping запрос getUser");
        return userMapper.toUserDto(userService.getUser(id));
    }

    @DeleteMapping(value = "/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("В UserController получен DeleteMapping запрос deleteUser");
        userService.deleteUser(id);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("В UserController получен GetMapping запрос getAllUsers");
        return userService.getAllUsers()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
