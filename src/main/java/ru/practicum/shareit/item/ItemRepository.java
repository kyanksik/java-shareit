package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {

    Item save(Item item);

    Optional<Item> findById(long id);

    Collection<Item> findByOwnerId(long ownerId);

    Collection<Item> search(String text);
}
