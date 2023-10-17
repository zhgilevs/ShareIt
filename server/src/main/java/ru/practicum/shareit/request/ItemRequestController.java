package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    ItemRequestDto create(@RequestHeader(USER_HEADER) long userId,
                          @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Creating request from request body: " + itemRequestDto.toString());
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    List<ItemRequestDto> getAllByUserId(@RequestHeader(USER_HEADER) long userId) {
        log.info("Receiving requests of user with ID: '" + userId + "'");
        return itemRequestService.getAllByUserId(userId);
    }

    @GetMapping(path = "{requestId}")
    ItemRequestDto get(@RequestHeader(USER_HEADER) long userId,
                       @PathVariable long requestId) {
        log.info("Receiving request with ID: '" + requestId + "'");
        return itemRequestService.get(userId, requestId);
    }

    @GetMapping(path = "/all")
    List<ItemRequestDto> getAll(@RequestHeader(USER_HEADER) long userId,
                                @RequestParam(name = "from", defaultValue = "0") int from,
                                @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Receiving all requests");
        return itemRequestService.getAll(userId, from, size);
    }
}