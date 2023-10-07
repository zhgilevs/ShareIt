package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingInfoDtoTest {

    @Autowired
    private JacksonTester<BookingInfoDto> jacksonTester;

    @Test
    void test_BookingInfoDto_serialization() throws Exception {
        BookingInfoDto bookingInfoDto = new BookingInfoDto(1L, 1L);
        JsonContent<BookingInfoDto> bookingInfoDtoJson = jacksonTester.write(bookingInfoDto);
        assertThat(bookingInfoDtoJson).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(bookingInfoDtoJson).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
    }
}