package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {
    Long id;
    @NotBlank(message = "Нет имени пользователя")
    String name;
    @NotBlank(message = "Нет email пользователя")
    @Email
    String email;
}