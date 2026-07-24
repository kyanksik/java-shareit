package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID_HEADER) long userId,
                          @RequestBody ItemDto item) {
        log.info("Добавление вещи пользователем id={}", userId);
        return itemService.create(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID_HEADER) long userId,
                          @PathVariable long itemId,
                          @RequestBody ItemDto item) {
        log.info("Редактирование вещи id={} пользователем id={}", itemId, userId);
        return itemService.update(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemBookingDto findById(@RequestHeader(USER_ID_HEADER) long userId,
                                   @PathVariable long itemId) {
        return itemService.findById(userId, itemId);
    }

    @GetMapping
    public Collection<ItemBookingDto> findByOwner(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.findByOwner(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_ID_HEADER) long userId,
                                 @PathVariable long itemId,
                                 @RequestBody CommentDto commentDto) {
        log.info("Добавление комментария к вещи id={} пользователем id={}", itemId, userId);
        return itemService.addComment(userId, itemId, commentDto);
    }
}
