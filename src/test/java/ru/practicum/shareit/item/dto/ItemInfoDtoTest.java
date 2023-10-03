package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingInfoDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemInfoDtoTest {

    @Autowired
    private JacksonTester<ItemInfoDto> jacksonTester;

    @Test
    void test_ItemDInfoDto_serialization() throws Exception {
        ItemInfoDto itemInfoDto = ItemInfoDto.builder()
                .id(1L).name("Дрель").description("Простая дрель").available(true)
                .lastBooking(new BookingInfoDto(1L, 1L))
                .nextBooking(new BookingInfoDto(2L, 2L))
                .build();
        JsonContent<ItemInfoDto> itemInfoDtoJson = jacksonTester.write(itemInfoDto);
        assertThat(itemInfoDtoJson).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(itemInfoDtoJson).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(itemInfoDtoJson).extractingJsonPathStringValue("$.description").isEqualTo("Простая дрель");
        assertThat(itemInfoDtoJson).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(itemInfoDtoJson).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(itemInfoDtoJson).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(1);
        assertThat(itemInfoDtoJson).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(itemInfoDtoJson).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(2);
    }
}