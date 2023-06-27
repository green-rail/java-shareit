package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class Item {
    @NonNull
    private final Long id;

    @NonNull
    private final Long ownerId;

    String name;
    String description;
    boolean available;
}
