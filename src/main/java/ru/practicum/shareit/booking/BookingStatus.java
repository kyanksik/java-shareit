package ru.practicum.shareit.booking;

/**
 * Статус бронирования.
 */
public enum BookingStatus {
    WAITING, // ожидает подтверждения владельцем
    APPROVED, // подтверждено владельцем
    REJECTED, // отклонено владельцем
    CANCELED // отменено создателем брони
}
