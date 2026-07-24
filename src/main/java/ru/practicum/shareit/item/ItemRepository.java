package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * Доступ к данным вещей.
 */
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Возвращает вещи, принадлежащие владельцу.
     */
    List<Item> findByOwnerId(Long ownerId);

    /**
     * Ищет доступные вещи, у которых текст встречается в названии или описании.
     */
    @Query("select i from Item i " +
            "where i.available = true " +
            "and (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%')))")
    List<Item> search(String text);
}
