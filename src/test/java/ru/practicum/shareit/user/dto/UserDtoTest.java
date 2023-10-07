package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> jacksonTester;

    @Test
    void test_UserDto_serialization() throws Exception {
        UserDto userDto = new UserDto(1L, "user", "user@user.com");
        JsonContent<UserDto> userDtoJson = jacksonTester.write(userDto);
        assertThat(userDtoJson).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(userDtoJson).extractingJsonPathStringValue("$.name").isEqualTo("user");
        assertThat(userDtoJson).extractingJsonPathStringValue("$.email").isEqualTo("user@user.com");
    }
}