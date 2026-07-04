package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 1;

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(nextId++);
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteById(long id) {
        users.remove(id);
    }

    @Override
    public boolean existsByEmail(String email, Long excludeUserId) {
        return users.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email)
                        && !Objects.equals(u.getId(), excludeUserId));
    }
}
