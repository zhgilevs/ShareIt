package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item create(Long userId, Item item);

    Optional<Item> update(Item item);

    List<Item> getAll(Long userId);

    Optional<Item> get(Long id);

    List<Item> search(String text);
}