package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Доступ к данным бронирований.
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Все бронирования пользователя.
     */
    List<Booking> findByBookerId(Long bookerId, Sort sort);

    /**
     * Текущие бронирования пользователя (идущие в данный момент).
     */
    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime start,
                                                          LocalDateTime end, Sort sort);

    /**
     * Завершённые бронирования пользователя.
     */
    List<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime end, Sort sort);

    /**
     * Будущие бронирования пользователя.
     */
    List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime start, Sort sort);

    /**
     * Бронирования пользователя с заданным статусом.
     */
    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    /**
     * Все бронирования вещей владельца.
     */
    List<Booking> findByItemOwnerId(Long ownerId, Sort sort);

    /**
     * Текущие бронирования вещей владельца.
     */
    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime start,
                                                             LocalDateTime end, Sort sort);

    /**
     * Завершённые бронирования вещей владельца.
     */
    List<Booking> findByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime end, Sort sort);

    /**
     * Будущие бронирования вещей владельца.
     */
    List<Booking> findByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime start, Sort sort);

    /**
     * Бронирования вещей владельца с заданным статусом.
     */
    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    /**
     * Бронирования конкретной вещи с заданным статусом.
     */
    List<Booking> findByItemIdAndStatus(Long itemId, BookingStatus status, Sort sort);

    /**
     * Бронирования нескольких вещей с заданным статусом.
     */
    List<Booking> findByItemIdInAndStatus(List<Long> itemIds, BookingStatus status, Sort sort);

    /**
     * Проверяет, была ли у пользователя завершённая аренда вещи с заданным статусом.
     */
    boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(Long bookerId, Long itemId,
                                                           BookingStatus status, LocalDateTime end);
}
