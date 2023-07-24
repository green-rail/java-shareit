package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.Instant;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByIdDesc(Long bookerId);

    List<Booking> findByBookerIdAndStatusOrderByIdDesc(Long bookerId, BookingStatus status);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByEndDesc(Long bookerId, Instant now, Instant after);

    List<Booking> findByBookerIdAndEndBeforeOrderByIdDesc(Long bookerId, Instant now);

    List<Booking> findByBookerIdAndStartAfterOrderByIdDesc(Long bookerId, Instant now);

    List<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId);

    @Query("select b " +
            "from Booking b " +
            "JOIN b.item it " +
            "where it.sharerId = ?1 " +
            "order by b.id desc")
    List<Booking> findAllByOwner(Long ownerId);

    @Query("select b " +
            "from Booking b " +
            "JOIN b.item it " +
            "where it.sharerId = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?3 " +
            "order by b.id desc")
    List<Booking> findCurrentByOwner(Long ownerId, Instant before, Instant after);

    @Query("select b " +
            "from Booking b " +
            "JOIN b.item it " +
            "where it.sharerId = ?1 " +
            "and b.end < ?2 " +
            "order by b.id desc")
    List<Booking> findPastByOwner(Long ownerId, Instant after);

    @Query("select b " +
            "from Booking b " +
            "JOIN b.item it " +
            "where it.sharerId = ?1 " +
            "and b.start > ?2 " +
            "order by b.id desc")
    List<Booking> findFutureByOwner(Long ownerId, Instant now);

    @Query("select b " +
            "from Booking b " +
            "JOIN b.item it " +
            "where it.sharerId = ?1 " +
            "and b.status = ?2 " +
            "order by b.id desc")
    List<Booking> findByOwnerAndStatus(Long ownerId, BookingStatus status);

    List<Booking> findByItemOrderByStartAsc(Item item);

}
