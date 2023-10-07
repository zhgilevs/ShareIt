package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.common.EntityGetter;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OwnershipException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;

class ItemServiceImplTest {

    private ItemService itemService;
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private EntityGetter entityGetter;
    private User owner;
    private User booker;
    private Booking lastBooking;
    private Booking nextBooking;
    private ItemRequest request;
    private Comment comment;
    private CommentDto commentDto;
    private Item item;
    private ItemDto itemDto;
    private ItemInfoDto itemInfoDto;

    @BeforeEach
    void init() {
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        entityGetter = mock(EntityGetter.class);
        itemService = new ItemServiceImpl(itemRepository, bookingRepository, commentRepository, entityGetter);
        LocalDateTime requestCreated = LocalDateTime.now();
        LocalDateTime commentCreated = LocalDateTime.now().minusHours(1);
        owner = new User(1L, "owner", "owner@ya.ru");
        item = new Item(1L, "Дрель", "Обычная дрель", true, owner, null);
        booker = new User(2L, "booker", "booker@ya.ru");
        User requester = new User(3L, "requester", "requester@ya.ru");
        request = new ItemRequest(1L, "Нужна дрель", requestCreated, requester);
        lastBooking = new Booking(1L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2), item, booker, Status.APPROVED);
        nextBooking = new Booking(2L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3), item, booker, Status.APPROVED);
        BookingInfoDto lastBookingInfoDto = new BookingInfoDto(1L, 2L);
        BookingInfoDto nextBookingInfoDto = new BookingInfoDto(2L, 2L);
        comment = new Comment(1L, "Хорошая дрель", item, booker, commentCreated);
        commentDto = new CommentDto(1L, "Хорошая дрель", booker.getName(), commentCreated);
        itemDto = new ItemDto(1L, "Дрель", "Обычная дрель", true, null);
        itemInfoDto = new ItemInfoDto(1L, "Дрель", "Обычная дрель", true, lastBookingInfoDto, nextBookingInfoDto, new ArrayList<>(List.of(commentDto)));
    }

    @Test
    void should_not_create_item_without_owner() {
        when(entityGetter.getUser(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemService.create(itemDto.getId(), itemDto));
    }

    @Test
    void should_create_item() {
        when(entityGetter.getUser(anyLong())).thenReturn(owner);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        ItemDto returnedItemDto = itemService.create(1L, itemDto);
        assertEquals(itemDto.getId(), returnedItemDto.getId());
        assertEquals(itemDto.getName(), returnedItemDto.getName());
        assertEquals(itemDto.getDescription(), returnedItemDto.getDescription());
        assertEquals(itemDto.getAvailable(), returnedItemDto.getAvailable());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void should_create_item_to_request() {
        item.setRequest(request);
        itemDto.setRequestId(request.getId());
        when(entityGetter.getUser(anyLong())).thenReturn(owner);
        when(entityGetter.getItemRequest(anyLong())).thenReturn(Optional.ofNullable(request));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        ItemDto returnedItemDto = itemService.create(1L, itemDto);
        assertEquals(itemDto.getId(), returnedItemDto.getId());
        assertEquals(itemDto.getName(), returnedItemDto.getName());
        assertEquals(itemDto.getDescription(), returnedItemDto.getDescription());
        assertEquals(itemDto.getAvailable(), returnedItemDto.getAvailable());
        assertEquals(itemDto.getRequestId(), returnedItemDto.getRequestId());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void should_not_update_item_by_not_owner() {
        when(entityGetter.getUser(anyLong())).thenReturn(owner);
        when(entityGetter.getItem(anyLong())).thenReturn(item);
        assertThrows(OwnershipException.class, () -> itemService.update(2L, 1L, itemDto));
    }

    @Test
    void should_update_item_by_owner() {
        itemDto.setName("Super-Дрель");
        itemDto.setDescription("Невероятная дрель");
        itemDto.setAvailable(false);
        when(entityGetter.getUser(anyLong())).thenReturn(owner);
        when(entityGetter.getItem(anyLong())).thenReturn(item);
        ItemDto returnedItemDto = itemService.update(1L, 1L, itemDto);
        assertEquals(itemDto.getId(), returnedItemDto.getId());
        assertEquals(itemDto.getName(), returnedItemDto.getName());
        assertEquals(itemDto.getDescription(), returnedItemDto.getDescription());
        assertEquals(itemDto.getAvailable(), returnedItemDto.getAvailable());
    }

    @Test
    void should_update_item_without_updated_data() {
        itemDto.setName(null);
        itemDto.setDescription(null);
        itemDto.setAvailable(null);
        when(entityGetter.getUser(anyLong())).thenReturn(owner);
        when(entityGetter.getItem(anyLong())).thenReturn(item);
        ItemDto returnedItemDto = itemService.update(1L, 1L, itemDto);
        assertEquals(toItemDto(item).getId(), returnedItemDto.getId());
        assertEquals(toItemDto(item).getName(), returnedItemDto.getName());
        assertEquals(toItemDto(item).getDescription(), returnedItemDto.getDescription());
        assertEquals(toItemDto(item).getAvailable(), returnedItemDto.getAvailable());
    }

    @Test
    void should_not_get_all_without_owner() {
        when(entityGetter.getUser(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemService.getAll(1L, 0, 10));
    }

    @Test
    void should_get_all() {
        List<ItemInfoDto> expectedItems = List.of(itemInfoDto);
        when(entityGetter.getUser(anyLong())).thenReturn(owner);
        List<Item> items = List.of(item);
        when(itemRepository.findByOwnerId(anyLong(), any(Pageable.class))).thenReturn(items);
        List<Booking> bookings = List.of(lastBooking, nextBooking);
        when(bookingRepository.findByItemOwnerIdAndItemIn(anyLong(), anyList(), any(Pageable.class))).thenReturn(bookings);
        List<Comment> comments = List.of(comment);
        when(commentRepository.findByItemIn(anyList())).thenReturn(comments);
        List<ItemInfoDto> returnedItems = itemService.getAll(1L, 0, 10);
        assertEquals(expectedItems.size(), returnedItems.size());
        assertEquals(expectedItems.get(0).getId(), returnedItems.get(0).getId());
        assertEquals(expectedItems.get(0).getName(), returnedItems.get(0).getName());
        assertEquals(expectedItems.get(0).getDescription(), returnedItems.get(0).getDescription());
        assertEquals(expectedItems.get(0).getAvailable(), returnedItems.get(0).getAvailable());
        assertEquals(expectedItems.get(0).getLastBooking().getId(), returnedItems.get(0).getLastBooking().getId());
        assertEquals(expectedItems.get(0).getLastBooking().getBookerId(), returnedItems.get(0).getLastBooking().getBookerId());
        assertEquals(expectedItems.get(0).getNextBooking().getId(), returnedItems.get(0).getNextBooking().getId());
        assertEquals(expectedItems.get(0).getNextBooking().getBookerId(), returnedItems.get(0).getNextBooking().getBookerId());
        assertEquals(expectedItems.get(0).getComments().size(), returnedItems.get(0).getComments().size());
    }

    @Test
    void should_not_get_without_owner() {
        when(entityGetter.getUser(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemService.get(1L, 1L));
    }

    @Test
    void should_not_get_not_existing_item() {
        when(entityGetter.getItem(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemService.get(1L, 1L));
    }

    @Test
    void should_get_item_by_id() {
        when(entityGetter.getUser(anyLong())).thenReturn(owner);
        when(entityGetter.getItem(anyLong())).thenReturn(item);
        List<Comment> comments = List.of(comment);
        when(commentRepository.findByItemIn(List.of(item))).thenReturn(comments);
        List<Booking> bookings = List.of(lastBooking, nextBooking);
        when(bookingRepository.findByItemId(anyLong())).thenReturn(bookings);
        ItemInfoDto returnedItemInfoDto = itemService.get(1L, 1L);
        assertEquals(itemInfoDto.getId(), returnedItemInfoDto.getId());
        assertEquals(itemInfoDto.getName(), returnedItemInfoDto.getName());
        assertEquals(itemInfoDto.getDescription(), returnedItemInfoDto.getDescription());
        assertEquals(itemInfoDto.getAvailable(), returnedItemInfoDto.getAvailable());
        assertEquals(itemInfoDto.getLastBooking().getId(), returnedItemInfoDto.getLastBooking().getId());
        assertEquals(itemInfoDto.getLastBooking().getBookerId(), returnedItemInfoDto.getLastBooking().getBookerId());
        assertEquals(itemInfoDto.getNextBooking().getId(), returnedItemInfoDto.getNextBooking().getId());
        assertEquals(itemInfoDto.getNextBooking().getBookerId(), returnedItemInfoDto.getNextBooking().getBookerId());
        assertEquals(itemInfoDto.getComments().size(), returnedItemInfoDto.getComments().size());
    }

    @Test
    void should_search_empty_text() {
        List<ItemDto> returned = itemService.search("", 0, 10);
        assertTrue(returned.isEmpty());
    }

    @Test
    void should_search_items() {
        List<ItemDto> expected = List.of(itemDto);
        when(itemRepository.search(anyString(), any(Pageable.class))).thenReturn(List.of(item));
        List<ItemDto> returned = itemService.search("Дрель", 0, 10);
        assertEquals(expected.get(0).getId(), returned.get(0).getId());
        assertEquals(expected.get(0).getName(), returned.get(0).getName());
        assertEquals(expected.get(0).getDescription(), returned.get(0).getDescription());
        assertEquals(expected.get(0).getAvailable(), returned.get(0).getAvailable());
        assertEquals(expected.get(0).getRequestId(), returned.get(0).getRequestId());
    }

    @Test
    void should_not_create_comment_by_not_existing_user() {
        when(entityGetter.getUser(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemService.createComment(1L, 1L, commentDto));
    }

    @Test
    void should_not_create_comment_to_not_existing_item() {
        when(entityGetter.getItem(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemService.createComment(1L, 1L, commentDto));
    }

    @Test
    void should_not_create_comment_without_approved_bookings() {
        when(bookingRepository.findByBookerIdAndItemIdAndStatusApproved(anyLong(), anyLong())).thenReturn(new ArrayList<>());
        assertThrows(NotAvailableException.class, () -> itemService.createComment(1L, 1L, commentDto));
    }

    @Test
    void should_not_create_comment_without_finished_bookings() {
        lastBooking.setStart(LocalDateTime.now().plusHours(10));
        lastBooking.setEnd(LocalDateTime.now().plusHours(11));
        List<Booking> bookings = List.of(lastBooking, nextBooking);
        when(bookingRepository.findByBookerIdAndItemIdAndStatusApproved(anyLong(), anyLong())).thenReturn(bookings);
        assertThrows(NotAvailableException.class, () -> itemService.createComment(1L, 1L, commentDto));
    }

    @Test
    void should_create_comment() {
        when(entityGetter.getUser(anyLong())).thenReturn(booker);
        when(entityGetter.getItem(anyLong())).thenReturn(item);
        List<Booking> bookings = List.of(lastBooking, nextBooking);
        when(bookingRepository.findByBookerIdAndItemIdAndStatusApproved(anyLong(), anyLong())).thenReturn(bookings);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        CommentDto returnedCommentDto = itemService.createComment(1L, 1L, commentDto);
        assertEquals(commentDto.getId(), returnedCommentDto.getId());
        assertEquals(commentDto.getText(), returnedCommentDto.getText());
        assertEquals(commentDto.getAuthorName(), returnedCommentDto.getAuthorName());
        assertEquals(commentDto.getCreated(), returnedCommentDto.getCreated());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }
}