package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> jacksonTester;

    @Test
    void test_ItemDto_serialization() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Дрель", "Простая дрель", true, 1L);
        JsonContent<ItemDto> itemDtoJson = jacksonTester.write(itemDto);
        assertThat(itemDtoJson).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(itemDtoJson).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(itemDtoJson).extractingJsonPathStringValue("$.description").isEqualTo("Простая дрель");
        assertThat(itemDtoJson).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(itemDtoJson).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }
}