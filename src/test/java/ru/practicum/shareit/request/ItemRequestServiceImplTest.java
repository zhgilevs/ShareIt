package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.common.EntityGetter;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemRequestServiceImplTest {

    ItemRequestRepository itemRequestRepository;
    ItemRepository itemRepository;
    EntityGetter entityGetter;
    ItemRequestService itemRequestService;
    User requester;
    User anotherRequester;
    ItemRequest itemRequest;
    ItemRequestDto itemRequestDto;
    ItemDto itemDto;
    Item item;
    LocalDateTime created;

    @BeforeEach
    void init() {
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemRepository = mock(ItemRepository.class);
        entityGetter = mock(EntityGetter.class);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, itemRepository, entityGetter);
        requester = new User(1L, "requester", "requester@ya.ru");
        anotherRequester = new User(2L, "anotherRequester", "anotherRequester@ya.ru");
        created = LocalDateTime.now();
        itemRequest = new ItemRequest(1L, "Нужна дрель", created, requester);
        itemDto = new ItemDto(1L, "Дрель", "Обычная дрель", true, 1L);
        item = new Item(1L, "Дрель", "Обычная дрель", true, null, itemRequest);
        itemRequestDto = new ItemRequestDto(1L, "Нужна дрель", created, List.of(itemDto));
    }

    @Test
    void should_not_create_by_not_existing_user() {
        when(entityGetter.getUser(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemRequestService.create(1L, itemRequestDto));
    }

    @Test
    void should_create_item_request() {
        when(entityGetter.getUser(anyLong())).thenReturn(requester);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        itemRequestDto.setItems(null);
        ItemRequestDto returnedItemRequestDto = itemRequestService.create(1L, itemRequestDto);
        assertEquals(itemRequestDto.getId(), returnedItemRequestDto.getId());
        assertEquals(itemRequestDto.getDescription(), returnedItemRequestDto.getDescription());
        assertEquals(itemRequestDto.getItems(), returnedItemRequestDto.getItems());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void should_not_get_all_user_requests_by_not_existing_user() {
        when(entityGetter.getUser(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemRequestService.getAllByUserId(1L));
    }

    @Test
    void should_get_empty_list_of_user_requests() {
        when(entityGetter.getUser(anyLong())).thenReturn(requester);
        when(itemRequestRepository.findByRequesterId(anyLong(), any(Sort.class))).thenReturn(new ArrayList<>());
        List<ItemRequestDto> returned = itemRequestService.getAllByUserId(1L);
        assertTrue(returned.isEmpty());
    }

    @Test
    void should_get_all_requests_of_user() {
        when(entityGetter.getUser(anyLong())).thenReturn(requester);
        List<ItemRequest> requests = List.of(itemRequest);
        when(itemRequestRepository.findByRequesterId(anyLong(), any(Sort.class))).thenReturn(requests);
        when(itemRepository.findByRequestIn(anyList())).thenReturn(List.of(item));
        List<ItemRequestDto> expected = List.of(itemRequestDto);
        List<ItemRequestDto> returned = itemRequestService.getAllByUserId(1L);
        assertEquals(expected.size(), returned.size());
        assertEquals(expected.get(0).getId(), returned.get(0).getId());
        assertEquals(expected.get(0).getDescription(), returned.get(0).getDescription());
        assertEquals(expected.get(0).getCreated(), returned.get(0).getCreated());
        assertEquals(expected.get(0).getItems().size(), returned.get(0).getItems().size());
        assertEquals(expected.get(0).getItems().get(0).getId(), returned.get(0).getItems().get(0).getId());
        assertEquals(expected.get(0).getItems().get(0).getName(), returned.get(0).getItems().get(0).getName());
        assertEquals(expected.get(0).getItems().get(0).getDescription(), returned.get(0).getItems().get(0).getDescription());
        assertEquals(expected.get(0).getItems().get(0).getAvailable(), returned.get(0).getItems().get(0).getAvailable());
        assertEquals(expected.get(0).getItems().get(0).getRequestId(), returned.get(0).getItems().get(0).getRequestId());
    }

    @Test
    void should_not_get_by_not_existing_user() {
        when(entityGetter.getUser(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemRequestService.get(1L, 1L));
    }

    @Test
    void should_not_get_by_not_existing_request() {
        when(entityGetter.getItemRequest(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemRequestService.get(1L, 1L));
    }

    @Test
    void should_get_by_user_id_and_request_id() {
        when(entityGetter.getUser(anyLong())).thenReturn(requester);
        when(entityGetter.getItemRequest(anyLong())).thenReturn(Optional.ofNullable(itemRequest));
        List<Item> items = List.of(item);
        when(itemRepository.findByRequestId(anyLong())).thenReturn(items);
        ItemRequestDto returnedItemRequestDto = itemRequestService.get(1L, 1L);
        assertEquals(itemRequestDto.getId(), returnedItemRequestDto.getId());
        assertEquals(itemRequestDto.getDescription(), returnedItemRequestDto.getDescription());
        assertEquals(itemRequestDto.getItems(), returnedItemRequestDto.getItems());
    }

    @Test
    void should_not_get_all_requests_by_not_existing_user() {
        when(entityGetter.getUser(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemRequestService.getAll(1L, 0, 10));
    }

    @Test
    void should_get_empty_list_of_all_requests() {
        when(entityGetter.getUser(anyLong())).thenReturn(requester);
        when(itemRequestRepository.findByRequesterId(anyLong(), any(Sort.class))).thenReturn(new ArrayList<>());
        List<ItemRequestDto> returned = itemRequestService.getAll(1L, 0, 10);
        assertTrue(returned.isEmpty());
    }

    @Test
    void should_get_all_requests_by_requester_id_but_not_his_own_requests() {
        when(entityGetter.getUser(anyLong())).thenReturn(anotherRequester);
        List<ItemRequest> requests = List.of(itemRequest);
        when(itemRequestRepository.findByRequesterIdNot(anyLong(), any(Pageable.class))).thenReturn(requests);
        when(itemRepository.findByRequestIn(anyList())).thenReturn(List.of(item));
        List<ItemRequestDto> expected = List.of(itemRequestDto);
        List<ItemRequestDto> returned = itemRequestService.getAll(2L, 0, 10);
        assertEquals(expected.size(), returned.size());
        assertEquals(expected.get(0).getId(), returned.get(0).getId());
        assertEquals(expected.get(0).getDescription(), returned.get(0).getDescription());
        assertEquals(expected.get(0).getCreated(), returned.get(0).getCreated());
        assertEquals(expected.get(0).getItems().size(), returned.get(0).getItems().size());
        assertEquals(expected.get(0).getItems().get(0).getId(), returned.get(0).getItems().get(0).getId());
        assertEquals(expected.get(0).getItems().get(0).getName(), returned.get(0).getItems().get(0).getName());
        assertEquals(expected.get(0).getItems().get(0).getDescription(), returned.get(0).getItems().get(0).getDescription());
        assertEquals(expected.get(0).getItems().get(0).getAvailable(), returned.get(0).getItems().get(0).getAvailable());
        assertEquals(expected.get(0).getItems().get(0).getRequestId(), returned.get(0).getItems().get(0).getRequestId());
    }
}