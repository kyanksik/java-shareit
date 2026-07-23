package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Доступ к данным пользователей.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Ищет пользователя по электронной почте.
     */
    Optional<User> findByEmail(String email);
}
