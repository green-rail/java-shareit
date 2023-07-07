package ru.practicum.shareit.booking;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bookings")
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;

    @Column(name = "start_date")
    private Instant start;

    @Column(name = "end_date")
    private Instant end;

    @ManyToOne()
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "booker_id")
    private Long bookerId;

    @Enumerated(EnumType.STRING)
    @Column
    private BookingStatus status;
}
