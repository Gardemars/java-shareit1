package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public UserDto add(@Valid @RequestBody UserDto userDto) {
        return userMapper.userToUserDto(userService.add(userMapper.userDtoToUser(userDto)));
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id, @Valid @RequestBody UpdateUserDto userDto) {
        return userMapper.userToUserDto(userService.update(id, userMapper.updateUserDtoToUser(userDto)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        userService.remove(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userMapper.userListToUserDtoList(userService.getAll());
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) {
        return userMapper.userToUserDto(userService.getByUserId(id));
    }
}
