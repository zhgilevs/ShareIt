package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.OnCreate;
import ru.practicum.shareit.common.OnUpdate;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {

    private final UserClient userClient;

    @PostMapping
    ResponseEntity<Object> create(@Validated({OnCreate.class}) @RequestBody UserDto userDto) {
        log.info("Creating user from request body: " + userDto.toString());
        return userClient.create(userDto);
    }

    @PatchMapping(path = "/{id}")
    ResponseEntity<Object> update(@Validated({OnUpdate.class}) @RequestBody UserDto userDto,
                                  @PathVariable long id) {
        log.info("Updating user with ID: '" + id + "' from request body: " + userDto.toString());
        userDto.setId(id);
        return userClient.update(userDto);
    }

    @GetMapping
    ResponseEntity<Object> getAll() {
        log.info("Receiving list of all users");
        return userClient.getAll();
    }

    @GetMapping(path = "/{id}")
    ResponseEntity<Object> get(@PathVariable long id) {
        log.info("Receiving user with ID: '" + id + "'");
        return userClient.get(id);
    }

    @DeleteMapping(path = "/{id}")
    ResponseEntity<Object> delete(@PathVariable long id) {
        log.info("Removing user with ID: '" + id + "'");
        return userClient.delete(id);
    }
}