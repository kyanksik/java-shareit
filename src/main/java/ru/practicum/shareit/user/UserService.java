package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

/**
 * Операции над пользователями сервиса.
 */
public interface UserService {

    /**
     * Возвращает всех пользователей.
     */
    Collection<UserDto> findAll();

    /**
     * Возвращает пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     */
    UserDto findById(long id);

    /**
     * Создаёт нового пользователя.
     */
    UserDto create(UserDto user);

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param id идентификатор обновляемого пользователя
     */
    UserDto update(long id, UserDto user);

    /**
     * Удаляет пользователя по идентификатору.
     */
    void delete(long id);
}
