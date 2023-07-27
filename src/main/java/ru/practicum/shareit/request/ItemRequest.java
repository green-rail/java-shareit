package ru.practicum.shareit.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@Entity
@Table(name = "requests")
@NoArgsConstructor
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @Column
    private String description;

    @Column(name = "requester_id")
    private Long requesterId;

    @Column
    private Instant created = LocalDateTime.now().toInstant(ZoneOffset.UTC);
}
