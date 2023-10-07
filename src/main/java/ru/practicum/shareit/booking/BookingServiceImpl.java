package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.common.EntityGetter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.dto.BookingMapper.toBookingResponseDto;
import static ru.practicum.shareit.booking.State.parseState;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EntityGetter entityGetter;

    @Override
    @Transactional(rollbackFor = {NotFoundException.class, TimeValidationException.class, PermissionException.class, NotAvailableException.class})
    public BookingResponseDto create(long bookerId, BookingRequestDto bookingRequestDto) {
        LocalDateTime start = bookingRequestDto.getStart();
        LocalDateTime end = bookingRequestDto.getEnd();
        if (end.isBefore(start) || start.equals(end)) {
            throw new TimeValidationException("Start or End in request body is incorrect");
        }
        long itemId = bookingRequestDto.getItemId();
        Item item = entityGetter.getItem(itemId);
        if (item.getOwner().getId() == bookerId) {
            throw new PermissionException("Booker with ID: '" + bookerId + "' couldn't book his own item");
        }
        boolean available = item.getAvailable();
        if (!available) {
            throw new NotAvailableException("Item with ID: '" + itemId + "' is not available for booking");
        }
        User booker = entityGetter.getUser(bookerId);
        Booking booking = toBooking(bookingRequestDto);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        booking = bookingRepository.save(booking);
        log.info("Booking with ID: '" + booking.getId()
                + "' of user with ID: '" + booker.getId()
                + "' for item with ID: '" + itemId
                + "' successfully created");
        return toBookingResponseDto(booking);
    }

    @Override
    @Transactional(rollbackFor = {NotFoundException.class, PermissionException.class, NotAvailableException.class})
    public BookingResponseDto updateStatus(long ownerId, long bookingId, boolean approved) {
        User user = entityGetter.getUser(ownerId);
        Booking booking = entityGetter.getBooking(bookingId);
        if (user.getId() != booking.getItem().getOwner().getId()) {
            throw new PermissionException("User with ID: '" + ownerId + "' couldn't change status of booking");
        }
        if (approved) {
            if (booking.getStatus() == Status.APPROVED) {
                throw new NotAvailableException("Status of booking with ID:'" + bookingId + "' already approved");
            }
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        log.info("Status of booking with ID: '" + bookingId + "' updated to: " + booking.getStatus());
        return toBookingResponseDto(booking);
    }

    @Override
    @Transactional(rollbackFor = {NotFoundException.class, PermissionException.class}, readOnly = true)
    public BookingResponseDto get(long userId, long bookingId) {
        entityGetter.getUser(userId);
        Booking booking = entityGetter.getBooking(bookingId);
        long bookerId = booking.getBooker().getId();
        long itemOwnerId = booking.getItem().getOwner().getId();
        if (userId == bookerId) {
            booking = bookingRepository.findByIdAndBookerId(bookingId, bookerId);
        } else if (userId == itemOwnerId) {
            booking = bookingRepository.findByIdAndItemOwnerId(bookingId, itemOwnerId);
        } else {
            throw new PermissionException("User with ID: '" + userId + "' couldn't receive booking with ID: '" + bookingId + "'");
        }
        log.info("Booking with ID: '" + bookingId + "' by user with ID: '" + userId + "' successfully received");
        return toBookingResponseDto(booking);
    }

    @Override
    @Transactional(rollbackFor = {NotFoundException.class, UnsupportedStatusException.class}, readOnly = true)
    public List<BookingResponseDto> getByBookerId(long bookerId, String state, int from, int size) {
        State enumState = parseState(state);
        entityGetter.getUser(bookerId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        List<Booking> result = new ArrayList<>();
        switch (enumState) {
            case ALL:
                result = bookingRepository.findByBookerId(bookerId, pageable);
                break;
            case PAST:
                result = bookingRepository.findByBookerIdPastState(bookerId, pageable);
                break;
            case FUTURE:
                result = bookingRepository.findByBookerIdFutureState(bookerId, pageable);
                break;
            case CURRENT:
                result = bookingRepository.findByBookerIdCurrentState(bookerId, pageable);
                break;
            case WAITING:
                result = bookingRepository.findByBookerIdAndStatus(bookerId, Status.WAITING, pageable);
                break;
            case REJECTED:
                result = bookingRepository.findByBookerIdAndStatus(bookerId, Status.REJECTED, pageable);
                break;
        }
        log.info(result.size() + " bookings found by booker with ID: '" + bookerId + "'");
        return result.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = {NotFoundException.class, UnsupportedStatusException.class}, readOnly = true)
    public List<BookingResponseDto> getByOwnerId(long ownerId, String state, int from, int size) {
        State enumState = parseState(state);
        entityGetter.getUser(ownerId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        List<Booking> result = new ArrayList<>();
        switch (enumState) {
            case ALL:
                result = bookingRepository.findByItemOwnerId(ownerId, pageable);
                break;
            case PAST:
                result = bookingRepository.findByItemOwnerIdPastState(ownerId, pageable);
                break;
            case FUTURE:
                result = bookingRepository.findByItemOwnerIdFutureState(ownerId, pageable);
                break;
            case CURRENT:
                result = bookingRepository.findByItemOwnerIdCurrentState(ownerId, pageable);
                break;
            case WAITING:
                result = bookingRepository.findByItemOwnerIdAndStatus(ownerId, Status.WAITING, pageable);
                break;
            case REJECTED:
                result = bookingRepository.findByItemOwnerIdAndStatus(ownerId, Status.REJECTED, pageable);
                break;
        }
        log.info(result.size() + " bookings found by owner with ID: '" + ownerId + "'");
        return result.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }
}