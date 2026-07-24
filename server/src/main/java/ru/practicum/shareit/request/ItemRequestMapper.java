package ru.practicum.shareit.request;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemAnswerDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public final class ItemRequestMapper {

    private ItemRequestMapper() {
    }

    public static ItemRequestDto toDto(ItemRequest request) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        return dto;
    }

    public static ItemRequestDto toDto(ItemRequest request, List<Item> answers) {
        ItemRequestDto dto = toDto(request);
        dto.setItems(answers.stream()
                .map(ItemRequestMapper::toAnswer)
                .toList());
        return dto;
    }

    private static ItemAnswerDto toAnswer(Item item) {
        ItemAnswerDto dto = new ItemAnswerDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setOwnerId(item.getOwner() != null ? item.getOwner().getId() : null);
        return dto;
    }
}
