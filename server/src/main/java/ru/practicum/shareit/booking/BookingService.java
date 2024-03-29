package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto create(long bookerId, BookingRequestDto bookingRequestDto);

    BookingResponseDto updateStatus(long ownerId, long bookingId, boolean approved);

    BookingResponseDto get(long userId, long bookingId);

    List<BookingResponseDto> getByBookerId(long bookerId, State state, int from, int size);

    List<BookingResponseDto> getByOwnerId(long ownerId, State state, int from, int size);
}