package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequesterId(long userId, Sort sort);

    List<ItemRequest> findByRequesterIdNot(long userId, Pageable pageable);
}