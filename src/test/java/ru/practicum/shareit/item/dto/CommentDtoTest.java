package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> jacksonTester;

    @Test
    void test_CommentDto_serialization() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "Хорошая дрель", "User", LocalDateTime.of(2022,11,1,10,0));
        JsonContent<CommentDto> commentDtoJson = jacksonTester.write(commentDto);
        assertThat(commentDtoJson).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(commentDtoJson).extractingJsonPathStringValue("$.text").isEqualTo("Хорошая дрель");
        assertThat(commentDtoJson).extractingJsonPathStringValue("$.authorName").isEqualTo("User");
        assertThat(commentDtoJson).extractingJsonPathStringValue("$.created").isEqualTo(LocalDateTime.of(2022, 11, 1, 10, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}