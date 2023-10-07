package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    User user;
    Item itemOne;
    Item itemTwo;

    @BeforeEach
    void init() {
        user = new User(0L, "user", "user@ya.ru");
        itemOne = new Item(0L, "Дрель", "Обычная дрель", true, user, null);
        itemTwo = new Item(0L, "Отвертка", "Крестовая отвертка", false, user, null);
        userRepository.save(user);
        itemRepository.save(itemOne);
        itemRepository.save(itemTwo);
    }

    @Test
    void search_should_return_one_item() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        List<Item> items = itemRepository.search("ДРЕЛЬ", pageable);
        assertEquals(1, items.size());
        assertEquals("Дрель", items.get(0).getName());
        assertEquals("Обычная дрель", items.get(0).getDescription());
        assertEquals(true, items.get(0).getAvailable());
        assertEquals("user", items.get(0).getOwner().getName());
        assertEquals("user@ya.ru", items.get(0).getOwner().getEmail());
    }

    @Test
    void search_should_return_empty_list() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        List<Item> items = itemRepository.search("Отвертка", pageable);
        assertEquals(0, items.size());
    }

    @AfterEach
    void refresh() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}