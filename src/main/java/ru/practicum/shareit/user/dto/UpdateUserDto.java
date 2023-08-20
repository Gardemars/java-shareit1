package ru.practicum.shareit.user.dto;

import lombok.Value;

import javax.validation.constraints.Email;

@Value
public class UpdateUserDto {
    String name;
    @Email(message = "Введите правильный email")
    String email;
}
