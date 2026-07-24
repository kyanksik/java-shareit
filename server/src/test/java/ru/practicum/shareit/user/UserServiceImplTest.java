package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    private UserDto dto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

    @Test
    void createAndFindById() {
        UserDto created = userService.create(dto("Alice", "alice@ex.com"));
        assertThat(created.getId()).isNotNull();

        UserDto found = userService.findById(created.getId());
        assertThat(found.getName()).isEqualTo("Alice");
        assertThat(found.getEmail()).isEqualTo("alice@ex.com");
    }

    @Test
    void findAllReturnsCreated() {
        userService.create(dto("A", "a@ex.com"));
        userService.create(dto("B", "b@ex.com"));
        Collection<UserDto> all = userService.findAll();
        assertThat(all).hasSize(2);
    }

    @Test
    void createWithDuplicateEmailThrowsConflict() {
        userService.create(dto("A", "dup@ex.com"));
        assertThrows(ConflictException.class, () -> userService.create(dto("B", "dup@ex.com")));
    }

    @Test
    void updateNameAndEmail() {
        UserDto created = userService.create(dto("Old", "old@ex.com"));
        UserDto updated = userService.update(created.getId(), dto("New", "new@ex.com"));
        assertThat(updated.getName()).isEqualTo("New");
        assertThat(updated.getEmail()).isEqualTo("new@ex.com");
    }

    @Test
    void updateWithSameEmailKeepsUser() {
        UserDto created = userService.create(dto("A", "same@ex.com"));
        UserDto patch = new UserDto();
        patch.setEmail("same@ex.com");
        UserDto updated = userService.update(created.getId(), patch);
        assertThat(updated.getEmail()).isEqualTo("same@ex.com");
    }

    @Test
    void updateWithBlankFieldsKeepsOldValues() {
        UserDto created = userService.create(dto("Name", "keep@ex.com"));
        UserDto patch = new UserDto();
        patch.setName(" ");
        patch.setEmail(" ");
        UserDto updated = userService.update(created.getId(), patch);
        assertThat(updated.getName()).isEqualTo("Name");
        assertThat(updated.getEmail()).isEqualTo("keep@ex.com");
    }

    @Test
    void updateEmailToExistingThrowsConflict() {
        userService.create(dto("A", "a2@ex.com"));
        UserDto b = userService.create(dto("B", "b2@ex.com"));
        assertThrows(ConflictException.class, () -> userService.update(b.getId(), dto("B", "a2@ex.com")));
    }

    @Test
    void deleteRemovesUser() {
        UserDto created = userService.create(dto("A", "del@ex.com"));
        userService.delete(created.getId());
        assertThrows(NotFoundException.class, () -> userService.findById(created.getId()));
    }

    @Test
    void findByIdUnknownThrowsNotFound() {
        assertThrows(NotFoundException.class, () -> userService.findById(9999L));
    }

    @Test
    void deleteUnknownThrowsNotFound() {
        assertThrows(NotFoundException.class, () -> userService.delete(9999L));
    }
}
