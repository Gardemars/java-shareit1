package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
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
    @FutureOrPresent(message = "Время начала бронирования должно быть в будущем")
    private LocalDateTime start;
    @NotNull(message = "Необходимо указать время окончания бронирования")
    @Future(message = "Время окончания бронирования должно быть в будущем")
    private LocalDateTime end;
}
