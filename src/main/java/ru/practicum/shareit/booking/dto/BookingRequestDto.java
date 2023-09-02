package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookingRequestDto {
    @NotNull(message = "Необходимо указать id предмета")
    private Long itemId;

    @NotNull(message = "Необходимо указать время начала бронирования")
    private LocalDateTime start;

    @NotNull(message = "Необходимо указать время окончания бронирования")
    private LocalDateTime end;
}