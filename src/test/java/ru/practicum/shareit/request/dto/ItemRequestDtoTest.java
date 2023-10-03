package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> jacksonTester;

    @Test
    void test_ItemRequestDto_serialization() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "нужна дрель", now, null);
        JsonContent<ItemRequestDto> itemRequestDtoJson = jacksonTester.write(itemRequestDto);
        assertThat(itemRequestDtoJson).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(itemRequestDtoJson).extractingJsonPathStringValue("$.description").isEqualTo("нужна дрель");
        assertThat(itemRequestDtoJson).extractingJsonPathStringValue("$.created").isEqualTo(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}