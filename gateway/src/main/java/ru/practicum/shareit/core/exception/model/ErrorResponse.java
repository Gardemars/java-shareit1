package ru.practicum.shareit.core.exception.model;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class ErrorResponse {
    private final String error;
}
