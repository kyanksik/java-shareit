package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemRequestService requestService;

    private long ownerId;
    private long bookerId;

    @BeforeEach
    void setUp() {
        ownerId = createUser("Owner", "owner@ex.com");
        bookerId = createUser("Booker", "booker@ex.com");
    }

    private long createUser(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return userService.create(dto).getId();
    }

    private ItemDto item(String name, boolean available) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription("desc");
        dto.setAvailable(available);
        return dto;
    }

    @Test
    void createItem() {
        ItemDto created = itemService.create(ownerId, item("Drill", true));
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Drill");
    }

    @Test
    void createItemWithRequestId() {
        ItemRequestDto req = new ItemRequestDto();
        req.setDescription("need drill");
        long requestId = requestService.create(bookerId, req).getId();

        ItemDto dto = item("Drill", true);
        dto.setRequestId(requestId);
        ItemDto created = itemService.create(ownerId, dto);
        assertThat(created.getRequestId()).isEqualTo(requestId);
    }

    @Test
    void createItemWithUnknownRequestThrowsNotFound() {
        ItemDto dto = item("Drill", true);
        dto.setRequestId(9999L);
        assertThrows(NotFoundException.class, () -> itemService.create(ownerId, dto));
    }

    @Test
    void createForUnknownUserThrowsNotFound() {
        assertThrows(NotFoundException.class, () -> itemService.create(9999L, item("X", true)));
    }

    @Test
    void updateChangesFields() {
        long itemId = itemService.create(ownerId, item("Old", true)).getId();
        ItemDto patch = new ItemDto();
        patch.setName("New");
        patch.setDescription("newdesc");
        patch.setAvailable(false);
        ItemDto updated = itemService.update(ownerId, itemId, patch);
        assertThat(updated.getName()).isEqualTo("New");
        assertThat(updated.getDescription()).isEqualTo("newdesc");
        assertThat(updated.getAvailable()).isFalse();
    }

    @Test
    void updateByNotOwnerThrowsNotFound() {
        long itemId = itemService.create(ownerId, item("Drill", true)).getId();
        assertThrows(NotFoundException.class, () -> itemService.update(bookerId, itemId, item("X", true)));
    }

    @Test
    void findByIdOwnerSeesBookingDates() {
        long itemId = itemService.create(ownerId, item("Drill", true)).getId();
        approvedPastBooking(itemId);

        ItemBookingDto view = itemService.findById(ownerId, itemId);
        assertThat(view.getLastBooking()).isNotNull();
        assertThat(view.getComments()).isEmpty();
    }

    @Test
    void findByIdNonOwnerHidesBookingDates() {
        long itemId = itemService.create(ownerId, item("Drill", true)).getId();
        approvedPastBooking(itemId);

        ItemBookingDto view = itemService.findById(bookerId, itemId);
        assertThat(view.getLastBooking()).isNull();
        assertThat(view.getNextBooking()).isNull();
    }

    @Test
    void findByOwnerReturnsItems() {
        itemService.create(ownerId, item("A", true));
        itemService.create(ownerId, item("B", true));
        Collection<ItemBookingDto> items = itemService.findByOwner(ownerId);
        assertThat(items).hasSize(2);
    }

    @Test
    void searchFindsAvailableByText() {
        itemService.create(ownerId, item("Дрель ударная", true));
        itemService.create(ownerId, item("Молоток", false));
        Collection<ItemDto> found = itemService.search("дрель");
        assertThat(found).hasSize(1);
    }

    @Test
    void searchBlankReturnsEmpty() {
        assertThat(itemService.search(" ")).isEmpty();
    }

    @Test
    void addCommentAfterRental() {
        long itemId = itemService.create(ownerId, item("Drill", true)).getId();
        approvedPastBooking(itemId);

        CommentDto comment = new CommentDto();
        comment.setText("Отлично!");
        CommentDto saved = itemService.addComment(bookerId, itemId, comment);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAuthorName()).isEqualTo("Booker");

        ItemBookingDto view = itemService.findById(ownerId, itemId);
        assertThat(view.getComments()).hasSize(1);
    }

    @Test
    void addCommentWithoutRentalThrowsValidation() {
        long itemId = itemService.create(ownerId, item("Drill", true)).getId();
        CommentDto comment = new CommentDto();
        comment.setText("no rental");
        assertThrows(ValidationException.class, () -> itemService.addComment(bookerId, itemId, comment));
    }

    private void approvedPastBooking(long itemId) {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(itemId);
        dto.setStart(LocalDateTime.now().minusDays(2));
        dto.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto booking = bookingService.create(bookerId, dto);
        bookingService.approve(ownerId, booking.getId(), true);
    }
}
