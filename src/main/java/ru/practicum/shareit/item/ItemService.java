package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto create(long userId, ItemDto item);

    ItemDto update(long userId, long itemId, ItemDto item);

    ItemBookingDto findById(long userId, long itemId);

    Collection<ItemBookingDto> findByOwner(long userId);

    Collection<ItemDto> search(String text);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
