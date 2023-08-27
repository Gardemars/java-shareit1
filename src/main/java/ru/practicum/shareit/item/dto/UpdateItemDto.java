package ru.practicum.shareit.item.dto;

import lombok.Value;


@Value
public class UpdateItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
}
