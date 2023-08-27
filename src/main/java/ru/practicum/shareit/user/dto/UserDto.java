package ru.practicum.shareit.user.dto;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
public class UserDto {
    Long id;
    @NotBlank(message = "Имя пользователя не может быть пустым")
    String name;
    @Email(message = "Введите правильный email")
    @NotBlank(message = "Email не может быть пустым")
    String email;
}
