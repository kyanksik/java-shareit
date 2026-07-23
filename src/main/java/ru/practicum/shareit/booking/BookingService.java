package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

/**
 * Операции бронирования вещей.
 */
public interface BookingService {

    /**
     * Создаёт запрос на бронирование в статусе WAITING.
     *
     * @param userId идентификатор бронирующего пользователя
     */
    BookingDto create(long userId, BookingCreateDto dto);

    /**
     * Подтверждает или отклоняет бронирование; доступно только владельцу вещи.
     *
     * @param userId    идентификатор владельца вещи
     * @param bookingId идентификатор бронирования
     * @param approved  true — подтвердить, false — отклонить
     */
    BookingDto approve(long userId, long bookingId, boolean approved);

    /**
     * Возвращает бронирование; доступно автору брони или владельцу вещи.
     *
     * @param userId    идентификатор запрашивающего пользователя
     * @param bookingId идентификатор бронирования
     */
    BookingDto findById(long userId, long bookingId);

    /**
     * Возвращает бронирования текущего пользователя, отфильтрованные по состоянию.
     *
     * @param userId идентификатор пользователя — автора бронирований
     */
    List<BookingDto> findByBooker(long userId, BookingState state);

    /**
     * Возвращает бронирования всех вещей владельца, отфильтрованные по состоянию.
     *
     * @param userId идентификатор владельца вещей
     */
    List<BookingDto> findByOwner(long userId, BookingState state);
}
