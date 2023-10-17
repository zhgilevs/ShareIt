package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.common.EntityGetter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.dto.ItemRequestMapper.toItemRequest;
import static ru.practicum.shareit.request.dto.ItemRequestMapper.toItemRequestDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final EntityGetter entityGetter;

    @Override
    @Transactional(rollbackFor = NotFoundException.class)
    public ItemRequestDto create(long userId, ItemRequestDto itemRequestDto) {
        User requester = entityGetter.getUser(userId);
        ItemRequest itemRequest = toItemRequest(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequester(requester);
        itemRequest = itemRequestRepository.save(itemRequest);
        log.info("Request with ID: '" + itemRequest.getId() + "' of user with ID: '" + userId + "' successfully created");
        return toItemRequestDto(itemRequest);
    }

    @Override
    @Transactional(rollbackFor = NotFoundException.class, readOnly = true)
    public List<ItemRequestDto> getAllByUserId(long userId) {
        entityGetter.getUser(userId);
        Sort sort = Sort.by("created").descending();
        List<ItemRequest> requests = itemRequestRepository.findByRequesterId(userId, sort);
        List<ItemRequestDto> result = addItemsToRequests(requests);
        log.info("List of requests of user with ID: '" + userId + "' successfully received");
        return result;
    }

    @Override
    @Transactional(rollbackFor = NotFoundException.class, readOnly = true)
    public ItemRequestDto get(long userId, long requestId) {
        entityGetter.getUser(userId);
        ItemRequest itemRequest = entityGetter.getItemRequest(requestId)
                .orElseThrow(() -> new NotFoundException("Request with ID: '" + requestId + "' doesn't exist"));
        ItemRequestDto result = toItemRequestDto(itemRequest);
        List<ItemDto> itemsOfRequests = itemRepository.findByRequestId(requestId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        addItems(result, itemsOfRequests);
        log.info("Request with ID: '" + requestId + "' successfully received");
        return result;
    }

    @Override
    @Transactional(rollbackFor = NotFoundException.class, readOnly = true)
    public List<ItemRequestDto> getAll(long userId, int from, int size) {
        entityGetter.getUser(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdNot(userId, pageable);
        List<ItemRequestDto> result = addItemsToRequests(requests);
        log.info("All requests successfully received");
        return result;
    }

    private void addItems(ItemRequestDto itemRequestDto, List<ItemDto> itemsOfRequests) {
        itemRequestDto.setItems(itemsOfRequests.stream()
                .filter(i -> i.getRequestId() == itemRequestDto.getId())
                .collect(Collectors.toList()));
    }

    private List<ItemRequestDto> addItemsToRequests(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }
        List<ItemDto> itemsOfRequests = itemRepository.findByRequestIn(requests).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        List<ItemRequestDto> result = requests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        for (ItemRequestDto itemRequestDto : result) {
            addItems(itemRequestDto, itemsOfRequests);
        }
        return result;
    }
}