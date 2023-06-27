package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemDtoMapper {
    private ItemDtoMapper() {}

    public static ItemDto map(Item item) {

        return new ItemDto(item.getName(), item.getDescription(), item.isAvailable());

    }
}
