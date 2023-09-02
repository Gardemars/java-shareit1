package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.RequestUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User requestUserDtoToUser(RequestUserDto userDto);

    UserDto userToUserDto(User user);

    User updateUserDtoToUser(UpdateUserDto updateUserDto);

    List<UserDto> userListToUserDtoList(List<User> userList);
}