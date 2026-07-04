package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.validation.Create;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;

    @NotNull(groups = Create.class, message = "Дата начала бронирования обязательна")
    @FutureOrPresent(groups = Create.class, message = "Дата начала бронирования не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(groups = Create.class, message = "Дата окончания бронирования обязательна")
    private LocalDateTime end;

    @NotNull(groups = Create.class, message = "Не указана бронируемая вещь")
    private Long itemId;

    private BookingStatus status;
}
