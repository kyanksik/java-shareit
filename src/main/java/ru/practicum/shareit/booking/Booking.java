package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * Бронирование вещи на определённые даты.
 */
@Data
public class Booking {
    private Long id;
    private LocalDateTime start; // дата начала бронирования
    private LocalDateTime end; // дата окончания бронирования
    private Item item; // бронируемая вещь
    private User booker; // пользователь, который бронирует
    private BookingStatus status;
}
