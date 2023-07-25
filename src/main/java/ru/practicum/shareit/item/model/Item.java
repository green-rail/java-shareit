package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(name = "sharer_id", nullable = false)
    private Long sharerId;

    @Column
    private String name;
    @Column
    private String description;
    @Column
    private boolean available;

    @Column(name = "request_id")
    private Long requestId;
}
