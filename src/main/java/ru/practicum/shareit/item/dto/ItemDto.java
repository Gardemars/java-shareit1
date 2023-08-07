package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemDto {
    Long id;
    @NotBlank(message = "Нет названия вещи")
    String name;
    @NotBlank(message = "Нет описания вещи")
    String description;
    @NotNull(message = "Нет статуса аренды")
    Boolean available;
    Long ownerId;
    Long requestId;
}
