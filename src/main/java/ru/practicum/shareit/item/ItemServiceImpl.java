package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OwnershipException;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.ItemMapper.toItem;
import static ru.practicum.shareit.item.ItemMapper.toItemDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        checkIsUserExist(userId);
        Item item = itemRepository.create(userId, toItem(itemDto));
        return toItemDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        checkIsUserExist(userId);
        checkOwner(userId, itemId);
        itemDto.setId(itemId);
        Item item = itemRepository.update(toItem(itemDto))
                .orElseThrow(() -> new NotFoundException("Item with ID: '" + itemId + "' doesn't exist"));
        return toItemDto(item);
    }

    @Override
    public List<ItemDto> getAll(Long userId) {
        return itemRepository.getAll(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto get(Long itemId) {
        Item item = itemRepository.get(itemId)
                .orElseThrow(() -> new NotFoundException("Item with ID: '" + itemId + "' doesn't exist"));
        return toItemDto(item);
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void checkOwner(Long userId, Long itemId) {
        var a = itemRepository.get(itemId);
        if (a.isPresent()) {
            Long ownerId = a.get().getOwnerId();
            if (!Objects.equals(ownerId, userId)) {
                throw new OwnershipException("User with ID: '" + userId + "' not the owner of item with ID: '" + itemId + "'");
            }
        } else {
            throw new NotFoundException("Item with ID: '" + itemId + "' doesn't exist");
        }
    }

    private void checkIsUserExist(Long userId) {
        if (!userRepository.isUserExist(userId)) {
            throw new NotFoundException("User with ID: '" + userId + "' doesn't exist");
        }
    }
}