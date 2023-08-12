package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByBookerId(Long bookerId, Pageable page);

    Page<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable page);

    Page<Booking> findByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime now, LocalDateTime after, Pageable page);

    Page<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime now, Pageable page);

    Page<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime now, Pageable page);

    List<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId);

    @Query("select b " +
            "from Booking b " +
            "JOIN b.item it " +
            "where it.sharerId = :ownerId " +
            "order by b.id desc")
    Page<Booking> findAllByOwner(@Param("ownerId") Long ownerId, Pageable page);

    @Query("select b " +
            "from Booking b " +
            "JOIN b.item it " +
            "where it.sharerId = :ownerId " +
            "and b.start < :start " +
            "and b.end > :end " +
            "order by b.id desc")
    Page<Booking> findCurrentByOwner(@Param("ownerId") Long ownerId,
                                     @Param("start") LocalDateTime before,
                                     @Param("end") LocalDateTime after, Pageable page);

    @Query("select b " +
            "from Booking b " +
            "JOIN b.item it " +
            "where it.sharerId = :ownerId " +
            "and b.end < :after " +
            "order by b.id desc")
    Page<Booking> findPastByOwner(@Param("ownerId") Long ownerId,
                                  @Param("after") LocalDateTime after, Pageable page);

    @Query("select b " +
            "from Booking b " +
            "JOIN b.item it " +
            "where it.sharerId = :ownerId " +
            "and b.start > :start " +
            "order by b.id desc")
    Page<Booking> findFutureByOwner(@Param("ownerId") Long ownerId,
                                    @Param("start") LocalDateTime now, Pageable page);

    @Query("select b " +
            "from Booking b " +
            "JOIN b.item it " +
            "where it.sharerId = :ownerId " +
            "and b.status = :status " +
            "order by b.id desc")
    Page<Booking> findByOwnerAndStatus(@Param("ownerId") Long ownerId,
                                       @Param("status") BookingStatus status, Pageable page);

    List<Booking> findByItemOrderByStartAsc(Item item);

}
