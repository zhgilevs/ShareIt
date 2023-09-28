package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {

    private Long id = 0L;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item create(Long userId, Item item) {
        item.setId(generateId());
        item.setOwnerId(userId);
        items.put(item.getId(), item);
        log.info("Item with ID: '" + item.getId() + "' from user with ID: '" + userId + "' successfully created");
        return item;
    }

    @Override
    public Optional<Item> update(Item item) {
        Item i = items.get(item.getId());
        if (item.getName() != null) {
            i.setName(item.getName());
        }
        if (item.getDescription() != null) {
            i.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            i.setAvailable(item.getAvailable());
        }
        log.info("Item with ID: '" + i.getId() + "' successfully updated");
        return Optional.of(i);
    }

    @Override
    public List<Item> getAll(Long userId) {
        var result = items.values().stream()
                .filter(item -> Objects.equals(item.getOwnerId(), userId))
                .collect(Collectors.toList());
        log.info(result.size() + " items of user with ID:'" + userId + "' found");
        return result;
    }

    @Override
    public Optional<Item> get(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> search(String text) {
        List<Item> result = new ArrayList<>();
        if (text.isBlank()) {
            return result;
        } else {
            result = items.values().stream()
                    .filter(item -> item.getAvailable() == Boolean.TRUE)
                    .filter(item ->
                            StringUtils.containsIgnoreCase(item.getName(), text)
                                    || StringUtils.containsIgnoreCase(item.getDescription(), text))
                    .collect(Collectors.toList());
        }
        log.info(result.size() + " items found by request: '" + text + "'");
        return result;
    }

    private Long generateId() {
        return ++id;
    }
}