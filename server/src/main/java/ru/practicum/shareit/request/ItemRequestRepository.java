package ru.practicum.shareit.request;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

/**
 * Доступ к данным запросов вещей.
 */
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    /**
     * Возвращает запросы, созданные пользователем.
     */
    List<ItemRequest> findByRequestorId(Long requestorId, Sort sort);

    /**
     * Возвращает запросы, созданные другими пользователями.
     */
    List<ItemRequest> findByRequestorIdNot(Long requestorId, Sort sort);
}
