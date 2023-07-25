package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.Instant;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByBookerIdOrderByIdDesc(Long bookerId, Pageable page);

    Page<Booking> findByBookerIdAndStatusOrderByIdDesc(Long bookerId, BookingStatus status, Pageable page);

    Page<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByEndDesc(Long bookerId, Instant now, Instant after, Pageable page);

    Page<Booking> findByBookerIdAndEndBeforeOrderByIdDesc(Long bookerId, Instant now, Pageable page);

    Page<Booking> findByBookerIdAndStartAfterOrderByIdDesc(Long bookerId, Instant now, Pageable page);

    List<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId);

    @Query("select b " +
            "from Booking b " +
            "JOIN b.item it " +
            "where it.sharerId = ?1 " +
            "order by b.id desc")
    Page<Booking> findAllByOwner(Long ownerId, Pageable page);

    @Query("select b " +
            "from Booking b " +
            "JOIN b.item it " +
            "where it.sharerId = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?3 " +
            "order by b.id desc")
    Page<Booking> findCurrentByOwner(Long ownerId, Instant before, Instant after, Pageable page);

    @Query("select b " +
            "from Booking b " +
            "JOIN b.item it " +
            "where it.sharerId = ?1 " +
            "and b.end < ?2 " +
            "order by b.id desc")
    Page<Booking> findPastByOwner(Long ownerId, Instant after, Pageable page);

    @Query("select b " +
            "from Booking b " +
            "JOIN b.item it " +
            "where it.sharerId = ?1 " +
            "and b.start > ?2 " +
            "order by b.id desc")
    Page<Booking> findFutureByOwner(Long ownerId, Instant now, Pageable page);

    @Query("select b " +
            "from Booking b " +
            "JOIN b.item it " +
            "where it.sharerId = ?1 " +
            "and b.status = ?2 " +
            "order by b.id desc")
    Page<Booking> findByOwnerAndStatus(Long ownerId, BookingStatus status, Pageable page);

    List<Booking> findByItemOrderByStartAsc(Item item);

}
