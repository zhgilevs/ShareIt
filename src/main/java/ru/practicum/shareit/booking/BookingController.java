package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.common.OnCreate;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    BookingResponseDto create(@RequestHeader(USER_HEADER) long bookerId,
                              @Validated({OnCreate.class}) @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("Creating booking from request body: " + bookingRequestDto.toString());
        return bookingService.create(bookerId, bookingRequestDto);
    }

    @PatchMapping(path = "/{bookingId}")
    BookingResponseDto updateStatus(@RequestHeader(USER_HEADER) long ownerId,
                                    @PathVariable long bookingId,
                                    @RequestParam boolean approved) {
        log.info("Updating status of booking with ID: '" + bookingId + "'");
        return bookingService.updateStatus(ownerId, bookingId, approved);
    }

    @GetMapping(path = "/{bookingId}")
    BookingResponseDto get(@RequestHeader(USER_HEADER) long userId,
                           @PathVariable long bookingId) {
        log.info("Receiving booking with ID: '" + bookingId + "' of user with ID: '" + userId + "'");
        return bookingService.get(userId, bookingId);
    }

    @GetMapping
    List<BookingResponseDto> getByBookerId(@RequestHeader(USER_HEADER) long bookerId,
                                           @RequestParam(name = "state", required = false) String state,
                                           @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                           @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("Receiving all bookings of booker with ID: '" + bookerId + "'");
        return bookingService.getByBookerId(bookerId, state, from, size);
    }

    @GetMapping(path = "/owner")
    List<BookingResponseDto> getByOwnerId(@RequestHeader(USER_HEADER) long ownerId,
                                          @RequestParam(name = "state", required = false) String state,
                                          @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                          @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("Receiving all bookings to items of owner with ID: '" + ownerId + "'");
        return bookingService.getByOwnerId(ownerId, state, from, size);
    }
}