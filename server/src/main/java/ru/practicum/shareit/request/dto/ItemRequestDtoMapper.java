package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.ItemRequest;

import java.time.ZoneId;

@UtilityClass
public class ItemRequestDtoMapper {

    public ItemRequestDto toDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                null);
    }
}
