package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto create(long userId, ItemDto item);

    ItemDto update(long userId, long itemId, ItemDto item);

    ItemDto findById(long itemId);

    Collection<ItemDto> findByOwner(long userId);

    Collection<ItemDto> search(String text);
}
