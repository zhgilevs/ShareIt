package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.common.OnCreate;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @GetMapping
    ResponseEntity<Object> getByBookerId(@RequestHeader(USER_HEADER) long bookerId,
                                         @RequestParam(name = "state", required = false) String state,
                                         @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("Receiving all bookings of booker with ID: '" + bookerId + "'");
        State enumState = State.parseState(state);
        return bookingClient.getByBookerId(bookerId, enumState, from, size);
    }

    @PostMapping
    ResponseEntity<Object> create(@RequestHeader(USER_HEADER) long bookerId,
                                  @Validated({OnCreate.class}) @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("Creating booking from request body: " + bookingRequestDto.toString());
        return bookingClient.create(bookerId, bookingRequestDto);
    }

    @GetMapping("/{bookingId}")
    ResponseEntity<Object> get(@RequestHeader(USER_HEADER) long userId,
                               @PathVariable long bookingId) {
        log.info("Receiving booking with ID: '" + bookingId + "' of user with ID: '" + userId + "'");
        return bookingClient.get(userId, bookingId);
    }

    @PatchMapping(path = "/{bookingId}")
    ResponseEntity<Object> updateStatus(@RequestHeader(USER_HEADER) long ownerId,
                                        @PathVariable long bookingId,
                                        @RequestParam boolean approved) {
        log.info("Updating status of booking with ID: '" + bookingId + "'");
        return bookingClient.updateStatus(ownerId, bookingId, approved);
    }

    @GetMapping(path = "/owner")
    ResponseEntity<Object> getByOwnerId(@RequestHeader(USER_HEADER) long ownerId,
                                        @RequestParam(name = "state", required = false) String state,
                                        @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                        @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("Receiving all bookings to items of owner with ID: '" + ownerId + "'");
        State enumState = State.parseState(state);
        return bookingClient.getByOwnerId(ownerId, enumState, from, size);
    }
}