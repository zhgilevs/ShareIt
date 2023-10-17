package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.request.dto.ItemRequestMapper.toItemRequest;
import static ru.practicum.shareit.request.dto.ItemRequestMapper.toItemRequestDto;

class ItemRequestMapperTest {

    ItemRequest itemRequest;
    ItemRequestDto itemRequestDto;

    @BeforeEach
    void init() {
        LocalDateTime now = LocalDateTime.now();
        itemRequest = new ItemRequest(1L, "нужна дрель", now,
                new User(1L, "requester", "requester@ya.ru"));
        itemRequestDto = new ItemRequestDto(1L, "нужна дрель", now, null);
    }

    @Test
    void test_toItemRequest() {
        ItemRequest returnedItemRequest = toItemRequest(itemRequestDto);
        assertEquals(itemRequest.getDescription(), returnedItemRequest.getDescription());
    }

    @Test
    void test_toItemRequestDto() {
        ItemRequestDto returnedItemRequestDto = toItemRequestDto(itemRequest);
        assertEquals(itemRequest.getId(), returnedItemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), returnedItemRequestDto.getDescription());
        assertEquals(itemRequest.getCreated(), returnedItemRequestDto.getCreated());
    }
}