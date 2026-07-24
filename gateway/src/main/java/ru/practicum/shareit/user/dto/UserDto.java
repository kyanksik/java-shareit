package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

@Data
public class UserDto {
    private Long id;
    private String name;

    @NotBlank(groups = Create.class, message = "Электронная почта не может быть пустой")
    @Email(groups = {Create.class, Update.class}, message = "Некорректный формат электронной почты")
    private String email;
}
