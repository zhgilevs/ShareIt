package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.OnCreate;

import java.util.List;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    ItemDto create(@RequestHeader(USER_HEADER) Long userId,
                   @Validated({OnCreate.class}) @RequestBody ItemDto itemDto) {
        log.info("Creating item from request body: " + itemDto.toString());
        return itemService.create(userId, itemDto);
    }

    @PatchMapping(path = "/{itemId}")
    ItemDto update(@RequestHeader(USER_HEADER) Long userId,
                   @PathVariable Long itemId,
                   @RequestBody ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping
    List<ItemDto> getAll(@RequestHeader(USER_HEADER) Long userId) {
        log.info("Receiving items of user with ID: '" + userId + "'");
        return itemService.getAll(userId);
    }

    @GetMapping(path = "{itemId}")
    ItemDto get(@PathVariable Long itemId) {
        log.info("Receiving item with ID: '" + itemId + "'");
        return itemService.get(itemId);
    }

    @GetMapping(path = "/search")
    List<ItemDto> search(@RequestHeader(USER_HEADER) Long userId,
                         @RequestParam String text) {
        log.info("User with ID: '" + userId + "' search item by text: '" + text + "'");
        return itemService.search(text);
    }
}