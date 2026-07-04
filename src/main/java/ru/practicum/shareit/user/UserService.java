package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    Collection<UserDto> findAll();

    UserDto findById(long id);

    UserDto create(UserDto user);

    UserDto update(long id, UserDto user);

    void delete(long id);
}
