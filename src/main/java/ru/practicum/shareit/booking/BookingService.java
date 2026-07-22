package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto create(long userId, BookingCreateDto dto);

    BookingDto approve(long userId, long bookingId, boolean approved);

    BookingDto findById(long userId, long bookingId);

    List<BookingDto> findByBooker(long userId, BookingState state);

    List<BookingDto> findByOwner(long userId, BookingState state);
}
