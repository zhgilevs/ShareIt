package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;

import java.util.List;

public interface ItemService {

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    List<ItemInfoDto> getAll(long userId);

    ItemInfoDto get(long userId, long itemId);

    List<ItemDto> search(String text);

    CommentDto createComment(long userId, long itemId, CommentDto commentDto);
}