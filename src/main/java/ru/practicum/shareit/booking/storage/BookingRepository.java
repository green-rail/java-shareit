package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.Instant;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, Instant now, Instant after);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, Instant now);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, Instant now);


    //@Query(" select b " +
    //        "from Booking b " +
    //        "JOIN b.item_id where item_id = 1?")
    //List<Booking> findAllByOwner(Long ownerId);

    @Query("select b " +
            "from Booking b "+
            "JOIN b.item it " +
            "where it.sharerId = ?1 "+
            "order by b.start desc")
    List<Booking> findAllByOwner(Long ownerId);



}
