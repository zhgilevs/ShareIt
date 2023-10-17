package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingRequestDtoTest {

    @Autowired
    private JacksonTester<BookingRequestDto> jacksonTester;

    @Test
    void test_BookingRequestDto_serialization() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1L, start, end);
        JsonContent<BookingRequestDto> bookingRequestDtoJson = jacksonTester.write(bookingRequestDto);
        assertThat(bookingRequestDtoJson).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(bookingRequestDtoJson).extractingJsonPathStringValue("$.start").isEqualTo(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(bookingRequestDtoJson).extractingJsonPathStringValue("$.end").isEqualTo(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}