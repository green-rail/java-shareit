package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class Item {
    @NonNull
    private Long id;

    @NonNull
    private final Long sharerId;

    String name;
    String description;
    boolean available;
    Long requestId;
}
