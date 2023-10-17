package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.user.dto.UserMapper.toUser;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

class UserMapperTest {

    User user;
    UserDto userDto;

    @BeforeEach
    void init() {
        user = new User(1L, "user", "user@user.com");
        userDto = new UserDto(1L, "user", "user@user.com");
    }

    @Test
    void test_toUser() {
        User returnedUser = toUser(userDto);
        assertEquals(user.getId(), returnedUser.getId());
        assertEquals(user.getName(), returnedUser.getName());
        assertEquals(user.getEmail(), returnedUser.getEmail());
    }

    @Test
    void test_toUserDto() {
        UserDto returnedUserDto = toUserDto(user);
        assertEquals(userDto.getId(), returnedUserDto.getId());
        assertEquals(userDto.getName(), returnedUserDto.getName());
        assertEquals(userDto.getEmail(), returnedUserDto.getEmail());
    }
}