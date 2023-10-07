package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findByIdAndBookerId(long bookingId, long bookerId);

    Booking findByIdAndItemOwnerId(long bookingId, long itemOwnerOd);

    List<Booking> findByBookerId(long bookerId, Pageable pageable);

    @Query(value = "select b from Booking as b where b.booker.id = ?1 and b.end < current_timestamp and b.start < b.end")
    List<Booking> findByBookerIdPastState(long bookerId, Pageable pageable);

    @Query(value = "select b from Booking as b where b.booker.id = ?1 and b.start > current_timestamp and b.start < b.end")
    List<Booking> findByBookerIdFutureState(long bookerId, Pageable pageable);

    @Query(value = "select b from Booking as b where b.booker.id = ?1 and b.start < current_timestamp and b.end > current_timestamp")
    List<Booking> findByBookerIdCurrentState(long bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndStatus(long bookerId, Status status, Pageable pageable);

    List<Booking> findByItemOwnerId(long ownerId, Pageable pageable);

    @Query(value = "select b from Booking as b where b.item.owner.id = ?1 and b.end < current_timestamp and b.start < b.end")
    List<Booking> findByItemOwnerIdPastState(long ownerId, Pageable pageable);

    @Query(value = "select b from Booking as b where b.item.owner.id = ?1 and b.start > current_timestamp and b.start < b.end")
    List<Booking> findByItemOwnerIdFutureState(long ownerId, Pageable pageable);

    @Query(value = "select b from Booking as b where b.item.owner.id = ?1 and b.start < current_timestamp and b.end > current_timestamp")
    List<Booking> findByItemOwnerIdCurrentState(long ownerId, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStatus(long ownerId, Status status, Pageable pageable);

    List<Booking> findByItemId(long itemId);

    List<Booking> findByItemOwnerIdAndItemIn(long ownerId, List<Item> items, Pageable pageable);

    @Query(value = "select b from Booking as b where b.booker.id = ?1 and b.item.id = ?2 and b.status = 'APPROVED'")
    List<Booking> findByBookerIdAndItemIdAndStatusApproved(long bookerId, long itemId);
}