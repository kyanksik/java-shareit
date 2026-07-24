package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) long userId,
                                         @Valid @RequestBody BookItemRequestDto dto) {
        log.info("Создание бронирования вещи id={} пользователем id={}", dto.getItemId(), userId);
        return bookingClient.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(USER_ID_HEADER) long userId,
                                          @PathVariable long bookingId,
                                          @RequestParam boolean approved) {
        log.info("Подтверждение={} бронирования id={} владельцем id={}", approved, bookingId, userId);
        return bookingClient.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@RequestHeader(USER_ID_HEADER) long userId,
                                           @PathVariable long bookingId) {
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findByBooker(@RequestHeader(USER_ID_HEADER) long userId,
                                               @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getByBooker(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findByOwner(@RequestHeader(USER_ID_HEADER) long userId,
                                              @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getByOwner(userId, state);
    }
}
