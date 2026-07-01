package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public UserDto findById(long id) {
        return UserMapper.toDto(getOrThrow(id));
    }

    @Override
    public UserDto create(UserDto dto) {
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new ValidationException("Электронная почта не может быть пустой");
        }
        checkEmailUnique(dto.getEmail(), null);
        User created = userRepository.save(UserMapper.toModel(dto));
        return UserMapper.toDto(created);
    }

    @Override
    public UserDto update(long id, UserDto dto) {
        User user = getOrThrow(id);
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            checkEmailUnique(dto.getEmail(), id);
            user.setEmail(dto.getEmail());
        }
        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }
        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public void delete(long id) {
        getOrThrow(id);
        userRepository.deleteById(id);
    }

    private User getOrThrow(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    private void checkEmailUnique(String email, Long excludeUserId) {
        if (userRepository.existsByEmail(email, excludeUserId)) {
            throw new ConflictException("Пользователь с email " + email + " уже существует");
        }
    }
}
