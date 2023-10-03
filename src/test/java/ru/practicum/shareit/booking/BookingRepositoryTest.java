package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    User owner;
    User booker;
    Item item;
    Booking booking;
    LocalDateTime start;
    LocalDateTime end;

    @BeforeEach
    void init() {
        start = LocalDateTime.now().minusHours(3);
        end = LocalDateTime.now().minusHours(2);
        owner = new User(0L, "owner", "owner@ya.ru");
        item = new Item(0L, "Дрель", "Обычная дрель", true, owner, null);
        booker = new User(0L, "booker", "booker@ya.ru");
        booking = new Booking(0L, start, end, item, booker, Status.WAITING);
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
        bookingRepository.save(booking);
    }

    @Test
    void should_find_by_booker_id_past_state() {
        List<Booking> bookings = bookingRepository.findByBookerIdPastState(booker.getId(), Pageable.unpaged());
        assertEquals(1, bookings.size());
        assertEquals("Дрель", bookings.get(0).getItem().getName());
        assertEquals("Обычная дрель", bookings.get(0).getItem().getDescription());
        assertEquals(true, bookings.get(0).getItem().getAvailable());
        assertEquals("owner", bookings.get(0).getItem().getOwner().getName());
        assertEquals("owner@ya.ru", bookings.get(0).getItem().getOwner().getEmail());
        assertEquals("booker", bookings.get(0).getBooker().getName());
        assertEquals("booker@ya.ru", bookings.get(0).getBooker().getEmail());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());
        booking.setStart(LocalDateTime.now().minusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(1));
        bookings = bookingRepository.findByBookerIdPastState(booker.getId(), Pageable.unpaged());
        assertTrue(bookings.isEmpty());
    }

    @Test
    void should_find_by_booker_id_future_state() {
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        List<Booking> bookings = bookingRepository.findByBookerIdFutureState(booker.getId(), Pageable.unpaged());
        assertEquals(1, bookings.size());
        assertEquals("Дрель", bookings.get(0).getItem().getName());
        assertEquals("Обычная дрель", bookings.get(0).getItem().getDescription());
        assertEquals(true, bookings.get(0).getItem().getAvailable());
        assertEquals("owner", bookings.get(0).getItem().getOwner().getName());
        assertEquals("owner@ya.ru", bookings.get(0).getItem().getOwner().getEmail());
        assertEquals("booker", bookings.get(0).getBooker().getName());
        assertEquals("booker@ya.ru", bookings.get(0).getBooker().getEmail());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());
        booking.setStart(LocalDateTime.now().minusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(1));
        bookings = bookingRepository.findByBookerIdFutureState(booker.getId(), Pageable.unpaged());
        assertTrue(bookings.isEmpty());
    }

    @Test
    void should_find_by_booker_id_current_state() {
        booking.setStart(LocalDateTime.now().minusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(1));
        List<Booking> bookings = bookingRepository.findByBookerIdCurrentState(booker.getId(), Pageable.unpaged());
        assertEquals(1, bookings.size());
        assertEquals("Дрель", bookings.get(0).getItem().getName());
        assertEquals("Обычная дрель", bookings.get(0).getItem().getDescription());
        assertEquals(true, bookings.get(0).getItem().getAvailable());
        assertEquals("owner", bookings.get(0).getItem().getOwner().getName());
        assertEquals("owner@ya.ru", bookings.get(0).getItem().getOwner().getEmail());
        assertEquals("booker", bookings.get(0).getBooker().getName());
        assertEquals("booker@ya.ru", bookings.get(0).getBooker().getEmail());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        bookings = bookingRepository.findByBookerIdCurrentState(booker.getId(), Pageable.unpaged());
        assertTrue(bookings.isEmpty());
    }

    @Test
    void should_find_by_item_owner_id_past_state() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdPastState(owner.getId(), Pageable.unpaged());
        assertEquals(1, bookings.size());
        assertEquals("Дрель", bookings.get(0).getItem().getName());
        assertEquals("Обычная дрель", bookings.get(0).getItem().getDescription());
        assertEquals(true, bookings.get(0).getItem().getAvailable());
        assertEquals("owner", bookings.get(0).getItem().getOwner().getName());
        assertEquals("owner@ya.ru", bookings.get(0).getItem().getOwner().getEmail());
        assertEquals("booker", bookings.get(0).getBooker().getName());
        assertEquals("booker@ya.ru", bookings.get(0).getBooker().getEmail());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());
        booking.setStart(LocalDateTime.now().minusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(1));
        bookings = bookingRepository.findByItemOwnerIdPastState(owner.getId(), Pageable.unpaged());
        assertTrue(bookings.isEmpty());
    }

    @Test
    void should_find_by_item_owner_id_future_state() {
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        List<Booking> bookings = bookingRepository.findByItemOwnerIdFutureState(owner.getId(), Pageable.unpaged());
        assertEquals(1, bookings.size());
        assertEquals("Дрель", bookings.get(0).getItem().getName());
        assertEquals("Обычная дрель", bookings.get(0).getItem().getDescription());
        assertEquals(true, bookings.get(0).getItem().getAvailable());
        assertEquals("owner", bookings.get(0).getItem().getOwner().getName());
        assertEquals("owner@ya.ru", bookings.get(0).getItem().getOwner().getEmail());
        assertEquals("booker", bookings.get(0).getBooker().getName());
        assertEquals("booker@ya.ru", bookings.get(0).getBooker().getEmail());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());
        booking.setStart(LocalDateTime.now().minusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(1));
        bookings = bookingRepository.findByItemOwnerIdFutureState(owner.getId(), Pageable.unpaged());
        assertTrue(bookings.isEmpty());
    }

    @Test
    void should_find_by_item_owner_id_current_state() {
        booking.setStart(LocalDateTime.now().minusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(1));
        List<Booking> bookings = bookingRepository.findByItemOwnerIdCurrentState(owner.getId(), Pageable.unpaged());
        assertEquals(1, bookings.size());
        assertEquals("Дрель", bookings.get(0).getItem().getName());
        assertEquals("Обычная дрель", bookings.get(0).getItem().getDescription());
        assertEquals(true, bookings.get(0).getItem().getAvailable());
        assertEquals("owner", bookings.get(0).getItem().getOwner().getName());
        assertEquals("owner@ya.ru", bookings.get(0).getItem().getOwner().getEmail());
        assertEquals("booker", bookings.get(0).getBooker().getName());
        assertEquals("booker@ya.ru", bookings.get(0).getBooker().getEmail());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        bookings = bookingRepository.findByItemOwnerIdCurrentState(owner.getId(), Pageable.unpaged());
        assertTrue(bookings.isEmpty());
    }

    @Test
    void should_find_by_booker_id_and_item_id_and_status_approved() {
        booking.setStatus(Status.APPROVED);
        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdAndStatusApproved(booker.getId(), item.getId());
        assertEquals(1, bookings.size());
        assertEquals("Дрель", bookings.get(0).getItem().getName());
        assertEquals("Обычная дрель", bookings.get(0).getItem().getDescription());
        assertEquals(true, bookings.get(0).getItem().getAvailable());
        assertEquals("owner", bookings.get(0).getItem().getOwner().getName());
        assertEquals("owner@ya.ru", bookings.get(0).getItem().getOwner().getEmail());
        assertEquals("booker", bookings.get(0).getBooker().getName());
        assertEquals("booker@ya.ru", bookings.get(0).getBooker().getEmail());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
        booking.setStatus(Status.REJECTED);
        bookings = bookingRepository.findByBookerIdAndItemIdAndStatusApproved(booker.getId(), item.getId());
        assertTrue(bookings.isEmpty());
    }

    @AfterEach
    void refresh() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}