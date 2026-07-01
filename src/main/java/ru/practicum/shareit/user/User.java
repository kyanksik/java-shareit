package ru.practicum.shareit.user;

import lombok.Data;

/**
 * Пользователь сервиса.
 */
@Data
public class User {
    private Long id;
    private String name;
    private String email;
}
