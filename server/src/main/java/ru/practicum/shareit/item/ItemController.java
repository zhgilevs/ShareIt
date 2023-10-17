package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;

import java.util.List;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    ItemDto create(@RequestHeader(USER_HEADER) long userId,
                   @RequestBody ItemDto itemDto) {
        log.info("Creating item from request body: " + itemDto.toString());
        return itemService.create(userId, itemDto);
    }

    @PatchMapping(path = "/{itemId}")
    ItemDto update(@RequestHeader(USER_HEADER) long userId,
                   @PathVariable long itemId,
                   @RequestBody ItemDto itemDto) {
        log.info("Updating item from request body: " + itemDto.toString());
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping
    List<ItemInfoDto> getAll(@RequestHeader(USER_HEADER) long userId,
                             @RequestParam(name = "from", defaultValue = "0") int from,
                             @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Receiving items of user with ID: '" + userId + "'");
        return itemService.getAll(userId, from, size);
    }

    @GetMapping(path = "{itemId}")
    ItemInfoDto get(@RequestHeader(USER_HEADER) long userId,
                    @PathVariable long itemId) {
        log.info("Receiving item with ID: '" + itemId + "'");
        return itemService.get(userId, itemId);
    }

    @GetMapping(path = "/search")
    List<ItemDto> search(@RequestHeader(USER_HEADER) long userId,
                         @RequestParam String text,
                         @RequestParam(name = "from", defaultValue = "0") int from,
                         @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("User with ID: '" + userId + "' search item by text: '" + text + "'");
        return itemService.search(userId, text, from, size);
    }

    @PostMapping(path = "/{itemId}/comment")
    CommentDto createComment(@RequestHeader(USER_HEADER) long userId,
                             @PathVariable long itemId,
                             @RequestBody CommentDto commentDto) {
        log.info("Creating comment from request body: " + commentDto.toString());
        return itemService.createComment(userId, itemId, commentDto);
    }
}