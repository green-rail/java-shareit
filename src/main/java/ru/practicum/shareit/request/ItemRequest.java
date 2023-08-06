package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "requests")
@NoArgsConstructor
@AllArgsConstructor
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
    @CreationTimestamp
    private Instant created;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "requestId")
    private List<Item> responses = new ArrayList<>();
}
