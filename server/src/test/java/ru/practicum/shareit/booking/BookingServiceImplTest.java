package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    private long ownerId;
    private long bookerId;
    private long itemId;

    @BeforeEach
    void setUp() {
        ownerId = createUser("Owner", "owner@ex.com");
        bookerId = createUser("Booker", "booker@ex.com");
        itemId = createItem(ownerId, true);
    }

    private long createUser(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return userService.create(dto).getId();
    }

    private long createItem(long userId, boolean available) {
        ItemDto dto = new ItemDto();
        dto.setName("Item");
        dto.setDescription("desc");
        dto.setAvailable(available);
        return itemService.create(userId, dto).getId();
    }

    private BookingDto book(long userId, long item, LocalDateTime start, LocalDateTime end) {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(item);
        dto.setStart(start);
        dto.setEnd(end);
        return bookingService.create(userId, dto);
    }

    @Test
    void createBookingWaiting() {
        BookingDto booking = book(bookerId, itemId,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(booking.getItem().getId()).isEqualTo(itemId);
        assertThat(booking.getBooker().getId()).isEqualTo(bookerId);
    }

    @Test
    void createBookingUnavailableItemThrowsValidation() {
        long unavailable = createItem(ownerId, false);
        assertThrows(ValidationException.class, () -> book(bookerId, unavailable,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));
    }

    @Test
    void createBookingOwnItemThrowsNotFound() {
        assertThrows(NotFoundException.class, () -> book(ownerId, itemId,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));
    }

    @Test
    void createBookingEndBeforeStartThrowsValidation() {
        assertThrows(ValidationException.class, () -> book(bookerId, itemId,
                LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1)));
    }

    @Test
    void createBookingUnknownItemThrowsNotFound() {
        assertThrows(NotFoundException.class, () -> book(bookerId, 9999L,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));
    }

    @Test
    void approveByOwner() {
        BookingDto booking = book(bookerId, itemId,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingDto approved = bookingService.approve(ownerId, booking.getId(), true);
        assertThat(approved.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void rejectByOwner() {
        BookingDto booking = book(bookerId, itemId,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingDto rejected = bookingService.approve(ownerId, booking.getId(), false);
        assertThat(rejected.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void approveByNotOwnerThrowsForbidden() {
        BookingDto booking = book(bookerId, itemId,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        assertThrows(ForbiddenException.class, () -> bookingService.approve(bookerId, booking.getId(), true));
    }

    @Test
    void approveAlreadyProcessedThrowsValidation() {
        BookingDto booking = book(bookerId, itemId,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        bookingService.approve(ownerId, booking.getId(), true);
        assertThrows(ValidationException.class, () -> bookingService.approve(ownerId, booking.getId(), true));
    }

    @Test
    void findByIdByBookerAndOwner() {
        BookingDto booking = book(bookerId, itemId,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        assertThat(bookingService.findById(bookerId, booking.getId()).getId()).isEqualTo(booking.getId());
        assertThat(bookingService.findById(ownerId, booking.getId()).getId()).isEqualTo(booking.getId());
    }

    @Test
    void findByIdByStrangerThrowsNotFound() {
        long strangerId = createUser("Stranger", "str@ex.com");
        BookingDto booking = book(bookerId, itemId,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        assertThrows(NotFoundException.class, () -> bookingService.findById(strangerId, booking.getId()));
    }

    @Test
    void findByIdUnknownThrowsNotFound() {
        assertThrows(NotFoundException.class, () -> bookingService.findById(bookerId, 9999L));
    }

    @Test
    void findByBookerAllStates() {
        book(bookerId, itemId, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
        book(bookerId, itemId, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        book(bookerId, itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        assertThat(bookingService.findByBooker(bookerId, BookingState.ALL)).hasSize(3);
        assertThat(bookingService.findByBooker(bookerId, BookingState.PAST)).hasSize(1);
        assertThat(bookingService.findByBooker(bookerId, BookingState.CURRENT)).hasSize(1);
        assertThat(bookingService.findByBooker(bookerId, BookingState.FUTURE)).hasSize(1);
        assertThat(bookingService.findByBooker(bookerId, BookingState.WAITING)).hasSize(3);
        assertThat(bookingService.findByBooker(bookerId, BookingState.REJECTED)).isEmpty();
    }

    @Test
    void findByOwnerAllStates() {
        BookingDto future = book(bookerId, itemId,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        bookingService.approve(ownerId, future.getId(), false);

        assertThat(bookingService.findByOwner(ownerId, BookingState.ALL)).hasSize(1);
        assertThat(bookingService.findByOwner(ownerId, BookingState.FUTURE)).hasSize(1);
        assertThat(bookingService.findByOwner(ownerId, BookingState.REJECTED)).hasSize(1);
        assertThat(bookingService.findByOwner(ownerId, BookingState.WAITING)).isEmpty();
        assertThat(bookingService.findByOwner(ownerId, BookingState.PAST)).isEmpty();
        assertThat(bookingService.findByOwner(ownerId, BookingState.CURRENT)).isEmpty();
    }

    @Test
    void unknownStateThrowsValidation() {
        assertThrows(ValidationException.class, () -> BookingState.from("BOGUS"));
    }
}
