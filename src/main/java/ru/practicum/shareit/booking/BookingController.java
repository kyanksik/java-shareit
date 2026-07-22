package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader(USER_ID_HEADER) long userId,
                             @Valid @RequestBody BookingCreateDto dto) {
        log.info("Создание бронирования вещи id={} пользователем id={}", dto.getItemId(), userId);
        return bookingService.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader(USER_ID_HEADER) long userId,
                             @PathVariable long bookingId,
                             @RequestParam boolean approved) {
        log.info("Подтверждение={} бронирования id={} владельцем id={}", approved, bookingId, userId);
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader(USER_ID_HEADER) long userId,
                              @PathVariable long bookingId) {
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findByBooker(@RequestHeader(USER_ID_HEADER) long userId,
                                        @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findByBooker(userId, BookingState.from(state));
    }

    @GetMapping("/owner")
    public List<BookingDto> findByOwner(@RequestHeader(USER_ID_HEADER) long userId,
                                       @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findByOwner(userId, BookingState.from(state));
    }
}
