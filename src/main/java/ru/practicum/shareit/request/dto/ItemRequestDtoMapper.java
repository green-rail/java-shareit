package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.ItemRequest;

public class ItemRequestDtoMapper {
    private ItemRequestDtoMapper(){}

    public ItemRequestDto map(ItemRequest itemRequest) {
        return new ItemRequestDto();
    }

}