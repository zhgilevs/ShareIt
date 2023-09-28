package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    List<ItemDto> getAll(Long userId);

    ItemDto get(Long itemId);

    List<ItemDto> search(String text);
}