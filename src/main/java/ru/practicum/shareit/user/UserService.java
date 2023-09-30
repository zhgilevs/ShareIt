package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto);

    List<UserDto> getAll();

    UserDto get(Long id);

    void delete(Long id);
}