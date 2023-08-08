package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemDto {
    private Long id;
    @NotBlank(message = "Нет названия вещи")
    private String name;
    @NotBlank(message = "Нет описания вещи")
    private String description;
    @NotNull(message = "Нет статуса аренды")
    private Boolean available;
    private Long ownerId;
    private Long requestId;
}
