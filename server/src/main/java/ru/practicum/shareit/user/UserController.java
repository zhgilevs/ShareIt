package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    UserDto create(@RequestBody UserDto userDto) {
        log.info("Creating user from request body: " + userDto.toString());
        return userService.create(userDto);
    }

    @PatchMapping(path = "/{id}")
    UserDto update(@RequestBody UserDto userDto,
                   @PathVariable long id) {
        log.info("Updating user with ID: '" + id + "' from request body: " + userDto.toString());
        userDto.setId(id);
        return userService.update(userDto);
    }

    @GetMapping
    List<UserDto> getAll() {
        log.info("Receiving list of all users");
        return userService.getAll();
    }

    @GetMapping(path = "/{id}")
    UserDto get(@PathVariable long id) {
        log.info("Receiving user with ID: '" + id + "'");
        return userService.get(id);
    }

    @DeleteMapping(path = "/{id}")
    void delete(@PathVariable long id) {
        log.info("Removing user with ID: '" + id + "'");
        userService.delete(id);
    }
}