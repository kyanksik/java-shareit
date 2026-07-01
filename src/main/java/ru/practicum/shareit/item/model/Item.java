package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * Вещь, которой владелец готов поделиться.
 */
@Data
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available; // доступна ли вещь для аренды
    private User owner; // владелец вещи
    private ItemRequest request; // запрос, в ответ на который создана вещь
}
