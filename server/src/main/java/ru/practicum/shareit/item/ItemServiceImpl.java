package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OwnershipException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.common.EntityGetter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.BookingMapper.toBookingInfoDto;
import static ru.practicum.shareit.item.dto.CommentMapper.toComment;
import static ru.practicum.shareit.item.dto.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.dto.ItemMapper.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final EntityGetter entityGetter;

    @Override
    @Transactional(rollbackFor = NotFoundException.class)
    public ItemDto create(long userId, ItemDto itemDto) {
        User owner = entityGetter.getUser(userId);
        Optional<ItemRequest> request = Optional.empty();
        if (itemDto.getRequestId() != null) {
            request = entityGetter.getItemRequest(itemDto.getRequestId());
        }
        Item item = toItem(itemDto);
        item.setRequest(request.orElse(null));
        item.setOwner(owner);
        item = itemRepository.save(item);
        log.info("Item with ID: '" + item.getId() + "' of user with ID: '" + userId + "' successfully created");
        return toItemDto(item);
    }

    @Override
    @Transactional(rollbackFor = {NotFoundException.class, OwnershipException.class})
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        entityGetter.getUser(userId);
        Item item = entityGetter.getItem(itemId);
        if (userId != item.getOwner().getId()) {
            throw new OwnershipException("User with ID: '" + userId + "' not the owner of item with ID: '" + itemId + "'");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        log.info("Item with ID: '" + item.getId() + "' of user with ID: '" + userId + "' successfully updated");
        return toItemDto(item);
    }

    @Override
    @Transactional(rollbackFor = NotFoundException.class, readOnly = true)
    public List<ItemInfoDto> getAll(long userId, int from, int size) {
        entityGetter.getUser(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        List<Item> items = itemRepository.findByOwnerId(userId, pageable);
        List<ItemInfoDto> result = items.stream()
                .map(ItemMapper::toItemInfoDto)
                .collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndItemIn(userId, items, pageable);
        List<Comment> comments = commentRepository.findByItemIn(items);
        for (ItemInfoDto itemInfoDto : result) {
            addBookings(itemInfoDto, bookings);
            addComments(itemInfoDto, comments);
        }
        log.info("List of items of user with ID: '" + userId + "' successfully received");
        return result;
    }

    @Override
    @Transactional(rollbackFor = NotFoundException.class, readOnly = true)
    public ItemInfoDto get(long userId, long itemId) {
        User user = entityGetter.getUser(userId);
        Item item = entityGetter.getItem(itemId);
        List<Comment> comments = commentRepository.findByItemIn(List.of(item));
        ItemInfoDto result = toItemInfoDto(item);
        if (user.getId() == item.getOwner().getId()) {
            List<Booking> bookings = bookingRepository.findByItemId(itemId);
            addBookings(result, bookings);
        }
        addComments(result, comments);
        log.info("Item with ID: '" + itemId + "' of user with ID: '" + userId + "' successfully received");
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(long userId, String text, int from, int size) {
        if (text.isBlank()) {
            log.info("List of items is empty because of text is blank");
            return Collections.emptyList();
        }
        Pageable pageable = PageRequest.of(from, size, Sort.by("id"));
        List<Item> result = itemRepository.search(text, pageable);
        log.info(result.size() + " items founded by text: '" + text + "'");
        return result.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = {NotFoundException.class, NotAvailableException.class})
    public CommentDto createComment(long userId, long itemId, CommentDto commentDto) {
        User user = entityGetter.getUser(userId);
        Item item = entityGetter.getItem(itemId);
        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdAndStatusApproved(userId, itemId);
        if (bookings.isEmpty()) {
            throw new NotAvailableException("User without approved bookings couldn't make a comment");
        }
        boolean isAnyFinished = false;
        for (Booking booking : bookings) {
            if (booking.getEnd().isBefore(LocalDateTime.now())) {
                isAnyFinished = true;
                break;
            }
        }
        if (!isAnyFinished) {
            throw new NotAvailableException("User couldn't make a comment to item not booking yet");
        }
        Comment comment = toComment(commentDto);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        comment.setAuthor(user);
        comment = commentRepository.save(comment);
        log.info("Comment with ID: '" + comment.getId()
                + "' of user with ID: '" + userId
                + "' to item with ID: '" + itemId + "' successfully created");
        return toCommentDto(comment);
    }

    private void addBookings(ItemInfoDto itemInfoDto, List<Booking> bookings) {
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = bookings.stream()
                .filter(b -> b.getItem().getId() == itemInfoDto.getId())
                .filter(b -> b.getStatus() == Status.APPROVED)
                .filter(b -> b.getStart().isBefore(now) || b.getEnd().isBefore(now))
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);
        Booking nextBooking = bookings.stream()
                .filter(booking -> booking.getItem().getId() == itemInfoDto.getId())
                .filter(b -> b.getStatus() == Status.APPROVED)
                .filter(b -> b.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
        itemInfoDto.setLastBooking(lastBooking == null ? null : toBookingInfoDto(lastBooking));
        itemInfoDto.setNextBooking(nextBooking == null ? null : toBookingInfoDto(nextBooking));
    }

    private void addComments(ItemInfoDto itemInfoDto, List<Comment> comments) {
        List<CommentDto> result = comments.stream()
                .filter(c -> c.getItem().getId() == itemInfoDto.getId())
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemInfoDto.setComments(result);
    }
}