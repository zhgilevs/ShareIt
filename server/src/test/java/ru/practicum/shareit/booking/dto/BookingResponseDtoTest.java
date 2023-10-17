package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingResponseDtoTest {

    @Autowired
    private JacksonTester<BookingResponseDto> jacksonTester;

    @Test
    void test_BookingResponseDto_serialization() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        BookingResponseDto bookingResponseDto = new BookingResponseDto(1L, start, end, Status.APPROVED,
                new UserDto(1L, "booker", "booker@ya.ru"),
                new ItemDto(1L, "Дрель", "Простая дрель", true, 1L));
        JsonContent<BookingResponseDto> bookingResponseDtoJson = jacksonTester.write(bookingResponseDto);
        assertThat(bookingResponseDtoJson).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(bookingResponseDtoJson).extractingJsonPathStringValue("$.start").isEqualTo(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(bookingResponseDtoJson).extractingJsonPathStringValue("$.end").isEqualTo(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(bookingResponseDtoJson).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(bookingResponseDtoJson).extractingJsonPathStringValue("$.booker.name").isEqualTo("booker");
        assertThat(bookingResponseDtoJson).extractingJsonPathStringValue("$.booker.email").isEqualTo("booker@ya.ru");
        assertThat(bookingResponseDtoJson).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(bookingResponseDtoJson).extractingJsonPathStringValue("$.item.name").isEqualTo("Дрель");
        assertThat(bookingResponseDtoJson).extractingJsonPathStringValue("$.item.description").isEqualTo("Простая дрель");
        assertThat(bookingResponseDtoJson).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(bookingResponseDtoJson).extractingJsonPathNumberValue("$.item.requestId").isEqualTo(1);
    }
}