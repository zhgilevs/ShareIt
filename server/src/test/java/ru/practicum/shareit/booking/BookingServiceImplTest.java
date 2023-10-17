package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.common.EntityGetter;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    private BookingServiceImpl bookingService;
    private BookingRepository bookingRepository;
    private EntityGetter entityGetter;
    private User owner;
    private User booker;
    private User justUser;
    private Item item;
    private Booking booking;
    private BookingRequestDto bookingRequestDto;
    private BookingResponseDto bookingResponseDto;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void init() {
        bookingRepository = mock(BookingRepository.class);
        entityGetter = mock(EntityGetter.class);
        bookingService = new BookingServiceImpl(bookingRepository, entityGetter);
        start = LocalDateTime.now().plusHours(1);
        end = LocalDateTime.now().plusHours(3);
        owner = new User(1L, "owner", "owner@ya.ru");
        booker = new User(2L, "booker", "booker@ya.ru");
        justUser = new User(3L, "justUser", "justUser@ys.ru");
        UserDto bookerDto = new UserDto(2L, "booker", "booker@ya.ru");
        item = new Item(1L, "Дрель", "Простая дрель", true, owner, null);
        ItemDto itemDto = new ItemDto(1L, "Дрель", "Простая дрель", true, null);
        bookingRequestDto = new BookingRequestDto(1L, start, end);
        bookingResponseDto = new BookingResponseDto(1L, start, end, Status.WAITING, bookerDto, itemDto);
        booking = new Booking(1L, start, end, item, booker, Status.WAITING);
    }

    @Test
    void should_not_create_booking_with_start_after_end() {
        start = LocalDateTime.now().plusHours(1);
        end = LocalDateTime.now().minusHours(1);
        bookingRequestDto.setStart(start);
        bookingRequestDto.setEnd(end);
        assertThrows(TimeValidationException.class, () -> bookingService.create(2L, bookingRequestDto));
    }

    @Test
    void should_not_create_booking_with_start_equals_end() {
        start = end;
        bookingRequestDto.setStart(start);
        assertThrows(TimeValidationException.class, () -> bookingService.create(2L, bookingRequestDto));
    }

    @Test
    void should_not_create_booking_for_not_existing_item() {
        when(entityGetter.getItem(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> bookingService.create(2L, bookingRequestDto));
    }

    @Test
    void should_not_create_booking_by_owner() {
        when(entityGetter.getItem(anyLong())).thenReturn(item);
        assertThrows(PermissionException.class, () -> bookingService.create(1L, bookingRequestDto));
    }

    @Test
    void should_not_create_booking_for_not_available_item() {
        item.setAvailable(false);
        when(entityGetter.getItem(anyLong())).thenReturn(item);
        assertThrows(NotAvailableException.class, () -> bookingService.create(2L, bookingRequestDto));
    }

    @Test
    void should_create_booking() {
        when(entityGetter.getItem(anyLong())).thenReturn(item);
        when(entityGetter.getUser(anyLong())).thenReturn(booker);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingResponseDto returnedBookingResponseDto = bookingService.create(2L, bookingRequestDto);
        assertEquals(bookingResponseDto.getId(), returnedBookingResponseDto.getId());
        assertEquals(bookingResponseDto.getStart(), returnedBookingResponseDto.getStart());
        assertEquals(bookingResponseDto.getEnd(), returnedBookingResponseDto.getEnd());
        assertEquals(bookingResponseDto.getStatus(), returnedBookingResponseDto.getStatus());
        assertEquals(bookingResponseDto.getBooker().getId(), returnedBookingResponseDto.getBooker().getId());
        assertEquals(bookingResponseDto.getBooker().getName(), returnedBookingResponseDto.getBooker().getName());
        assertEquals(bookingResponseDto.getBooker().getEmail(), returnedBookingResponseDto.getBooker().getEmail());
        assertEquals(bookingResponseDto.getItem().getId(), returnedBookingResponseDto.getItem().getId());
        assertEquals(bookingResponseDto.getItem().getName(), returnedBookingResponseDto.getItem().getName());
        assertEquals(bookingResponseDto.getItem().getDescription(), returnedBookingResponseDto.getItem().getDescription());
        assertEquals(bookingResponseDto.getItem().getAvailable(), returnedBookingResponseDto.getItem().getAvailable());
        assertEquals(bookingResponseDto.getItem().getRequestId(), returnedBookingResponseDto.getItem().getRequestId());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void should_not_update_status_without_owner() {
        when(entityGetter.getUser(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> bookingService.updateStatus(1L, 1L, true));
    }

    @Test
    void should_not_update_status_of_not_existing_booking() {
        when(entityGetter.getBooking(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> bookingService.updateStatus(1L, 1L, true));
    }

    @Test
    void should_not_update_status_by_not_owner() {
        when(entityGetter.getUser(anyLong())).thenReturn(booker);
        when(entityGetter.getBooking(anyLong())).thenReturn(booking);
        assertThrows(PermissionException.class, () -> bookingService.updateStatus(1L, 1L, true));
    }

    @Test
    void should_not_update_already_approved_status() {
        booking.setStatus(Status.APPROVED);
        when(entityGetter.getUser(anyLong())).thenReturn(owner);
        when(entityGetter.getBooking(anyLong())).thenReturn(booking);
        assertThrows(NotAvailableException.class, () -> bookingService.updateStatus(1L, 1L, true));
    }

    @Test
    void should_update_status_to_approved() {
        bookingResponseDto.setStatus(Status.APPROVED);
        when(entityGetter.getUser(anyLong())).thenReturn(owner);
        when(entityGetter.getBooking(anyLong())).thenReturn(booking);
        BookingResponseDto returnedBookingResponseDto = bookingService.updateStatus(1L, 1L, true);
        assertEquals(bookingResponseDto.getId(), returnedBookingResponseDto.getId());
        assertEquals(bookingResponseDto.getStart(), returnedBookingResponseDto.getStart());
        assertEquals(bookingResponseDto.getEnd(), returnedBookingResponseDto.getEnd());
        assertEquals(bookingResponseDto.getStatus(), returnedBookingResponseDto.getStatus());
        assertEquals(bookingResponseDto.getBooker().getId(), returnedBookingResponseDto.getBooker().getId());
        assertEquals(bookingResponseDto.getBooker().getName(), returnedBookingResponseDto.getBooker().getName());
        assertEquals(bookingResponseDto.getBooker().getEmail(), returnedBookingResponseDto.getBooker().getEmail());
        assertEquals(bookingResponseDto.getItem().getId(), returnedBookingResponseDto.getItem().getId());
        assertEquals(bookingResponseDto.getItem().getName(), returnedBookingResponseDto.getItem().getName());
        assertEquals(bookingResponseDto.getItem().getDescription(), returnedBookingResponseDto.getItem().getDescription());
        assertEquals(bookingResponseDto.getItem().getAvailable(), returnedBookingResponseDto.getItem().getAvailable());
        assertEquals(bookingResponseDto.getItem().getRequestId(), returnedBookingResponseDto.getItem().getRequestId());
    }

    @Test
    void should_update_status_to_rejected() {
        bookingResponseDto.setStatus(Status.REJECTED);
        when(entityGetter.getUser(anyLong())).thenReturn(owner);
        when(entityGetter.getBooking(anyLong())).thenReturn(booking);
        BookingResponseDto returnedBookingResponseDto = bookingService.updateStatus(1L, 1L, false);
        assertEquals(bookingResponseDto.getId(), returnedBookingResponseDto.getId());
        assertEquals(bookingResponseDto.getStart(), returnedBookingResponseDto.getStart());
        assertEquals(bookingResponseDto.getEnd(), returnedBookingResponseDto.getEnd());
        assertEquals(bookingResponseDto.getStatus(), returnedBookingResponseDto.getStatus());
        assertEquals(bookingResponseDto.getBooker().getId(), returnedBookingResponseDto.getBooker().getId());
        assertEquals(bookingResponseDto.getBooker().getName(), returnedBookingResponseDto.getBooker().getName());
        assertEquals(bookingResponseDto.getBooker().getEmail(), returnedBookingResponseDto.getBooker().getEmail());
        assertEquals(bookingResponseDto.getItem().getId(), returnedBookingResponseDto.getItem().getId());
        assertEquals(bookingResponseDto.getItem().getName(), returnedBookingResponseDto.getItem().getName());
        assertEquals(bookingResponseDto.getItem().getDescription(), returnedBookingResponseDto.getItem().getDescription());
        assertEquals(bookingResponseDto.getItem().getAvailable(), returnedBookingResponseDto.getItem().getAvailable());
        assertEquals(bookingResponseDto.getItem().getRequestId(), returnedBookingResponseDto.getItem().getRequestId());
    }

    @Test
    void should_not_get_booking_by_not_existing_user() {
        when(entityGetter.getUser(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> bookingService.get(1L, 1L));
    }

    @Test
    void should_not_get_not_existing_booking() {
        when(entityGetter.getBooking(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> bookingService.get(1L, 1L));
    }

    @Test
    void should_not_get_booking_by_not_owner_and_not_booker() {
        when(entityGetter.getUser(anyLong())).thenReturn(justUser);
        when(entityGetter.getBooking(anyLong())).thenReturn(booking);
        assertThrows(PermissionException.class, () -> bookingService.get(3L, 1L));
    }

    @Test
    void should_get_booking_by_booker() {
        when(entityGetter.getUser(anyLong())).thenReturn(booker);
        when(entityGetter.getBooking(anyLong())).thenReturn(booking);
        when(bookingRepository.findByIdAndBookerId(anyLong(), anyLong())).thenReturn(booking);
        BookingResponseDto returnedBookingResponseDto = bookingService.get(2L, 1L);
        assertEquals(bookingResponseDto.getId(), returnedBookingResponseDto.getId());
        assertEquals(bookingResponseDto.getStart(), returnedBookingResponseDto.getStart());
        assertEquals(bookingResponseDto.getEnd(), returnedBookingResponseDto.getEnd());
        assertEquals(bookingResponseDto.getStatus(), returnedBookingResponseDto.getStatus());
        assertEquals(bookingResponseDto.getBooker().getId(), returnedBookingResponseDto.getBooker().getId());
        assertEquals(bookingResponseDto.getBooker().getName(), returnedBookingResponseDto.getBooker().getName());
        assertEquals(bookingResponseDto.getBooker().getEmail(), returnedBookingResponseDto.getBooker().getEmail());
        assertEquals(bookingResponseDto.getItem().getId(), returnedBookingResponseDto.getItem().getId());
        assertEquals(bookingResponseDto.getItem().getName(), returnedBookingResponseDto.getItem().getName());
        assertEquals(bookingResponseDto.getItem().getDescription(), returnedBookingResponseDto.getItem().getDescription());
        assertEquals(bookingResponseDto.getItem().getAvailable(), returnedBookingResponseDto.getItem().getAvailable());
        assertEquals(bookingResponseDto.getItem().getRequestId(), returnedBookingResponseDto.getItem().getRequestId());
    }

    @Test
    void should_get_booking_by_owner() {
        when(entityGetter.getUser(anyLong())).thenReturn(owner);
        when(entityGetter.getBooking(anyLong())).thenReturn(booking);
        when(bookingRepository.findByIdAndItemOwnerId(anyLong(), anyLong())).thenReturn(booking);
        BookingResponseDto returnedBookingResponseDto = bookingService.get(1L, 1L);
        assertEquals(bookingResponseDto.getId(), returnedBookingResponseDto.getId());
        assertEquals(bookingResponseDto.getStart(), returnedBookingResponseDto.getStart());
        assertEquals(bookingResponseDto.getEnd(), returnedBookingResponseDto.getEnd());
        assertEquals(bookingResponseDto.getStatus(), returnedBookingResponseDto.getStatus());
        assertEquals(bookingResponseDto.getBooker().getId(), returnedBookingResponseDto.getBooker().getId());
        assertEquals(bookingResponseDto.getBooker().getName(), returnedBookingResponseDto.getBooker().getName());
        assertEquals(bookingResponseDto.getBooker().getEmail(), returnedBookingResponseDto.getBooker().getEmail());
        assertEquals(bookingResponseDto.getItem().getId(), returnedBookingResponseDto.getItem().getId());
        assertEquals(bookingResponseDto.getItem().getName(), returnedBookingResponseDto.getItem().getName());
        assertEquals(bookingResponseDto.getItem().getDescription(), returnedBookingResponseDto.getItem().getDescription());
        assertEquals(bookingResponseDto.getItem().getAvailable(), returnedBookingResponseDto.getItem().getAvailable());
        assertEquals(bookingResponseDto.getItem().getRequestId(), returnedBookingResponseDto.getItem().getRequestId());
    }

    @Test
    void should_not_get_by_not_existing_booker() {
        when(entityGetter.getUser(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class,
                () -> bookingService.getByBookerId(2L, State.ALL, 0, 10));
    }

    @Test
    void should_get_by_booker_id_with_all_state() {
        when(entityGetter.getUser(anyLong())).thenReturn(booker);
        List<Booking> result = List.of(booking);
        when(bookingRepository.findByBookerId(anyLong(), any(Pageable.class))).thenReturn(result);
        List<BookingResponseDto> returned = bookingService.getByBookerId(1L, State.ALL, 0, 10);
        List<BookingResponseDto> expected = List.of(bookingResponseDto);
        assertEquals(returned.size(), expected.size());
        assertEquals(expected.get(0).getId(), returned.get(0).getId());
        assertEquals(expected.get(0).getStart(), returned.get(0).getStart());
        assertEquals(expected.get(0).getEnd(), returned.get(0).getEnd());
        assertEquals(expected.get(0).getStatus(), returned.get(0).getStatus());
        assertEquals(expected.get(0).getBooker().getId(), returned.get(0).getBooker().getId());
        assertEquals(expected.get(0).getBooker().getName(), returned.get(0).getBooker().getName());
        assertEquals(expected.get(0).getBooker().getEmail(), returned.get(0).getBooker().getEmail());
        assertEquals(expected.get(0).getItem().getId(), returned.get(0).getItem().getId());
        assertEquals(expected.get(0).getItem().getName(), returned.get(0).getItem().getName());
        assertEquals(expected.get(0).getItem().getDescription(), returned.get(0).getItem().getDescription());
        assertEquals(expected.get(0).getItem().getAvailable(), returned.get(0).getItem().getAvailable());
        assertEquals(expected.get(0).getItem().getRequestId(), returned.get(0).getItem().getRequestId());
    }

    @Test
    void should_get_by_booker_id_with_past_state() {
        start = LocalDateTime.now().minusHours(3);
        end = LocalDateTime.now().minusHours(1);
        booking.setStart(start);
        bookingResponseDto.setStart(start);
        booking.setEnd(end);
        bookingResponseDto.setEnd(end);
        when(entityGetter.getUser(anyLong())).thenReturn(booker);
        List<Booking> result = List.of(booking);
        when(bookingRepository.findByBookerIdPastState(anyLong(), any(Pageable.class))).thenReturn(result);
        List<BookingResponseDto> returned = bookingService.getByBookerId(2L, State.PAST, 0, 10);
        List<BookingResponseDto> expected = List.of(bookingResponseDto);
        assertEquals(returned.size(), expected.size());
        assertEquals(expected.get(0).getId(), returned.get(0).getId());
        assertEquals(expected.get(0).getStart(), returned.get(0).getStart());
        assertEquals(expected.get(0).getEnd(), returned.get(0).getEnd());
        assertEquals(expected.get(0).getStatus(), returned.get(0).getStatus());
        assertEquals(expected.get(0).getBooker().getId(), returned.get(0).getBooker().getId());
        assertEquals(expected.get(0).getBooker().getName(), returned.get(0).getBooker().getName());
        assertEquals(expected.get(0).getBooker().getEmail(), returned.get(0).getBooker().getEmail());
        assertEquals(expected.get(0).getItem().getId(), returned.get(0).getItem().getId());
        assertEquals(expected.get(0).getItem().getName(), returned.get(0).getItem().getName());
        assertEquals(expected.get(0).getItem().getDescription(), returned.get(0).getItem().getDescription());
        assertEquals(expected.get(0).getItem().getAvailable(), returned.get(0).getItem().getAvailable());
        assertEquals(expected.get(0).getItem().getRequestId(), returned.get(0).getItem().getRequestId());
    }

    @Test
    void should_get_by_booker_id_with_future_state() {
        when(entityGetter.getUser(anyLong())).thenReturn(booker);
        List<Booking> result = List.of(booking);
        when(bookingRepository.findByBookerIdFutureState(anyLong(), any(Pageable.class))).thenReturn(result);
        List<BookingResponseDto> returned = bookingService.getByBookerId(2L, State.FUTURE, 0, 10);
        List<BookingResponseDto> expected = List.of(bookingResponseDto);
        assertEquals(returned.size(), expected.size());
        assertEquals(expected.get(0).getId(), returned.get(0).getId());
        assertEquals(expected.get(0).getStart(), returned.get(0).getStart());
        assertEquals(expected.get(0).getEnd(), returned.get(0).getEnd());
        assertEquals(expected.get(0).getStatus(), returned.get(0).getStatus());
        assertEquals(expected.get(0).getBooker().getId(), returned.get(0).getBooker().getId());
        assertEquals(expected.get(0).getBooker().getName(), returned.get(0).getBooker().getName());
        assertEquals(expected.get(0).getBooker().getEmail(), returned.get(0).getBooker().getEmail());
        assertEquals(expected.get(0).getItem().getId(), returned.get(0).getItem().getId());
        assertEquals(expected.get(0).getItem().getName(), returned.get(0).getItem().getName());
        assertEquals(expected.get(0).getItem().getDescription(), returned.get(0).getItem().getDescription());
        assertEquals(expected.get(0).getItem().getAvailable(), returned.get(0).getItem().getAvailable());
        assertEquals(expected.get(0).getItem().getRequestId(), returned.get(0).getItem().getRequestId());
    }

    @Test
    void should_get_by_booker_id_with_current_state() {
        start = LocalDateTime.now().minusHours(3);
        end = LocalDateTime.now().plusHours(3);
        booking.setStart(start);
        bookingResponseDto.setStart(start);
        booking.setEnd(end);
        bookingResponseDto.setEnd(end);
        when(entityGetter.getUser(anyLong())).thenReturn(booker);
        List<Booking> result = List.of(booking);
        when(bookingRepository.findByBookerIdCurrentState(anyLong(), any(Pageable.class))).thenReturn(result);
        List<BookingResponseDto> returned = bookingService.getByBookerId(2L, State.CURRENT, 0, 10);
        List<BookingResponseDto> expected = List.of(bookingResponseDto);
        assertEquals(returned.size(), expected.size());
        assertEquals(expected.get(0).getId(), returned.get(0).getId());
        assertEquals(expected.get(0).getStart(), returned.get(0).getStart());
        assertEquals(expected.get(0).getEnd(), returned.get(0).getEnd());
        assertEquals(expected.get(0).getStatus(), returned.get(0).getStatus());
        assertEquals(expected.get(0).getBooker().getId(), returned.get(0).getBooker().getId());
        assertEquals(expected.get(0).getBooker().getName(), returned.get(0).getBooker().getName());
        assertEquals(expected.get(0).getBooker().getEmail(), returned.get(0).getBooker().getEmail());
        assertEquals(expected.get(0).getItem().getId(), returned.get(0).getItem().getId());
        assertEquals(expected.get(0).getItem().getName(), returned.get(0).getItem().getName());
        assertEquals(expected.get(0).getItem().getDescription(), returned.get(0).getItem().getDescription());
        assertEquals(expected.get(0).getItem().getAvailable(), returned.get(0).getItem().getAvailable());
        assertEquals(expected.get(0).getItem().getRequestId(), returned.get(0).getItem().getRequestId());
    }

    @Test
    void should_get_by_booker_id_with_waiting_state() {
        when(entityGetter.getUser(anyLong())).thenReturn(booker);
        List<Booking> result = List.of(booking);
        when(bookingRepository.findByBookerIdAndStatus(anyLong(), any(Status.class), any(Pageable.class))).thenReturn(result);
        List<BookingResponseDto> returned = bookingService.getByBookerId(2L, State.WAITING, 0, 10);
        List<BookingResponseDto> expected = List.of(bookingResponseDto);
        assertEquals(returned.size(), expected.size());
        assertEquals(expected.get(0).getId(), returned.get(0).getId());
        assertEquals(expected.get(0).getStart(), returned.get(0).getStart());
        assertEquals(expected.get(0).getEnd(), returned.get(0).getEnd());
        assertEquals(expected.get(0).getStatus(), returned.get(0).getStatus());
        assertEquals(expected.get(0).getBooker().getId(), returned.get(0).getBooker().getId());
        assertEquals(expected.get(0).getBooker().getName(), returned.get(0).getBooker().getName());
        assertEquals(expected.get(0).getBooker().getEmail(), returned.get(0).getBooker().getEmail());
        assertEquals(expected.get(0).getItem().getId(), returned.get(0).getItem().getId());
        assertEquals(expected.get(0).getItem().getName(), returned.get(0).getItem().getName());
        assertEquals(expected.get(0).getItem().getDescription(), returned.get(0).getItem().getDescription());
        assertEquals(expected.get(0).getItem().getAvailable(), returned.get(0).getItem().getAvailable());
        assertEquals(expected.get(0).getItem().getRequestId(), returned.get(0).getItem().getRequestId());
    }

    @Test
    void should_get_by_booker_id_with_rejected_state() {
        booking.setStatus(Status.REJECTED);
        bookingResponseDto.setStatus(Status.REJECTED);
        when(entityGetter.getUser(anyLong())).thenReturn(booker);
        List<Booking> result = List.of(booking);
        when(bookingRepository.findByBookerIdAndStatus(anyLong(), any(Status.class), any(Pageable.class))).thenReturn(result);
        List<BookingResponseDto> returned = bookingService.getByBookerId(2L, State.REJECTED, 0, 10);
        List<BookingResponseDto> expected = List.of(bookingResponseDto);
        assertEquals(returned.size(), expected.size());
        assertEquals(expected.get(0).getId(), returned.get(0).getId());
        assertEquals(expected.get(0).getStart(), returned.get(0).getStart());
        assertEquals(expected.get(0).getEnd(), returned.get(0).getEnd());
        assertEquals(expected.get(0).getStatus(), returned.get(0).getStatus());
        assertEquals(expected.get(0).getBooker().getId(), returned.get(0).getBooker().getId());
        assertEquals(expected.get(0).getBooker().getName(), returned.get(0).getBooker().getName());
        assertEquals(expected.get(0).getBooker().getEmail(), returned.get(0).getBooker().getEmail());
        assertEquals(expected.get(0).getItem().getId(), returned.get(0).getItem().getId());
        assertEquals(expected.get(0).getItem().getName(), returned.get(0).getItem().getName());
        assertEquals(expected.get(0).getItem().getDescription(), returned.get(0).getItem().getDescription());
        assertEquals(expected.get(0).getItem().getAvailable(), returned.get(0).getItem().getAvailable());
        assertEquals(expected.get(0).getItem().getRequestId(), returned.get(0).getItem().getRequestId());
    }

    @Test
    void should_get_by_owner_id_with_all_state() {
        when(entityGetter.getUser(anyLong())).thenReturn(owner);
        List<Booking> result = List.of(booking);
        when(bookingRepository.findByItemOwnerId(anyLong(), any(Pageable.class))).thenReturn(result);
        List<BookingResponseDto> returned = bookingService.getByOwnerId(1L, State.ALL, 0, 10);
        List<BookingResponseDto> expected = List.of(bookingResponseDto);
        assertEquals(returned.size(), expected.size());
        assertEquals(expected.get(0).getId(), returned.get(0).getId());
        assertEquals(expected.get(0).getStart(), returned.get(0).getStart());
        assertEquals(expected.get(0).getEnd(), returned.get(0).getEnd());
        assertEquals(expected.get(0).getStatus(), returned.get(0).getStatus());
        assertEquals(expected.get(0).getBooker().getId(), returned.get(0).getBooker().getId());
        assertEquals(expected.get(0).getBooker().getName(), returned.get(0).getBooker().getName());
        assertEquals(expected.get(0).getBooker().getEmail(), returned.get(0).getBooker().getEmail());
        assertEquals(expected.get(0).getItem().getId(), returned.get(0).getItem().getId());
        assertEquals(expected.get(0).getItem().getName(), returned.get(0).getItem().getName());
        assertEquals(expected.get(0).getItem().getDescription(), returned.get(0).getItem().getDescription());
        assertEquals(expected.get(0).getItem().getAvailable(), returned.get(0).getItem().getAvailable());
        assertEquals(expected.get(0).getItem().getRequestId(), returned.get(0).getItem().getRequestId());
    }

    @Test
    void should_get_by_owner_id_with_past_state() {
        start = LocalDateTime.now().minusHours(3);
        end = LocalDateTime.now().minusHours(1);
        booking.setStart(start);
        bookingResponseDto.setStart(start);
        booking.setEnd(end);
        bookingResponseDto.setEnd(end);
        when(entityGetter.getUser(anyLong())).thenReturn(owner);
        List<Booking> result = List.of(booking);
        when(bookingRepository.findByItemOwnerIdPastState(anyLong(), any(Pageable.class))).thenReturn(result);
        List<BookingResponseDto> returned = bookingService.getByOwnerId(1L, State.PAST, 0, 10);
        List<BookingResponseDto> expected = List.of(bookingResponseDto);
        assertEquals(returned.size(), expected.size());
        assertEquals(expected.get(0).getId(), returned.get(0).getId());
        assertEquals(expected.get(0).getStart(), returned.get(0).getStart());
        assertEquals(expected.get(0).getEnd(), returned.get(0).getEnd());
        assertEquals(expected.get(0).getStatus(), returned.get(0).getStatus());
        assertEquals(expected.get(0).getBooker().getId(), returned.get(0).getBooker().getId());
        assertEquals(expected.get(0).getBooker().getName(), returned.get(0).getBooker().getName());
        assertEquals(expected.get(0).getBooker().getEmail(), returned.get(0).getBooker().getEmail());
        assertEquals(expected.get(0).getItem().getId(), returned.get(0).getItem().getId());
        assertEquals(expected.get(0).getItem().getName(), returned.get(0).getItem().getName());
        assertEquals(expected.get(0).getItem().getDescription(), returned.get(0).getItem().getDescription());
        assertEquals(expected.get(0).getItem().getAvailable(), returned.get(0).getItem().getAvailable());
        assertEquals(expected.get(0).getItem().getRequestId(), returned.get(0).getItem().getRequestId());
    }

    @Test
    void should_get_by_owner_id_with_future_state() {
        when(entityGetter.getUser(anyLong())).thenReturn(owner);
        List<Booking> result = List.of(booking);
        when(bookingRepository.findByItemOwnerIdFutureState(anyLong(), any(Pageable.class))).thenReturn(result);
        List<BookingResponseDto> returned = bookingService.getByOwnerId(1L, State.FUTURE, 0, 10);
        List<BookingResponseDto> expected = List.of(bookingResponseDto);
        assertEquals(returned.size(), expected.size());
        assertEquals(expected.get(0).getId(), returned.get(0).getId());
        assertEquals(expected.get(0).getStart(), returned.get(0).getStart());
        assertEquals(expected.get(0).getEnd(), returned.get(0).getEnd());
        assertEquals(expected.get(0).getStatus(), returned.get(0).getStatus());
        assertEquals(expected.get(0).getBooker().getId(), returned.get(0).getBooker().getId());
        assertEquals(expected.get(0).getBooker().getName(), returned.get(0).getBooker().getName());
        assertEquals(expected.get(0).getBooker().getEmail(), returned.get(0).getBooker().getEmail());
        assertEquals(expected.get(0).getItem().getId(), returned.get(0).getItem().getId());
        assertEquals(expected.get(0).getItem().getName(), returned.get(0).getItem().getName());
        assertEquals(expected.get(0).getItem().getDescription(), returned.get(0).getItem().getDescription());
        assertEquals(expected.get(0).getItem().getAvailable(), returned.get(0).getItem().getAvailable());
        assertEquals(expected.get(0).getItem().getRequestId(), returned.get(0).getItem().getRequestId());
    }

    @Test
    void should_get_by_owner_id_with_current_state() {
        start = LocalDateTime.now().minusHours(3);
        end = LocalDateTime.now().plusHours(3);
        booking.setStart(start);
        bookingResponseDto.setStart(start);
        booking.setEnd(end);
        bookingResponseDto.setEnd(end);
        when(entityGetter.getUser(anyLong())).thenReturn(owner);
        List<Booking> result = List.of(booking);
        when(bookingRepository.findByItemOwnerIdCurrentState(anyLong(), any(Pageable.class))).thenReturn(result);
        List<BookingResponseDto> returned = bookingService.getByOwnerId(1L, State.CURRENT, 0, 10);
        List<BookingResponseDto> expected = List.of(bookingResponseDto);
        assertEquals(returned.size(), expected.size());
        assertEquals(expected.get(0).getId(), returned.get(0).getId());
        assertEquals(expected.get(0).getStart(), returned.get(0).getStart());
        assertEquals(expected.get(0).getEnd(), returned.get(0).getEnd());
        assertEquals(expected.get(0).getStatus(), returned.get(0).getStatus());
        assertEquals(expected.get(0).getBooker().getId(), returned.get(0).getBooker().getId());
        assertEquals(expected.get(0).getBooker().getName(), returned.get(0).getBooker().getName());
        assertEquals(expected.get(0).getBooker().getEmail(), returned.get(0).getBooker().getEmail());
        assertEquals(expected.get(0).getItem().getId(), returned.get(0).getItem().getId());
        assertEquals(expected.get(0).getItem().getName(), returned.get(0).getItem().getName());
        assertEquals(expected.get(0).getItem().getDescription(), returned.get(0).getItem().getDescription());
        assertEquals(expected.get(0).getItem().getAvailable(), returned.get(0).getItem().getAvailable());
        assertEquals(expected.get(0).getItem().getRequestId(), returned.get(0).getItem().getRequestId());
    }

    @Test
    void should_get_by_owner_id_with_waiting_state() {
        when(entityGetter.getUser(anyLong())).thenReturn(owner);
        List<Booking> result = List.of(booking);
        when(bookingRepository.findByItemOwnerIdAndStatus(anyLong(), any(Status.class), any(Pageable.class))).thenReturn(result);
        List<BookingResponseDto> returned = bookingService.getByOwnerId(1L, State.WAITING, 0, 10);
        List<BookingResponseDto> expected = List.of(bookingResponseDto);
        assertEquals(returned.size(), expected.size());
        assertEquals(expected.get(0).getId(), returned.get(0).getId());
        assertEquals(expected.get(0).getStart(), returned.get(0).getStart());
        assertEquals(expected.get(0).getEnd(), returned.get(0).getEnd());
        assertEquals(expected.get(0).getStatus(), returned.get(0).getStatus());
        assertEquals(expected.get(0).getBooker().getId(), returned.get(0).getBooker().getId());
        assertEquals(expected.get(0).getBooker().getName(), returned.get(0).getBooker().getName());
        assertEquals(expected.get(0).getBooker().getEmail(), returned.get(0).getBooker().getEmail());
        assertEquals(expected.get(0).getItem().getId(), returned.get(0).getItem().getId());
        assertEquals(expected.get(0).getItem().getName(), returned.get(0).getItem().getName());
        assertEquals(expected.get(0).getItem().getDescription(), returned.get(0).getItem().getDescription());
        assertEquals(expected.get(0).getItem().getAvailable(), returned.get(0).getItem().getAvailable());
        assertEquals(expected.get(0).getItem().getRequestId(), returned.get(0).getItem().getRequestId());
    }

    @Test
    void should_get_by_owner_id_with_rejected_state() {
        booking.setStatus(Status.REJECTED);
        bookingResponseDto.setStatus(Status.REJECTED);
        when(entityGetter.getUser(anyLong())).thenReturn(owner);
        List<Booking> result = List.of(booking);
        when(bookingRepository.findByItemOwnerIdAndStatus(anyLong(), any(Status.class), any(Pageable.class))).thenReturn(result);
        List<BookingResponseDto> returned = bookingService.getByOwnerId(1L, State.REJECTED, 0, 10);
        List<BookingResponseDto> expected = List.of(bookingResponseDto);
        assertEquals(returned.size(), expected.size());
        assertEquals(expected.get(0).getId(), returned.get(0).getId());
        assertEquals(expected.get(0).getStart(), returned.get(0).getStart());
        assertEquals(expected.get(0).getEnd(), returned.get(0).getEnd());
        assertEquals(expected.get(0).getStatus(), returned.get(0).getStatus());
        assertEquals(expected.get(0).getBooker().getId(), returned.get(0).getBooker().getId());
        assertEquals(expected.get(0).getBooker().getName(), returned.get(0).getBooker().getName());
        assertEquals(expected.get(0).getBooker().getEmail(), returned.get(0).getBooker().getEmail());
        assertEquals(expected.get(0).getItem().getId(), returned.get(0).getItem().getId());
        assertEquals(expected.get(0).getItem().getName(), returned.get(0).getItem().getName());
        assertEquals(expected.get(0).getItem().getDescription(), returned.get(0).getItem().getDescription());
        assertEquals(expected.get(0).getItem().getAvailable(), returned.get(0).getItem().getAvailable());
        assertEquals(expected.get(0).getItem().getRequestId(), returned.get(0).getItem().getRequestId());
    }
}