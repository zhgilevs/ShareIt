package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.dto.BookingMapper.*;

class BookingMapperTest {

    Booking booking;
    BookingInfoDto bookingInfoDto;
    BookingRequestDto bookingRequestDto;
    BookingResponseDto bookingResponseDto;

    @BeforeEach
    void init() {
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        booking = new Booking(1L, start, end,
                new Item(1L, "Дрель", "Простая дрель", true,
                        new User(1L, "owner", "owner@ya.ru"), null),
                new User(2L, "booker", "booker@ya.ru"), Status.APPROVED);
        bookingInfoDto = new BookingInfoDto(1L, 2L);
        bookingRequestDto = new BookingRequestDto(1L, start, end);
        bookingResponseDto = new BookingResponseDto(1L, start, end, Status.APPROVED,
                new UserDto(2L, "booker", "booker@ya.ru"),
                new ItemDto(1L, "Дрель", "Простая дрель", true, null));
    }

    @Test
    void test_toBooking() {
        Booking returnedBooking = toBooking(bookingRequestDto);
        assertEquals(booking.getStart(), returnedBooking.getStart());
        assertEquals(booking.getEnd(), returnedBooking.getEnd());
    }

    @Test
    void test_toBookingInfoDto() {
        BookingInfoDto returnedBookingInfoDto = toBookingInfoDto(booking);
        assertEquals(bookingInfoDto.getId(), returnedBookingInfoDto.getId());
        assertEquals(bookingInfoDto.getBookerId(), returnedBookingInfoDto.getBookerId());
    }

    @Test
    void test_toBookingResponseDto() {
        BookingResponseDto returnedBookingResponseDto = toBookingResponseDto(booking);
        assertEquals(bookingResponseDto.getId(), returnedBookingResponseDto.getId());
        assertEquals(bookingResponseDto.getStart(), returnedBookingResponseDto.getStart());
        assertEquals(bookingResponseDto.getEnd(), returnedBookingResponseDto.getEnd());
        assertEquals(bookingResponseDto.getStatus(), returnedBookingResponseDto.getStatus());
        assertEquals(bookingResponseDto.getBooker(), returnedBookingResponseDto.getBooker());
        assertEquals(bookingResponseDto.getItem(), returnedBookingResponseDto.getItem());
    }
}