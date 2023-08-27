package ru.practicum.shareit.booking.dto;

import lombok.Value;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
public class BookingRequestDto {
    @NotNull(message = "Необходимо указать id предмета")
    Long itemId;

    @NotNull(message = "Необходимо указать время начала бронирования")
    LocalDateTime start;

    @NotNull(message = "Необходимо указать время окончания бронирования")
    LocalDateTime end;
}