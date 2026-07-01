package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * Запрос вещи, которой ещё нет в сервисе.
 */
@Data
public class ItemRequest {
    private Long id;
    private String description; // что именно ищет пользователь
    private User requestor; // пользователь, создавший запрос
    private LocalDateTime created;
}
