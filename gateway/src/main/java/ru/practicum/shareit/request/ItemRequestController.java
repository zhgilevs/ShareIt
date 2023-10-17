package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.OnCreate;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    ResponseEntity<Object> create(@RequestHeader(USER_HEADER) long userId,
                                  @Validated({OnCreate.class}) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Creating request from request body: " + itemRequestDto.toString());
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    ResponseEntity<Object> getAllByUserId(@RequestHeader(USER_HEADER) long userId) {
        log.info("Receiving requests of user with ID: '" + userId + "'");
        return itemRequestClient.getAllByUserId(userId);
    }

    @GetMapping(path = "{requestId}")
    ResponseEntity<Object> get(@RequestHeader(USER_HEADER) long userId,
                               @PathVariable long requestId) {
        log.info("Receiving request with ID: '" + requestId + "'");
        return itemRequestClient.get(userId, requestId);
    }

    @GetMapping(path = "/all")
    ResponseEntity<Object> getAll(@RequestHeader(USER_HEADER) long userId,
                                  @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                  @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("Receiving all requests");
        return itemRequestClient.getAll(userId, from, size);
    }
}