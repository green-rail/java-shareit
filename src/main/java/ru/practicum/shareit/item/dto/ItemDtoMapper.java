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
        if (sharerId == null || sharerId < 0) {
            throw new ItemDtoMappingException("неверный индекс владельца");
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ItemDtoMappingException("имя предмета не может быть пустым");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ItemDtoMappingException("название предмета не может быть пустым");
        }
        if (itemDto.getAvailable() == null) {
            throw new ItemDtoMappingException("доступность предмета должна быть указана");
        }

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
