package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingCreateDto {

    @NotNull(message = "Не указана бронируемая вещь")
    private Long itemId;

    @NotNull(message = "Дата начала бронирования обязательна")
    @FutureOrPresent(message = "Дата начала бронирования не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования обязательна")
    @Future(message = "Дата окончания бронирования должна быть в будущем")
    private LocalDateTime end;
}
