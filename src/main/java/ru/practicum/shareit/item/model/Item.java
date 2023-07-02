package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Item {
    private Long id;

    @NonNull
    private final Long sharerId;

    private String name;
    private String description;
    private boolean available;
    private Long requestId;
}
