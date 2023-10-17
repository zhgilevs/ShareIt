package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllByUserId(long userId);

    ItemRequestDto get(long userId, long requestId);

    List<ItemRequestDto> getAll(long userId, int from, int size);
}