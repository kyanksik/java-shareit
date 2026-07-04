package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(long userId, ItemDto dto) {
        User owner = getUserOrThrow(userId);
        Item item = ItemMapper.toModel(dto);
        item.setOwner(owner);
        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto dto) {
        getUserOrThrow(userId);
        Item item = getOrThrow(itemId);
        if (item.getOwner() == null || !Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Вещь с id " + itemId + " не принадлежит пользователю " + userId);
        }
        if (dto.getName() != null && !dto.getName().isBlank()) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }
        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto findById(long itemId) {
        return ItemMapper.toDto(getOrThrow(itemId));
    }

    @Override
    public Collection<ItemDto> findByOwner(long userId) {
        getUserOrThrow(userId);
        return itemRepository.findByOwnerId(userId).stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public Collection<ItemDto> search(String text) {
        return itemRepository.search(text).stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    private Item getOrThrow(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
    }

    private User getUserOrThrow(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }
}
