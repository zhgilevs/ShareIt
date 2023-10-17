package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(Long userId, Pageable pageable);

    @Query(value = "select it from Item as it where (upper(it.name) like upper(concat('%', ?1, '%')) or upper(it.description) like upper(concat('%', ?1, '%'))) and it.available = true")
    List<Item> search(String text, Pageable pageable);

    List<Item> findByRequestIn(List<ItemRequest> requests);

    List<Item> findByRequestId(long requestId);
}