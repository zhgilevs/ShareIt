package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.OnCreate;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    ResponseEntity<Object> create(@RequestHeader(USER_HEADER) long userId,
                                  @Validated({OnCreate.class}) @RequestBody ItemDto itemDto) {
        log.info("Creating item from request body: " + itemDto.toString());
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping(path = "/{itemId}")
    ResponseEntity<Object> update(@RequestHeader(USER_HEADER) long userId,
                                  @PathVariable long itemId,
                                  @RequestBody ItemDto itemDto) {
        log.info("Updating item from request body: " + itemDto.toString());
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping
    ResponseEntity<Object> getAll(@RequestHeader(USER_HEADER) long userId,
                                  @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                  @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("Receiving items of user with ID: '" + userId + "'");
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping(path = "{itemId}")
    ResponseEntity<Object> get(@RequestHeader(USER_HEADER) long userId,
                               @PathVariable long itemId) {
        log.info("Receiving item with ID: '" + itemId + "'");
        return itemClient.get(userId, itemId);
    }

    @GetMapping(path = "/search")
    ResponseEntity<Object> search(@RequestHeader(USER_HEADER) long userId,
                                  @RequestParam String text,
                                  @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                  @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("User with ID: '" + userId + "' search item by text: '" + text + "'");
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping(path = "/{itemId}/comment")
    ResponseEntity<Object> createComment(@RequestHeader(USER_HEADER) long userId,
                                         @PathVariable long itemId,
                                         @Validated({OnCreate.class}) @RequestBody CommentDto commentDto) {
        log.info("Creating comment from request body: " + commentDto.toString());
        return itemClient.createComment(userId, itemId, commentDto);
    }
}