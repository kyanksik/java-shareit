package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {

    Collection<User> findAll();

    Optional<User> findById(long id);

    User save(User user);

    void deleteById(long id);

    boolean existsByEmail(String email, Long excludeUserId);
}
