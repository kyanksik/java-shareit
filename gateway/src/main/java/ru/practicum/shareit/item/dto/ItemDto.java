package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.validation.Create;

@Data
public class ItemDto {
    private Long id;

    @NotBlank(groups = Create.class, message = "Название вещи не может быть пустым")
    private String name;

    @NotBlank(groups = Create.class, message = "Описание вещи не может быть пустым")
    private String description;

    @NotNull(groups = Create.class, message = "Статус доступности вещи обязателен")
    private Boolean available;

    private Long requestId;
}
