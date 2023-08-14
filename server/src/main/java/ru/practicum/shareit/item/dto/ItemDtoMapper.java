package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.exception.ItemDtoMappingException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@UtilityClass
public class ItemDtoMapper {

    public ItemDto toDto(Item item, List<CommentDto> comments, BookingDto last, BookingDto next) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                last,
                next,
                comments,
                item.getRequestId()
        );
    }

    public Item fromDto(Long sharerId, ItemDto itemDto) throws ItemDtoMappingException {
        return new Item(
                itemDto.getId(),
                sharerId,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getRequestId()
        );
    }

    public Item updateItem(Item item, ItemDto update) {
        item.setName(update.getName() == null ? item.getName() : update.getName());
        item.setDescription(update.getDescription() == null ? item.getDescription() : update.getDescription());
        item.setAvailable(update.getAvailable() == null ? item.isAvailable() : update.getAvailable());
        return item;
    }
}
