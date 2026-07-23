package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

/**
 * Операции над вещами: создание, редактирование, поиск и отзывы.
 */
public interface ItemService {

    /**
     * Добавляет новую вещь от имени пользователя.
     *
     * @param userId идентификатор владельца
     */
    ItemDto create(long userId, ItemDto item);

    /**
     * Редактирует вещь; доступно только её владельцу.
     *
     * @param userId идентификатор владельца
     * @param itemId идентификатор редактируемой вещи
     */
    ItemDto update(long userId, long itemId, ItemDto item);

    /**
     * Возвращает вещь с отзывами; владельцу — также с датами бронирований.
     *
     * @param userId идентификатор запрашивающего пользователя
     * @param itemId идентификатор вещи
     */
    ItemBookingDto findById(long userId, long itemId);

    /**
     * Возвращает все вещи владельца с датами бронирований и отзывами.
     *
     * @param userId идентификатор владельца
     */
    Collection<ItemBookingDto> findByOwner(long userId);

    /**
     * Ищет доступные вещи по тексту в названии и описании.
     */
    Collection<ItemDto> search(String text);

    /**
     * Добавляет отзыв к вещи; доступно пользователю, завершившему её аренду.
     *
     * @param userId идентификатор автора отзыва
     * @param itemId идентификатор вещи
     */
    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
