package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * Операции с запросами вещей.
 */
public interface ItemRequestService {

    /**
     * Создаёт новый запрос вещи.
     *
     * @param userId идентификатор автора запроса
     */
    ItemRequestDto create(long userId, ItemRequestDto dto);

    /**
     * Возвращает запросы пользователя вместе с ответами на них.
     *
     * @param userId идентификатор автора запросов
     */
    List<ItemRequestDto> findByRequestor(long userId);

    /**
     * Возвращает запросы, созданные другими пользователями.
     *
     * @param userId идентификатор текущего пользователя
     */
    List<ItemRequestDto> findAll(long userId);

    /**
     * Возвращает запрос по идентификатору вместе с ответами на него.
     *
     * @param userId    идентификатор текущего пользователя
     * @param requestId идентификатор запроса
     */
    ItemRequestDto findById(long userId, long requestId);
}
