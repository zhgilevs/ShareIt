package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.item.dto.ItemMapper.*;

class ItemMapperTest {

    Item item;
    ItemDto itemDto;
    ItemInfoDto itemInfoDto;

    @BeforeEach
    void init() {
        LocalDateTime now = LocalDateTime.now();
        item = new Item(1L, "Дрель", "Простая дрель", true,
                new User(1L, "owner", "owner@ya.ru"),
                new ItemRequest(1L, "нужна простая дрель", now,
                        new User(2L, "requester", "requester@ya.ru")));
        itemDto = new ItemDto(1L, "Дрель", "Простая дрель", true, 1L);
        itemInfoDto = new ItemInfoDto(1L, "Дрель", "Простая дрель", true,
                new BookingInfoDto(5L, 5L),
                new BookingInfoDto(6L, 6L),
                new ArrayList<>());
    }

    @Test
    void test_toItem() {
        Item returnedItem = toItem(itemDto);
        assertEquals(item.getId(), returnedItem.getId());
        assertEquals(item.getName(), returnedItem.getName());
        assertEquals(item.getDescription(), returnedItem.getDescription());
        assertEquals(item.getAvailable(), returnedItem.getAvailable());
    }

    @Test
    void test_toItemDto() {
        ItemDto returnedItemDto = toItemDto(item);
        assertEquals(itemDto.getId(), returnedItemDto.getId());
        assertEquals(itemDto.getName(), returnedItemDto.getName());
        assertEquals(itemDto.getDescription(), returnedItemDto.getDescription());
        assertEquals(itemDto.getAvailable(), returnedItemDto.getAvailable());
        assertEquals(itemDto.getRequestId(), returnedItemDto.getRequestId());
    }

    @Test
    void test_toItemInfoDto() {
        ItemInfoDto returnedItemInfoDto = toItemInfoDto(item);
        assertEquals(itemInfoDto.getId(), returnedItemInfoDto.getId());
        assertEquals(itemInfoDto.getName(), returnedItemInfoDto.getName());
        assertEquals(itemInfoDto.getDescription(), returnedItemInfoDto.getDescription());
        assertEquals(itemInfoDto.getAvailable(), returnedItemInfoDto.getAvailable());
    }
}